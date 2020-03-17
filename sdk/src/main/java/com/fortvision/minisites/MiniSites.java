package com.fortvision.minisites;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.fortvision.minisites.model.FVButton;
import com.fortvision.minisites.network.BaseCallback;
import com.fortvision.minisites.network.FVServerAPI;
import com.fortvision.minisites.network.FVServerApiFactory;
import com.fortvision.minisites.utils.Utils;
import com.fortvision.minisites.view.FVButtonActionListener;
import com.fortvision.minisites.view.FVButtonVideoView;
import com.fortvision.minisites.view.FVButtonView;
import com.fortvision.minisites.view.FVButtonViewFactory;
import com.fortvision.minisites.view.VideoEventsListener;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.util.HashSet;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MiniSites implements FVButtonActionListener, VideoEventsListener {

    private static final String LOG_TAG = "FortVision";

    private FVServerAPI serverAPI;

    private String cachedUserId;
    private String cachedUserAgent;

    private AsyncTask<FVButtonContext, Void, Void> getUserIdTask;

    private Call<FVButton> lastButtonCall;

    private FVButtonContext context;

    private Set<FVButton> clickedButtons = new HashSet<>();

    public static MiniSites INSTANCE;

    private LocationManager lm;

    private MiniSites() {
        serverAPI = FVServerApiFactory.create();
        ButtonViewController.get().setListener(this);
        ButtonViewController.get().setVideoEventsListener(this);
    }

    private static MiniSites get() {
        if (INSTANCE == null)
            INSTANCE = new MiniSites();
        return INSTANCE;
    }

    public String getCachedUserId() {
        return cachedUserId;
    }

    public String getCachedUserAgent() {
        return cachedUserAgent;
    }

    public static void setAllowedLocationGathering(@NonNull Context context, boolean allowed) {
        UserLocationUpdater.setLocationGatheringAllowed(context, allowed);
    }

    public static void trigger(@NonNull Activity activity, @NonNull String publisherId) {
        trigger(activity, publisherId, null);
    }

    public static void trigger(@NonNull Activity activity, @NonNull String publisherId,
                               @Nullable String categoryId) {
        get().clearButtonImpl();
        get().triggerInternal(activity, publisherId, categoryId);
        UserLocationUpdater.start(activity);
    }

    private void triggerInternal(@NonNull Activity activity, @NonNull String publisherId,
                                 @Nullable String categoryId) {
        //Removed remote assets loading till further notice
        //Assets.verifyLoaded();
        getLocation(activity);

        if (cachedUserAgent == null)
            cachedUserAgent = Utils.getUserAgent(activity);
        context = new FVButtonContext(activity, publisherId, categoryId, cachedUserAgent);
        if (cachedUserId == null) {
            if (getUserIdTask != null)
                getUserIdTask.cancel(true);
            getUserIdTask = new AsyncTask<FVButtonContext, Void, Void>() {
                @Override
                protected Void doInBackground(FVButtonContext... params) {
                    FVButtonContext context = params[0];
                    if (context == null)
                        return null;

                    Activity activity = context.getActivity();
                    if (activity == null)
                        return null;

                    cachedUserId = getAdvertisingId(activity);
                    context.setUserId(cachedUserId);
                    getUserIdTask = null;
                    retrieveAndShowButton(context);
                    return null;
                }
            };
            getUserIdTask.execute(context);
        } else {
            context.setUserId(cachedUserId);
            retrieveAndShowButton(context);
        }
        Utils.setDisplayMetrics(context.getMetrics(), context.getMetrics().density);

    }

    @SuppressWarnings({"MissingPermission"})
    private void getLocation(Context context) {
        UserLocationListener userLocationListener = new UserLocationListener(context);
        if (lm == null) {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (!(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED))
                return;
            lm.requestLocationUpdates(lm.GPS_PROVIDER, 5 * 60 * 1000, 0, userLocationListener);
        }
    }

    private void reportBattery(@NonNull final FVButtonContext context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent battery = context.getActivity().registerReceiver(null, ifilter);

        int status = battery.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        int charging = isCharging ? 1 : 0;

        int batteryLevel = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        Call<ResponseBody> call = serverAPI.reportBattery(context.getUserId(), Utils.getDeviceIpAsStr(), context.getUserAgent(),
                charging, batteryLevel);
        call.enqueue(new BaseCallback<ResponseBody>());
    }

    private void retrieveAndShowButton(@NonNull final FVButtonContext context) {

        reportBattery(context);

        if (lastButtonCall != null)
            lastButtonCall.cancel();
        lastButtonCall = serverAPI.getButton(context.getPublisherId(), context.getUserId(), "application/x-www-form-urlencoded; charset=UTF-8",
                context.getUserAgent(), "{\"batteryLevel\":null,\"isCharging\":null}", "true", "https://www.fortvision.com/demo/?fv-c=143707",
                1, Integer.parseInt(context.getCategoryId())/*146874*/, 0);
        lastButtonCall.enqueue(new Callback<FVButton>() {
            @Override
            public void onResponse(@NonNull Call<FVButton> call, @NonNull Response<FVButton> response) {
                if (call.isCanceled())
                    return;

                FVButton body = response.body();
                if (response.isSuccessful() && body != null) {
                    FVButtonView buttonView = FVButtonViewFactory.create(context, body);
                    if (buttonView == null)
                        return;

                    ButtonViewController.get().startManageButton(context, buttonView, body);
                } else
                    onFailure(call, new Exception("Failed to retrieve FV Button"));
            }

            @Override
            public void onFailure(@NonNull Call<FVButton> call, @NonNull Throwable t) {
                Log.e(LOG_TAG, "Failed to retrieve FV Button");
                t.printStackTrace();
            }
        });
    }

    private void clearButtonImpl() {
        if (lastButtonCall != null)
            lastButtonCall.cancel();
        ButtonViewController.get().removeCurrentButton(false);
        if (context != null)
            context.destroy();
        context = null;
    }

    public static void clearButton() {
        get().clearButtonImpl();
    }

    @WorkerThread
    private String getAdvertisingId(@NonNull Context context) {
        AdvertisingIdClient.Info idInfo;
        try {
            idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            if (idInfo != null) {
                String id = idInfo.getId();
                Prefs.setAdvertisingId(context, id);
                return id;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public void onFinishedLoadingData(@NonNull FVButtonView buttonView) {
    }

    @Override
    public void onButtonClick(@NonNull FVButton button) {
        if (clickedButtons.contains(button))
            return;

        clickedButtons.add(button);
        Call<ResponseBody> call = serverAPI.reportClick(button.getCampaignId(), String.valueOf(button.getDesignId()), cachedUserId, Utils.getDeviceIpAsStr(), cachedUserAgent);
        call.enqueue(new BaseCallback<ResponseBody>());
    }

    @Override
    public void onButtonDismissed(@NonNull FVButton button, @DismissType int dismissType) {
        Call<ResponseBody> call = serverAPI.reportTrash(context.getPublisherId(), button.getCampaignId(), String.valueOf(button.getDesignId()), dismissType, cachedUserId, Utils.getDeviceIpAsStr(), cachedUserAgent);
        call.enqueue(new BaseCallback<ResponseBody>());
    }

    @Override
    public void onButtonVisible(@NonNull FVButton button) {
        Call<ResponseBody> call = serverAPI.reportImpression(button.getCampaignId(), String.valueOf(button.getDesignId()), cachedUserId, Utils.getDeviceIpAsStr(), cachedUserAgent);
        call.enqueue(new BaseCallback<ResponseBody>());
    }

    @Override
    public void onVideoEvent(@NonNull FVButtonVideoView view, @NonNull @VideoEvent String eventType) {
        FVButton button = view.getButton();
        if (eventType.equals(VideoEvent.VideoRemoved)) {
            Call<ResponseBody> call = serverAPI.reportVideoStats(button.getCampaignId(), String.valueOf(button.getDesignId()), cachedUserId, Utils.getDeviceIpAsStr(), cachedUserAgent,
                    view.isBigMode() ? "big-video" : "small-video", eventType, view.getSecSinceStart(), view.getCurrentPosInVideo(), 0, 0);
            call.enqueue(new BaseCallback<ResponseBody>());
        } else {
            Call<ResponseBody> call = serverAPI.reportVideoEvent(button.getCampaignId(), String.valueOf(button.getDesignId()), cachedUserId, Utils.getDeviceIpAsStr(), cachedUserAgent,
                    view.isBigMode() ? "big-video" : "small-video", eventType, view.getSecSinceStart(), view.getCurrentPosInVideo());
            call.enqueue(new BaseCallback<ResponseBody>());
        }
    }

    @Override
    public void onMinimizeVideo(@NonNull FVButtonVideoView view) {
    }
}

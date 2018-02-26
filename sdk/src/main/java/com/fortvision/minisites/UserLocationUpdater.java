package com.fortvision.minisites;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.fortvision.minisites.network.BaseCallback;
import com.fortvision.minisites.network.FVServerAPI;
import com.fortvision.minisites.network.FVServerApiFactory;
import com.fortvision.minisites.utils.Utils;

import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * This class is responsible for register location request, receiving the user location from the
 * system and reporting it back to the server.
 */

public class UserLocationUpdater extends BroadcastReceiver {

    @SuppressWarnings("EmptyCatchBlock")
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)) {
                Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
                //Log.i("onLocationReceive", location.getLongitude() + " " + location.getLatitude());
                reportUserLocation(context, location);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FORTVISION", e.getMessage());
        }
    }

    public static void reportUserLocation(@NonNull Context context, @NonNull Location location) {
        String advertisingId = Prefs.getStoredAdvertisingId(context);
        if (advertisingId == null)
            return;

        FVServerAPI serverAPI = FVServerApiFactory.create();
        Call<ResponseBody> call = serverAPI.reportLocation(advertisingId, Utils.getDeviceIpAsStr(), Utils.getUserAgent(context),
                String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        call.enqueue(new BaseCallback<ResponseBody>());
    }

    @SuppressWarnings({"MissingPermission"})
    private static void setLocationGathering(@NonNull Context context, boolean isActive) {
        try {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // UserLocationListener userLocationListener = new UserLocationListener(context);
            if (!isActive) {
                lm.removeUpdates(getPendingIntent(context));
                Prefs.setLocationGathering(context, false);
            } else if (!Prefs.isLocationGatheringOn(context)
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                lm.requestLocationUpdates(TimeUnit.HOURS.toMillis(1), 0, criteria, getPendingIntent(context));
                //lm.requestLocationUpdates(lm.GPS_PROVIDER, 1 * 60 * 1000, 0, userLocationListener);
                Prefs.setLocationGathering(context, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FORTVISION", e.getMessage());
        }
    }

    static void start(@NonNull Context context) {
        setLocationGathering(context, Prefs.isLocationGatheringAllowed(context, true));
    }

    private static PendingIntent getPendingIntent(Context context) {
        return PendingIntent.getBroadcast(context, 745, new Intent(context, UserLocationUpdater.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void setLocationGatheringAllowed(@NonNull Context context, boolean allowed) {
        Prefs.setLocationGatheringAllowed(context, allowed);
        start(context);
    }
}

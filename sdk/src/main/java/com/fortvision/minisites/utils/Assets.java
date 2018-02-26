package com.fortvision.minisites.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.fortvision.minisites.R;
import com.fortvision.minisites.network.FVServerApiFactory;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Assets {

    private static volatile boolean IS_LOADED = false;

    private static String TRASH_ICON;

    private static String CLOSE_POPUP_ICON;

    private static String CLOSE_BUTTON_ICON;

    private static String VIDEO_CONTROL_MINIMIZE;

    private static String VIDEO_CONTROL_BACK;

    private static String VIDEO_CONTROL_FWD;

    private static String VIDEO_CONTROL_FULL_SCREEN;

    private static String VIDEO_CONTROL_PAUSE;

    private static String VIDEO_CONTROL_PLAY;

    public static void loadTrashImage(@NonNull ImageView target) {
        Utils.loadImage(target, TRASH_ICON, R.drawable.fv_minisites_trash);
    }

    public static void loadPopupCloseImage(@NonNull ImageView target) {
        Utils.loadImage(target, CLOSE_POPUP_ICON, R.drawable.fv_minisites_close_popup);
    }

    public static void loadButtonCloseImage(@NonNull ImageView target) {
        Utils.loadImage(target, CLOSE_BUTTON_ICON, R.drawable.fv_minisites_close_image);
    }

    public static void loadVideoControlMinimize(@NonNull ImageView target) {
        Utils.loadImage(target, VIDEO_CONTROL_MINIMIZE, R.drawable.fv_minisites_closebigvideo);
    }

    public static void loadVideoControlPrev(@NonNull ImageView target) {
        Utils.loadImage(target, VIDEO_CONTROL_BACK, R.drawable.fv_minisites_trash);
    }

    public static void loadVideoControlFwd(@NonNull ImageView target) {
        Utils.loadImage(target, VIDEO_CONTROL_FWD, R.drawable.fv_minisites_trash);
    }

    public static void loadVideoControlFull(@NonNull ImageView target) {
        Utils.loadImage(target, VIDEO_CONTROL_FULL_SCREEN, R.drawable.fv_minisites_trash);
    }

    public static void loadVideoControlPause(@NonNull ImageView target) {
        loadVideoControlPause(target.getContext(), new DrawableImageViewTarget(target));
    }

    public static void loadVideoControlPlay(@NonNull ImageView target) {
        loadVideoControlPlay(target.getContext(), new DrawableImageViewTarget(target));
    }

    public static void loadVideoControlPause(@NonNull Context context, @NonNull Target<Drawable> target) {
        Utils.loadImage(context, target, VIDEO_CONTROL_PAUSE, R.drawable.fv_minisites_pause);
    }

    public static void loadVideoControlPlay(@NonNull Context context, @NonNull Target<Drawable> target) {
        Utils.loadImage(context, target, VIDEO_CONTROL_PLAY, R.drawable.fv_minisites_play);
    }

    public static void verifyLoaded() {
        if (IS_LOADED)
            return;

        Call<JsonObject> call = FVServerApiFactory.create().getImages();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (call.isCanceled())
                    return;

                JsonObject body = response.body();
                if (response.isSuccessful() && body != null) {
                    TRASH_ICON = Utils.getJsonElementAsString(body.get("trash"), null);
                    CLOSE_POPUP_ICON = Utils.getJsonElementAsString(body.get("popup_close"), null);
                    CLOSE_BUTTON_ICON = Utils.getJsonElementAsString(body.get("dismiss"), null);
                    VIDEO_CONTROL_MINIMIZE = Utils.getJsonElementAsString(body.get("video_close_big_video"), null);
                    VIDEO_CONTROL_BACK = Utils.getJsonElementAsString(body.get("video_back"), null);
                    VIDEO_CONTROL_FWD = Utils.getJsonElementAsString(body.get("video_forward"), null);
                    VIDEO_CONTROL_FULL_SCREEN = Utils.getJsonElementAsString(body.get("video_full_screen"), null);
                    VIDEO_CONTROL_PAUSE = Utils.getJsonElementAsString(body.get("video_pause"), null);
                    VIDEO_CONTROL_PLAY = Utils.getJsonElementAsString(body.get("video_play"), null);
                    IS_LOADED = true;
                } else
                    onFailure(call, new Exception("Failed to retrieve sdk image assets"));

            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

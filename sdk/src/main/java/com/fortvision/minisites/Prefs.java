package com.fortvision.minisites;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Serialized data variables stored in {@link android.content.SharedPreferences}
 */

public class Prefs {

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("fv_minisites_prefs", Context.MODE_PRIVATE);
    }

    static void setAdvertisingId(@NonNull Context context, @NonNull String advertisingId) {
        getPrefs(context).edit().putString("advertising_id", advertisingId).apply();
    }

    @Nullable
    static String getStoredAdvertisingId(@NonNull Context context) {
        return getPrefs(context).getString("advertising_id", null);
    }

    static void setLocationGathering(@NonNull Context context, boolean on) {
        getPrefs(context).edit().putBoolean("location_gathering_on", on).apply();
    }

    static boolean isLocationGatheringOn(@NonNull Context context) {
        return getPrefs(context).getBoolean("location_gathering_on", false);
    }

    static boolean isLocationGatheringAllowed(Context context, boolean allowedDefault) {
        return getPrefs(context).getBoolean("location_gathering_allowed", allowedDefault);
    }

    static void setLocationGatheringAllowed(Context context, boolean allowed) {
        getPrefs(context).edit().putBoolean("location_gathering_allowed", allowed).apply();
    }
}

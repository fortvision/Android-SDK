package com.fortvision.minisites;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * This class is responsible for listening user location changes.
 */

public class UserLocationListener implements LocationListener {
    Context context;

    UserLocationListener(Context context) {
        this.context = context;
    }

    public void onLocationChanged(Location location) {
        //Log.i("onLocationChanged", location.getLongitude() + " " + location.getLatitude());
        UserLocationUpdater.reportUserLocation(context, location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
}

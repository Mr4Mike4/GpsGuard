package ru.max314.gpsguard;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import ru.max314.util.LogHelper;

/**
 * Created by max on 16.01.2015.
 */
public class LocationFixListenerProcessing implements LocationListener, GpsStatus.Listener {
    private static LogHelper Log = new LogHelper(LocationFixListenerProcessing.class);
    private LocationManager locationManager = null;
    private Object owner;

    public LocationFixListenerProcessing(Object owner, LocationManager locationManager) {
        this.locationManager = locationManager;
        this.owner = owner;
    }


    public void up() {
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationManager.addGpsStatusListener(this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }


    public void down() {
        locationManager.removeUpdates(this);
        locationManager.removeGpsStatusListener(this);
    }


    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                Log.d("GPS статус - GPS_EVENT_STARTED");
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                Log.d("GPS статус - GPS_EVENT_FIRST_FIX");
                AppModel.getInstance().fixIndependencyLocation(owner);
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                Log.d("GPS статус - GPS_EVENT_STOPPED");
                break;
        }
    }



    /**
     * Called when the location has changed.
     * <p/>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
    }


    /**
     * Called when the provider status changes. This method is called when
     * a provider is unable to fetch a location or if the provider has recently
     * become available after a period of unavailability.
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(String.format("onStatusChanged: provider %s", provider));
    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {
        Log.d("onProviderEnabled: provider "+provider);
    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {
        Log.d("onProviderDisabled: provider "+provider);
    }
}


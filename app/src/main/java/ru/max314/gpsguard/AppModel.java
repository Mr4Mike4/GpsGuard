package ru.max314.gpsguard;

import android.content.Context;
import android.location.Location;
import android.os.Handler;

import java.util.Date;
import java.util.Objects;


/**
 * Created by max on 16.01.2015.
 */
public class AppModel {
    static AppModel self;

    private Location currentLocation;
    private Date currentLocationDate;
    private Date currentLocationDateFix;
    private Date checkLocationDateFix;
    LocationService locationService;
    LocationFixService locationFixService;
    Handler fixListenerStartHandler;
    Object guard = new Object();

    private static void initModel() {
        self = new AppModel();
    }

    public AppModel() {

    }

    public static synchronized AppModel getInstance() {
        if (self == null) {
            initModel();
        }
        return self;
    }

    public synchronized Location getCurrentLocation() {
        return currentLocation;
    }

    public synchronized void setCurrentLocation(Object owner, Location currentLocation) {
        if (owner != locationService)
            return;
        currentLocationDate = new Date();
        this.currentLocation = currentLocation;
    }

    public synchronized void fixCurrentLocation(Object owner) {
        if (owner != locationService)
            return;
        currentLocationDateFix = new Date();

    }

    public synchronized void fixIndependencyLocation(Object owner) {
        if (owner != locationFixService)
            return;
        checkLocationDateFix = new Date();
    }

    public synchronized void start() {
        if (locationService != null) {
            return;
        }
        locationService = new LocationService((android.location.LocationManager) App.getInstance().getSystemService(Context.LOCATION_SERVICE));
        locationService.start();
        locationFixService = new LocationFixService((android.location.LocationManager) App.getInstance().getSystemService(Context.LOCATION_SERVICE));
        locationFixService.start();

    }

}

package ru.max314.gpsguard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import ru.max314.util.threads.TimerHelper;


/**
 * Created by max on 16.01.2015.
 */
public class AppModel {
    static AppModel self;

    private Location currentLocation;
    private Date currentLocationDate;
    private Date currentLocationDateFix;
    private Date checkLocationDateFix;
    private LocationService locationService;
    private LocationFixService locationFixService;
    private boolean isShowActivityOnTrouble = false;
    private boolean isCloseActivityOnClick = false;

    private Date verifyDate;
    private String verifyString="";


    Handler fixListenerStartHandler;
    Object guard = new Object();

    private static final String MODEL_PREF = "MODEL_PREF";
    private static final String MODEL_SHOWACTIVITY = "MODEL_SHOWACTIVITY";
    private static final String MODEL_CLOSEACTIVITY = "CLOSE_SHOWACTIVITY";

    private synchronized static void initModel() {
        self = new AppModel();
        SharedPreferences pref = App.getInstance().getSharedPreferences(MODEL_PREF, App.MODE_PRIVATE);
        self.isShowActivityOnTrouble = pref.getBoolean(MODEL_SHOWACTIVITY, false);
        self.isCloseActivityOnClick = pref.getBoolean(MODEL_CLOSEACTIVITY, false);
    }

    public AppModel() {
        TimerHelper dateChangerWatcher = new TimerHelper("каждые пол минуты проверяем",
                TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES), // Начинаем через миуту
                TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS), // каждые полминуты
                new Runnable() {
                    @Override
                    public void run() {
                        verify();
                        showActivity();
                    }
                });
        dateChangerWatcher.start();
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

    public synchronized Date getCurrentLocationDate() {
        return currentLocationDate;
    }

    public synchronized Date getCurrentLocationDateFix() {
        return currentLocationDateFix;
    }

    public synchronized Date getCheckLocationDateFix() {
        return checkLocationDateFix;
    }

    public synchronized boolean isShowActivityOnTrouble() {
        return isShowActivityOnTrouble;
    }

    public synchronized void setShowActivityOnTrouble(boolean isShowActivityOnTrouble) {
        this.isShowActivityOnTrouble = isShowActivityOnTrouble;
        SharedPreferences pref = App.getInstance().getSharedPreferences(MODEL_PREF, App.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(MODEL_SHOWACTIVITY, this.isShowActivityOnTrouble);
        edit.commit();
    }

    public synchronized boolean isCloseActivityOnClick() {
        return isCloseActivityOnClick;
    }

    public synchronized void setCloseActivityOnClick(boolean isCloseActivityOnClick) {
        this.isCloseActivityOnClick = isCloseActivityOnClick;
        SharedPreferences pref = App.getInstance().getSharedPreferences(MODEL_PREF, App.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(MODEL_CLOSEACTIVITY, this.isCloseActivityOnClick);
        edit.commit();
    }

    public Date getVerifyDate() {
        return verifyDate;
    }

    public String getVerifyString() {
        return verifyString;
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

    /**
     * Выполнить верификацию
     */
    public synchronized void verify() {
        verifyDate = new Date();
        verifyString = "";
        // 1/ нет данных о местоположении
        Date dt = getCurrentLocationDate();
        if (dt==null){
            verifyString = "Нет данных о местоположении";
            return;
        }
        // не было обновления местоположения 5 сек
        long delta = Math.abs(verifyDate.getTime() - dt.getTime());
        long min = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS);
        if (delta>min){
            verifyString = "Местоположение не получалось в течении 5 секунд";
            return;
        }
        dt = getCheckLocationDateFix();
        if (dt!=null)
        {
            // не было фикса за минуту
            delta = Math.abs(verifyDate.getTime() - dt.getTime());
            min = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
            if (delta>min){
                verifyString = "В течении минуты не было фиксации спутников";
                return;
            }
        }
    }

    public void showActivity(){
        if (verifyString!=null && verifyString.length()>0){
            if (isShowActivityOnTrouble()){
                Intent intent = new Intent(App.getInstance(),MainActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                App.getInstance().startActivity(intent);
            }
        }
    }

}

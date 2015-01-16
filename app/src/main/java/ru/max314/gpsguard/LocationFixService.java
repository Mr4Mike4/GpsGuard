package ru.max314.gpsguard;

import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.TimeUtils;

import java.util.concurrent.TimeUnit;

import ru.max314.util.LogHelper;
import ru.max314.util.SpeechUtils;

/**
 * Created by max on 16.01.2015.
 */
public class LocationFixService extends Thread {
    static LogHelper Log = new LogHelper(LocationFixService.class);
    LocationFixListenerProcessing locationListenerProcessing = null;
    LocationManager locationManager;
    private Handler handler;
    private Object lock = new Object();
    private boolean exit = false;


    public LocationFixService(LocationManager locationManager) {
        super("LocationFixThread");
        this.locationManager = locationManager;
        Log.d("LocationFixService");
    }


    @Override
    public void run() {
        try {
            Log.d("run");
//            SpeechUtils.speech("Запуск потока отслеживание Fix - местоположения", false);
            Looper.prepare();
            synchronized (lock) {
                handler = new Handler();
            }
            if (exit){
                Looper.myLooper().quit();
                return;
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    processingUp();
                }
            }, TimeUnit.MILLISECONDS.convert(3, TimeUnit.SECONDS));

            Looper.loop();
            Log.d("exit");
        } catch (Exception e) {
            Log.e("LoopingThread", e);
            if (locationListenerProcessing!=null)
                locationListenerProcessing.down();
        }
    }

    private void processingUp() {
        locationListenerProcessing = new LocationFixListenerProcessing(this, locationManager);
        locationListenerProcessing.up();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                processingDown();
            }
        }, TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS));
    }

    private void processingDown() {
        locationListenerProcessing.down();
        locationListenerProcessing = null;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                processingUp();
            }
        }, TimeUnit.MILLISECONDS.convert(3, TimeUnit.SECONDS));
    }

    /**
     * Остановить
     */
    public void tryStop() {
        Log.d("tryStop");
        synchronized (lock) {
            exit = true;
            if (handler != null)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (locationListenerProcessing!=null){
                            locationListenerProcessing.down();
                            locationListenerProcessing = null;
                        }
                        Looper.myLooper().quit();
                    }
                });
        }
    }

}



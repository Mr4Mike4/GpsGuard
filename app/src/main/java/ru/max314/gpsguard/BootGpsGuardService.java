package ru.max314.gpsguard;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import ru.max314.util.LogHelper;

/**
 * Created by max on 16.01.2015.
 */
public class BootGpsGuardService extends IntentService {
    public static final String ACTION_START = "ru.max314.gpsguard.bootservice.START";
    protected static LogHelper Log = new LogHelper(BootGpsGuardService.class);

    public static void start(Context context) {
        Intent intent = new Intent(context, BootGpsGuardService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }


    public BootGpsGuardService() {
        super("gpsguard.bootservice");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                onStartup();
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void onStartup() {
        AppModel.getInstance().start();
    }
}

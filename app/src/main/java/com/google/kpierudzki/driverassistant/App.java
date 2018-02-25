package com.google.kpierudzki.driverassistant;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.google.kpierudzki.driverassistant.service.AssistantServiceNew;
import com.google.kpierudzki.driverassistant.service.connector.IConnectorSelectable;
import com.google.kpierudzki.driverassistant.service.database.AssistantDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Kamil on 26.06.2017.
 */

public class App extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    public final static String APP_IN_BACKGROUND_ACTION = "APP_IN_BACKGROUND_ACTION";
    public final static String APP_IN_FOREGROUND_ACTION = "APP_IN_FOREGROUND_ACTION";

    private static App instance;

    /**
     * Domyślne zdarzenie dla nieprzechwyconych wyjątków.
     */
    private Thread.UncaughtExceptionHandler defaultExceptionHandler;
    private final static String EXCEPTIONS_FILE_NAME = "exceptions.txt";

    private AssistantDatabase database, demoDatabase;
    private AppLifecycleDetector lifecycleDetector;
    private AssistantServiceNew assistantService;

    private Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
    }

    private void init() {
        appContext = instance.getApplicationContext();
        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
        database = Room.databaseBuilder(appContext, AssistantDatabase.class, AssistantDatabase.DATABASE_FILENAME).build();
        demoDatabase = Room.databaseBuilder(appContext, AssistantDatabase.class, AssistantDatabase.DEMO_DATABASE_FILENAME).build();
        lifecycleDetector = new AppLifecycleDetector();
        registerActivityLifecycleCallbacks(this);
    }

    public static void restartApp(boolean demoModeEnabled) {
        int pendingRequestId = 1102;
        Intent launcherActivity = new Intent(instance.appContext, MainActivity.class);
        launcherActivity.putExtra(MainActivity.DEMO_MODE_KEY, demoModeEnabled);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                instance.appContext,
                pendingRequestId,
                launcherActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)
                instance.appContext.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(
                    AlarmManager.RTC,
                    System.currentTimeMillis() + 100,
                    pendingIntent);
            System.exit(0);
        }
    }

    private void initService() {
        assistantService = new AssistantServiceNew();
    }

    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            File file = new File(appContext.getExternalFilesDir(null), EXCEPTIONS_FILE_NAME);
            try {
                PrintStream printStream = new PrintStream(new FileOutputStream(file, true), true, "UTF-8");
                ex.printStackTrace(printStream);
                printStream.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (defaultExceptionHandler != null) {
                defaultExceptionHandler.uncaughtException(thread, ex);
            }
        }
    };

    public static Context getAppContext() {
        return instance.appContext;
    }

    public static AssistantDatabase getDatabase() {
        if (GlobalConfig.DEMO_MODE) return instance.demoDatabase;
        else return instance.database;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        if (activity.getClass() == MainActivity.class) {
            lifecycleDetector.onMainActivityCreated();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity.getClass() == MainActivity.class) {
            lifecycleDetector.onMainActivityStarted();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity.getClass() == MainActivity.class) {
            lifecycleDetector.onMainActivityDestroyed();
        }
    }

    public static IConnectorSelectable getServiceConnector() {
        if (instance.assistantService == null) instance.initService();
        return instance.assistantService;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
            instance.lifecycleDetector.onAppInBackground();
    }
}

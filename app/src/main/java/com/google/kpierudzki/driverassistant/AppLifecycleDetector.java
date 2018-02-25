package com.google.kpierudzki.driverassistant;

import android.content.Intent;

import com.google.kpierudzki.driverassistant.service.AssistantService;

/**
 * Created by Kamil on 26.12.2017.
 */

class AppLifecycleDetector {

    private boolean appInBackground = false;
    private boolean mainActivityDestroyed = false;

    private void checkApplicationStateAndSendBroadcast() {
        if (appInBackground) {
            if (mainActivityDestroyed)
                App.getAppContext().stopService(new Intent(App.getAppContext(), AssistantService.class));
            else
                App.getAppContext().sendBroadcast(new Intent(App.APP_IN_BACKGROUND_ACTION));
        } else
            App.getAppContext().sendBroadcast(new Intent(App.APP_IN_FOREGROUND_ACTION));
    }

    void onMainActivityStarted() {
        appInBackground = false;
        App.getAppContext().startService(new Intent(App.getAppContext(), AssistantService.class));
        checkApplicationStateAndSendBroadcast();
    }

    void onMainActivityCreated() {
        mainActivityDestroyed = false;
    }

    void onMainActivityDestroyed() {
        mainActivityDestroyed = true;
        checkApplicationStateAndSendBroadcast();
    }

    void onAppInBackground() {
        appInBackground = true;
        checkApplicationStateAndSendBroadcast();
    }
}

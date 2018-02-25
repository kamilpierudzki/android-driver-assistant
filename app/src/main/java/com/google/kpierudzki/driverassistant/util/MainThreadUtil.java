package com.google.kpierudzki.driverassistant.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Kamil on 26.06.2017.
 */

public class MainThreadUtil {

    private static Handler uiHandler = new Handler(Looper.getMainLooper());

    public static void post(Runnable runnable) {
        uiHandler.post(runnable);
    }

    public static void postDelayed(Runnable runnable, long millis) {
        uiHandler.postDelayed(runnable, millis);
    }
}

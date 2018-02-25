package com.google.kpierudzki.driverassistant.service.helper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import com.google.kpierudzki.driverassistant.common.IDestroyable;

/**
 * Created by Kamil on 28.06.2017.
 */

public abstract class BackgroundWorkBasedHelper implements IDestroyable {

    protected Handler backgroundHandler;

    public BackgroundWorkBasedHelper() {
        HandlerThread handlerThread = new HandlerThread("background_worker_thread", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    public void onDestroy() {
        if (backgroundHandler != null) {
            backgroundHandler.removeCallbacksAndMessages(null);
            backgroundHandler.getLooper().quit();
            backgroundHandler = null;
        }
    }
}

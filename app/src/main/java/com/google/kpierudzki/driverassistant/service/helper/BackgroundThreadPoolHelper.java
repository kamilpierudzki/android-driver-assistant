package com.google.kpierudzki.driverassistant.service.helper;

import com.google.kpierudzki.driverassistant.common.IDestroyable;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by kamilpierudzki on 02/11/2017.
 */

public class BackgroundThreadPoolHelper implements IDestroyable {

    protected ThreadPoolExecutor threadPool;

    public BackgroundThreadPoolHelper() {
        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        threadPool.setKeepAliveTime(10, TimeUnit.SECONDS);
        threadPool.allowCoreThreadTimeOut(true);
    }

    @Override
    public void onDestroy() {
        threadPool.shutdownNow();
        threadPool = null;
    }
}

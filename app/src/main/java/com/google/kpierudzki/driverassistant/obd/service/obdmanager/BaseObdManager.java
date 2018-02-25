package com.google.kpierudzki.driverassistant.obd.service.obdmanager;

import com.google.kpierudzki.driverassistant.common.IDestroyable;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Kamil on 15.09.2017.
 */

public class BaseObdManager implements IDestroyable {

    protected ThreadPoolExecutor threadPool;

    public BaseObdManager(ThreadPoolExecutor threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public void onDestroy() {
        threadPool = null;
    }
}

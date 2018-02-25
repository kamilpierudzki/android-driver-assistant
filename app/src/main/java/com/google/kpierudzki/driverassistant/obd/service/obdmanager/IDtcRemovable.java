package com.google.kpierudzki.driverassistant.obd.service.obdmanager;

import android.support.annotation.MainThread;

/**
 * Created by Kamil on 27.12.2017.
 */

public interface IDtcRemovable {
    @MainThread
    public void clearAllDtc();
}

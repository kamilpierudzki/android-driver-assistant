package com.google.kpierudzki.driverassistant.obd.service.obdmanager;

import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.service.helper.Callbacks;

/**
 * Created by Kamil on 16.09.2017.
 */

public interface IObdCommonListener extends Callbacks {
    void onConnectionStateChanged(ObdManager.ConnectionState state);

    void onDeviceMalfunction();
}

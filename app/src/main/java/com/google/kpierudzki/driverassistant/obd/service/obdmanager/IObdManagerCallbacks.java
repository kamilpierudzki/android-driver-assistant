package com.google.kpierudzki.driverassistant.obd.service.obdmanager;

import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */

public interface IObdManagerCallbacks {
    void onConnectionStateChanged(ObdManager.ConnectionState state);

    void onNewObdData(ObdCommandModel data);

    boolean isManagerWorking();

    void onConnectionInfo(String info);

    void onDeviceMalfunction();
}

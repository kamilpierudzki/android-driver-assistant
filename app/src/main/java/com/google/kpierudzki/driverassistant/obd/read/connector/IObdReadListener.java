package com.google.kpierudzki.driverassistant.obd.read.connector;

import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.IObdCommonListener;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

/**
 * Created by Kamil on 16.09.2017.
 */

public interface IObdReadListener extends IBaseManager.IBaseManagerListener, IObdCommonListener {
    void onNewObdData(ObdCommandModel data);

    void onNewDtcDetected();
}

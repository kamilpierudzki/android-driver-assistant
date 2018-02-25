package com.google.kpierudzki.driverassistant.obd.read.connector;

import com.google.kpierudzki.driverassistant.obd.service.obdmanager.IObdCommonObservable;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

/**
 * Created by Kamil on 16.09.2017.
 */

public interface IObdReadObservable extends IBaseManager.IBaseManagerObservable, IObdCommonObservable {
    void forcePersistBuffer(boolean async);
    void clearAllDtc();
    boolean dtcDetected();
}

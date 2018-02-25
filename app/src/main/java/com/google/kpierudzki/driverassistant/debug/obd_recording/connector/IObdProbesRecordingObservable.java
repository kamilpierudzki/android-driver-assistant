package com.google.kpierudzki.driverassistant.debug.obd_recording.connector;

import com.google.kpierudzki.driverassistant.obd.service.obdmanager.IObdCommonObservable;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

/**
 * Created by Kamil on 17.09.2017.
 */

public interface IObdProbesRecordingObservable extends IBaseManager.IBaseManagerObservable,
        IObdCommonObservable {
    void startRecord();

    void stopRecord();

    @Deprecated
    void forcePersistBuffer();
}

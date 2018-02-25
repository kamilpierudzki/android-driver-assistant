package com.google.kpierudzki.driverassistant.debug.obd_recording.connector;

import com.google.kpierudzki.driverassistant.debug.obd_recording.ObdProbesRecordingContract;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.IObdCommonListener;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

/**
 * Created by Kamil on 17.09.2017.
 */

public interface IObdProbesRecordingListener extends
        IBaseManager.IBaseManagerListener,
        IObdCommonListener {
    void onNewObdProbesRecordStatus(ObdProbesRecordingContract.RecordStatus status);

    void onNewSamplesCount(ObdParamType paramType, int count);
}

package com.google.kpierudzki.driverassistant.debug.gps_recording.connector;

import com.google.kpierudzki.driverassistant.debug.gps_recording.GpsProbesRecordingContract;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

/**
 * Created by Kamil on 28.06.2017.
 */

public interface IGpsProbesRecordingListener extends IBaseManager.IBaseManagerListener {
    void onNewRecordStatus(GpsProbesRecordingContract.RecordStatus status);
    void onNewSamplesCount(int count);
}

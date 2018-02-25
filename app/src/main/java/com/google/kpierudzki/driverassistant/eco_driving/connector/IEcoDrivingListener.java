package com.google.kpierudzki.driverassistant.eco_driving.connector;

import com.google.kpierudzki.driverassistant.eco_driving.EcoDrivingContract;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

/**
 * Created by Kamil on 03.07.2017.
 */

public interface IEcoDrivingListener extends IBaseManager.IBaseManagerListener {
    void onAccelerationChanged(float acceleration);

    void onAvgScoreChanged(float score);

    void onSpeedChanged(float speed);

    void onGpsProviderStateChanged(IGeoSampleListener.GpsProviderState state);

    void onDataProviderChanged(EcoDrivingContract.EcoDrivingDataProvider provider);
}

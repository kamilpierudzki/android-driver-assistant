package com.google.kpierudzki.driverassistant.eco_driving.connector;

import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

/**
 * Created by Kamil on 20.07.2017.
 */

public interface IEcoDrivingObservable extends IBaseManager.IBaseManagerObservable {
    void forcePersistBuffer(boolean async);

    void onPermissionGranted();
}

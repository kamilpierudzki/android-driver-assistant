package com.google.kpierudzki.driverassistant.obd.service.obdmanager;

/**
 * Created by Kamil on 16.09.2017.
 */

public interface IObdCommonObservable {
    void disconnect();

    void onPermissionGranted();
}

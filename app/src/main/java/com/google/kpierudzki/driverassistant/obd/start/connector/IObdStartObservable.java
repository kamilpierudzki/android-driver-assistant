package com.google.kpierudzki.driverassistant.obd.start.connector;

import android.bluetooth.BluetoothDevice;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.IObdCommonObservable;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

/**
 * Created by Kamil on 09.09.2017.
 */

public interface IObdStartObservable extends IBaseManager.IBaseManagerObservable, IObdCommonObservable {
    void provideBluetoothDevices();
    void connectToDevice(BluetoothDevice device, ObdProtocol protocol);
}

package com.google.kpierudzki.driverassistant.obd.connect.connector;

import android.bluetooth.BluetoothDevice;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.IObdCommonObservable;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

/**
 * Created by Kamil on 10.09.2017.
 */

public interface IObdConnectObservable extends IBaseManager.IBaseManagerObservable, IObdCommonObservable {
    void connectToDevice(BluetoothDevice device, ObdProtocol protocol);
}

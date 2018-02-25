package com.google.kpierudzki.driverassistant.obd.connect.connector;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.IObdCommonListener;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

/**
 * Created by Kamil on 10.09.2017.
 */

public interface IObdConnectListener extends IBaseManager.IBaseManagerListener, IObdCommonListener {
    void onConnectingDevice(@Nullable BluetoothDevice bluetoothDevice, @Nullable ObdProtocol protocol);

    void onConnectionInfo(String info);
}

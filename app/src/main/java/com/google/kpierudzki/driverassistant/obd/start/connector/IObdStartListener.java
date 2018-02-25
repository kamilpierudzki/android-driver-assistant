package com.google.kpierudzki.driverassistant.obd.start.connector;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.IObdCommonListener;
import com.google.kpierudzki.driverassistant.obd.start.ObdStartContract;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

import java.util.List;

/**
 * Created by Kamil on 09.09.2017.
 */

public interface IObdStartListener extends IBaseManager.IBaseManagerListener, IObdCommonListener {
    void onDevicesLoaded(List<ObdStartContract.BluetoothDeviceModel> bluetoothDevices, ObdProtocol[] protocols);
}

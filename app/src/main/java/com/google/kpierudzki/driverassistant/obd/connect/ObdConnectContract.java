package com.google.kpierudzki.driverassistant.obd.connect;

import android.bluetooth.BluetoothDevice;

import com.google.kpierudzki.driverassistant.BaseView;
import com.google.kpierudzki.driverassistant.common.BasePresenter;
import com.google.kpierudzki.driverassistant.obd.connect.connector.IObdConnectListener;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;

/**
 * Created by Kamil on 09.09.2017.
 */

public interface ObdConnectContract {

    interface View extends BaseView<Presenter>, IObdConnectListener {
        //...
    }

    interface Presenter extends BasePresenter, IObdConnectListener {
        void connect(BluetoothDevice device, ObdProtocol protocol);
        void cancel();
    }
}

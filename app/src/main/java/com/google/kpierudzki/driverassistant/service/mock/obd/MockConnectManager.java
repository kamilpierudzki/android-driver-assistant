package com.google.kpierudzki.driverassistant.service.mock.obd;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.ConnectManager;
import com.google.kpierudzki.driverassistant.obd.service.provider.MruProtocolProvider;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Kamil on 13.11.2017.
 */

public class MockConnectManager extends ConnectManager {

    public MockConnectManager(ThreadPoolExecutor threadPool, BluetoothDevice device,
                              MruProtocolProvider mruProtocolProvider, ConnectManager.Callbacks callbacks) {
        super(threadPool, device, mruProtocolProvider, callbacks);
        initializeManager = new MockInitializeManager(threadPool, bluetoothSocketSppProvider, mruProtocolProvider, this);
    }

    @WorkerThread
    @Override
    public void connect() {
        if (callbacks.isManagerWorking()) {
            callbacks.onConnectionStateChanged(ObdManager.ConnectionState.Connecting);
            callbacks.onConnectionInfo(App.getAppContext().getString(R.string.Obd_Connect_Step_Connecting));
            initializeManager.initialize();
        } else {
            callbacks.onConnectFailed(this);
        }
    }
}

package com.google.kpierudzki.driverassistant.service.mock.obd;

import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.InitializeManager;
import com.google.kpierudzki.driverassistant.obd.service.provider.BluetoothSocketSppProvider;
import com.google.kpierudzki.driverassistant.obd.service.provider.MruProtocolProvider;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Kamil on 13.11.2017.
 */

public class MockInitializeManager extends InitializeManager {

    public MockInitializeManager(ThreadPoolExecutor workerThreadHandler, BluetoothSocketSppProvider bluetoothSocketSppProvider,
                                 MruProtocolProvider mruProtocolProvider, Callbacks callbacks) {
        super(workerThreadHandler, bluetoothSocketSppProvider, mruProtocolProvider, callbacks);
        testManager = new MockTestManager(workerThreadHandler, bluetoothSocketSppProvider, mruProtocolProvider, this);
    }

    @WorkerThread
    @Override
    public void initialize() {
        if (callbacks.isManagerWorking()) {
            callbacks.onConnectionInfo(App.getAppContext().getString(R.string.Obd_Connect_Step_Initializing,
                    mruProtocolProvider.getProtocol().toString()));

            testManager.test();
        } else {
            callbacks.onInitializeFail();
        }
    }
}

package com.google.kpierudzki.driverassistant.service.mock.obd;

import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.TestManager;
import com.google.kpierudzki.driverassistant.obd.service.provider.BluetoothSocketSppProvider;
import com.google.kpierudzki.driverassistant.obd.service.provider.MruProtocolProvider;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Kamil on 13.11.2017.
 */

public class MockTestManager extends TestManager {

    public MockTestManager(ThreadPoolExecutor workerThreadHandler, BluetoothSocketSppProvider bluetoothSocketSppProvider,
                           MruProtocolProvider mruProtocolProvider, Callbacks callbacks) {
        super(workerThreadHandler, bluetoothSocketSppProvider, mruProtocolProvider, callbacks);
        readDataManager = new MockReadDataManager(workerThreadHandler, bluetoothSocketSppProvider, mruProtocolProvider, this);
    }

    @WorkerThread
    @Override
    public void test() {
        if (callbacks.isManagerWorking()) {
            callbacks.onConnectionInfo(App.getAppContext().getString(R.string.Obd_Connect_Step_Testing));
            readDataManager.readData();
        } else {
            callbacks.onTestFailed();
        }
    }
}

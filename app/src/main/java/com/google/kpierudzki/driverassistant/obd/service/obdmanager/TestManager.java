package com.google.kpierudzki.driverassistant.obd.service.obdmanager;

import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.service.provider.BluetoothSocketSppProvider;
import com.google.kpierudzki.driverassistant.obd.service.provider.MruProtocolProvider;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Kamil on 16.09.2017.
 */

public class TestManager extends CommonCommandsManager implements ReadDataManager.Callbacks, IDtcRemovable {

    protected Callbacks callbacks;
    protected ReadDataManager readDataManager;

    public TestManager(ThreadPoolExecutor workerThreadHandler, BluetoothSocketSppProvider bluetoothSocketSppProvider,
                       MruProtocolProvider mruProtocolProvider, Callbacks callbacks) {
        super(workerThreadHandler, bluetoothSocketSppProvider, mruProtocolProvider);
        this.callbacks = callbacks;
        readDataManager = new ReadDataManager(workerThreadHandler, bluetoothSocketSppProvider, mruProtocolProvider, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (readDataManager != null) readDataManager.onDestroy();
    }

    @WorkerThread
    public void test() {
        if (callbacks.isManagerWorking()) {
            callbacks.onConnectionInfo(App.getAppContext().getString(R.string.Obd_Connect_Step_Testing));

            List<ObdCommandModel> allCommands = readDataManager.getAllCommands();
            boolean isOk = queryAllCommands(allCommands, callbacks);
            if (isOk) isOk = checkCommands(allCommands, callbacks);

            if (isOk) {
                mruProtocolProvider.notifySuccess();
                readDataManager.readData();
            } else {
                callbacks.onTestFailed();
            }
        } else {
            callbacks.onTestFailed();
        }
    }

    @Override
    public boolean isManagerWorking() {
        return callbacks.isManagerWorking();
    }

    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        callbacks.onConnectionStateChanged(state);
    }

    @Override
    public void onNewObdData(ObdCommandModel data) {
        callbacks.onNewObdData(data);
    }

    @Override
    public void onConnectionInfo(String info) {
        callbacks.onConnectionInfo(info);
    }

    @Override
    public void onDeviceMalfunction() {
        callbacks.onDeviceMalfunction();
    }

    @Override
    public void onReadDataFailed() {
        if (threadPool != null && !threadPool.isTerminating())
            threadPool.execute(() -> callbacks.onTestFailed());
    }

    @Override
    public void clearAllDtc() {
        if (readDataManager != null) readDataManager.clearAllDtc();
    }

    public interface Callbacks extends IObdManagerCallbacks {
        void onTestFailed();
    }
}

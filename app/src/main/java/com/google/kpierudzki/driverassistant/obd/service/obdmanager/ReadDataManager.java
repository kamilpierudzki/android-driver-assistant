package com.google.kpierudzki.driverassistant.obd.service.obdmanager;

import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol.ResetTroubleCodesCommand;
import com.google.kpierudzki.driverassistant.obd.service.provider.BluetoothSocketSppProvider;
import com.google.kpierudzki.driverassistant.obd.service.provider.MruProtocolProvider;
import com.google.kpierudzki.driverassistant.obd.service.provider.scheduler.ObdCommandScheduler;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Kamil on 16.09.2017.
 */

public class ReadDataManager extends CommonCommandsManager implements IDtcRemovable {

    protected Callbacks callbacks;
    private ObdCommandScheduler commandScheduler;

    public ReadDataManager(ThreadPoolExecutor workerThreadHandler, BluetoothSocketSppProvider bluetoothSocketSppProvider,
                           MruProtocolProvider mruProtocolProvider, Callbacks callbacks) {
        super(workerThreadHandler, bluetoothSocketSppProvider, mruProtocolProvider);
        this.callbacks = callbacks;
        commandScheduler = new ObdCommandScheduler();
    }

    @WorkerThread
    public void readData() {
        boolean isOk = true;
        callbacks.onConnectionStateChanged(ObdManager.ConnectionState.Connected);
        while (callbacks.isManagerWorking() && isOk) {
            commandScheduler.prepareCommands();
            for (ObdCommandModel obdCommandModel : commandScheduler.getScheduledCommands()) {
                synchronized (this) {
                    isOk = queryCommand(obdCommandModel, callbacks);
                    if (isOk)
                        isOk = checkCommands(commandScheduler.getScheduledCommands(), callbacks);
                    if (isOk) callbacks.onNewObdData(obdCommandModel);
                    else break;
                }
            }
        }
        callbacks.onConnectionStateChanged(ObdManager.ConnectionState.Connecting);
        callbacks.onReadDataFailed();
    }

    List<ObdCommandModel> getAllCommands() {
        return commandScheduler.getAllCommands();
    }

    @MainThread
    public void clearAllDtc() {
        synchronized (this) {
            if (threadPool != null && !threadPool.isTerminating()) {
                threadPool.execute(() -> queryCommandLight(new ResetTroubleCodesCommand()));
            }
        }
    }

    public interface Callbacks extends IObdManagerCallbacks {
        void onReadDataFailed();
    }
}

package com.google.kpierudzki.driverassistant.obd.service.obdmanager;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.service.provider.BluetoothSocketSppProvider;
import com.google.kpierudzki.driverassistant.obd.service.provider.MruProtocolProvider;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Kamil on 15.09.2017.
 */

public class ConnectManager extends CommonCommandsManager implements InitializeManager.Callbacks,
        IDtcRemovable {

    protected final static int CONNECTION_FAIL_LIMIT = 5;

    protected Callbacks callbacks;
    protected InitializeManager initializeManager;

    private int connectionFailCount = 0;

    public ConnectManager(ThreadPoolExecutor threadPool, BluetoothDevice device,
                          MruProtocolProvider mruProtocolProvider, Callbacks callbacks) {
        super(threadPool, new BluetoothSocketSppProvider(device), mruProtocolProvider);
        this.callbacks = callbacks;
        initializeManager = new InitializeManager(threadPool, bluetoothSocketSppProvider, mruProtocolProvider, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (initializeManager != null) initializeManager.onDestroy();
    }

    @WorkerThread
    public void connect() {
        if (callbacks.isManagerWorking()) {
            callbacks.onConnectionStateChanged(ObdManager.ConnectionState.Connecting);
            callbacks.onConnectionInfo(App.getAppContext().getString(R.string.Obd_Connect_Step_Connecting));
            try {
                bluetoothSocketSppProvider.getSocketNew().connect();
                connectionFailCount = 0;
                initializeManager.initialize();
            } catch (IOException e) {
                bluetoothSocketSppProvider.closeSocket();
                connectionFailCount++;
                if (checkConnectionTriesLimitExceeded())
                    callbacks.onConnectFailed(this);
                else if (threadPool != null) threadPool.execute(this::connect);
            }
        } else {
            callbacks.onConnectFailed(this);
        }
    }

    @Override
    public void onInitializeFail() {
        connectionFailCount++;
        if (checkConnectionTriesLimitExceeded()) callbacks.onConnectFailed(this);
        else if (threadPool != null) threadPool.execute(this::connect);
    }

    private boolean checkConnectionTriesLimitExceeded() {
        return connectionFailCount > CONNECTION_FAIL_LIMIT;
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
    public void clearAllDtc() {
        if (initializeManager != null) initializeManager.clearAllDtc();
    }

    public interface Callbacks extends IObdManagerCallbacks {
        void onConnectFailed(@NonNull ConnectManager currentConnectManager);
    }
}

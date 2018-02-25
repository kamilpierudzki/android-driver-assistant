package com.google.kpierudzki.driverassistant.obd.connect;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;

import com.google.kpierudzki.driverassistant.obd.connect.connector.IObdConnectObservable;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterState;
import com.google.kpierudzki.driverassistant.service.helper.ServiceBindHelper;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Kamil on 10.09.2017.
 */

public class ObdConnectPresenter implements ObdConnectContract.Presenter {

    private ObdConnectContract.View view;
    private ServiceBindHelper serviceBindHelper;
    private IObdConnectObservable modelObservable;

    public ObdConnectPresenter(ObdConnectContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        bindConnector();
    }

    private void bindConnector() {
        serviceBindHelper = new ServiceBindHelper(connector -> {
            connector.addListener(ManagerConnectorType.ObdConnect, this);
            modelObservable = (IObdConnectObservable) connector.provideObservable(
                    ManagerConnectorType.ObdConnect);
            if (view != null) view.onPresenterReady(this);
        });
    }

    private void unbindConnector() {
        if (serviceBindHelper != null) {
            if (serviceBindHelper.getServiceConnector() != null)
                serviceBindHelper.getServiceConnector().removeListener(
                        ManagerConnectorType.ObdConnect, this);

            serviceBindHelper.onDestroy();
            serviceBindHelper = null;
        }
    }

    @Override
    public void stop() {
        modelObservable = null;
        unbindConnector();
    }


    @Override
    public void onConnectionInfo(String info) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onConnectionInfo(info);
        });
    }

    @Override
    public void onDeviceMalfunction() {
        MainThreadUtil.post(() -> {
            if (view != null) view.onDeviceMalfunction();
        });
    }

    @Override
    public void connect(BluetoothDevice device, ObdProtocol protocol) {
        if (modelObservable != null) modelObservable.connectToDevice(device, protocol);
    }

    @Override
    public void cancel() {
        if (modelObservable != null) modelObservable.disconnect();
    }

    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onConnectionStateChanged(state);
        });
    }

    @Override
    public void onConnectingDevice(@Nullable BluetoothDevice bluetoothDevice, @Nullable ObdProtocol protocol) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onConnectingDevice(bluetoothDevice, protocol);
        });
    }

    @Override
    public void onBluetoothAdapterStateChanged(@NotNull BluetoothAdapterState state) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onBluetoothAdapterStateChanged(state);
        });
    }
}

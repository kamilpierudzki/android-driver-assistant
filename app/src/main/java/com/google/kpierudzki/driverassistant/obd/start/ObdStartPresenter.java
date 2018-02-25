package com.google.kpierudzki.driverassistant.obd.start;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.start.connector.IObdStartObservable;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterState;
import com.google.kpierudzki.driverassistant.service.helper.ServiceBindHelper;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Kamil on 09.09.2017.
 */

public class ObdStartPresenter implements ObdStartContract.Presenter {

    private ObdStartContract.View view;
    private ServiceBindHelper serviceBindHelper;
    private IObdStartObservable obdObservable;

    public ObdStartPresenter(@NonNull ObdStartContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        bindConnector();
    }

    private void bindConnector() {
        serviceBindHelper = new ServiceBindHelper(connector -> {
            connector.addListener(ManagerConnectorType.ObdStart, ObdStartPresenter.this);
            obdObservable = (IObdStartObservable) connector.provideObservable(ManagerConnectorType.ObdStart);
            if (view != null) view.onPresenterReady(this);
        });
    }

    private void unbindConnector() {
        if (serviceBindHelper != null) {
            if (serviceBindHelper.getServiceConnector() != null)
                serviceBindHelper.getServiceConnector().removeListener(
                        ManagerConnectorType.ObdStart, ObdStartPresenter.this);

            serviceBindHelper.onDestroy();
            serviceBindHelper = null;
        }
    }

    @Override
    public void stop() {
        obdObservable = null;
        unbindConnector();
    }

    @Override
    public void onDevicesLoaded(List<ObdStartContract.BluetoothDeviceModel> bluetoothDevices, ObdProtocol[] protocols) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onDevicesLoaded(bluetoothDevices, protocols);
        });
    }

    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onConnectionStateChanged(state);
        });
    }

    @Override
    public void onDeviceMalfunction() {
        MainThreadUtil.post(() -> {
            if (view != null) view.onDeviceMalfunction();
        });
    }

    @Override
    public void provideBluetoothDevices() {
        if (obdObservable != null) obdObservable.provideBluetoothDevices();
    }

    @Override
    public void connect(BluetoothDevice device, ObdProtocol protocol) {
        if (obdObservable != null) obdObservable.connectToDevice(device, protocol);
    }

    @Override
    public void onPermissionGranted() {
        if (obdObservable != null) obdObservable.onPermissionGranted();
    }

    @Override
    public void onBluetoothAdapterStateChanged(@NotNull BluetoothAdapterState state) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onBluetoothAdapterStateChanged(state);
        });
    }
}

package com.google.kpierudzki.driverassistant.obd.read;

import android.support.annotation.NonNull;

import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadObservable;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterState;
import com.google.kpierudzki.driverassistant.service.helper.ServiceBindHelper;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

/**
 * Created by Kamil on 16.09.2017.
 */

public class ObdReadPresenter implements ObdReadContract.Presenter {

    private ObdReadContract.View view;
    private ServiceBindHelper serviceBindHelper;
    private IObdReadObservable modelObservable;

    public ObdReadPresenter(ObdReadContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        bindConnector();
    }

    private void bindConnector() {
        serviceBindHelper = new ServiceBindHelper(connector -> {
            connector.addListener(ManagerConnectorType.ObdRead, this);
            modelObservable = (IObdReadObservable) connector.provideObservable(ManagerConnectorType.ObdRead);
            if (view != null) view.onPresenterReady(this);
        });
    }

    private void unbindConnector() {
        if (serviceBindHelper != null) {
            if (serviceBindHelper.getServiceConnector() != null)
                serviceBindHelper.getServiceConnector().removeListener(
                        ManagerConnectorType.ObdRead, ObdReadPresenter.this);

            serviceBindHelper.onDestroy();
            serviceBindHelper = null;
        }
    }

    @Override
    public void stop() {
        unbindConnector();
    }

    @Override
    public void onBluetoothAdapterStateChanged(@NonNull BluetoothAdapterState state) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onBluetoothAdapterStateChanged(state);
        });
    }

    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onConnectionStateChanged(state);
        });
    }

    @Override
    public void onNewObdData(ObdCommandModel data) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onNewObdData(data);
        });
    }

    @Override
    public void onDeviceMalfunction() {
        MainThreadUtil.post(() -> {
            if (view != null) view.onDeviceMalfunction();
        });
    }

    @Override
    public void onNewDtcDetected() {
        MainThreadUtil.post(() -> {
            if (view != null) view.onNewDtcDetected();
        });
    }

    @Override
    public void fetchDtcInfo() {
        if (modelObservable != null) {
            if (modelObservable.dtcDetected())
                MainThreadUtil.post(() -> {
                    if (view != null) view.onNewDtcDetected();
                });
        }
    }
}

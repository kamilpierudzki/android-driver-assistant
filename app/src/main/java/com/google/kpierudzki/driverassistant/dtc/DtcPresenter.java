package com.google.kpierudzki.driverassistant.dtc;

import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.dtc.database.DtcEntity;
import com.google.kpierudzki.driverassistant.dtc.usecase.DtcDbUseCase;
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadListener;
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadObservable;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterState;
import com.google.kpierudzki.driverassistant.service.helper.ServiceBindHelper;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Kamil on 25.12.2017.
 */

public class DtcPresenter implements DtcContract.Presenter, DtcDbUseCase.Callbacks, IObdReadListener {

    private DtcContract.View view;
    private DtcDbUseCase useCase;
    private IObdReadObservable modelObservable;
    private boolean dtcDetected = false;
    private ObdManager.ConnectionState _currentConnectionState = ObdManager.ConnectionState.Failed;
    private ServiceBindHelper _serviceBindHelper;

    public DtcPresenter(DtcContract.View view) {
        this.view = view;
        if (view != null) view.setPresenter(this);
    }

    @Override
    public void start() {
        useCase = new DtcDbUseCase(this);
        bindConnector();
    }

    @Override
    public void stop() {
        if (useCase != null) {
            useCase.onDestroy();
            useCase = null;
        }

        unbindConnector();
        modelObservable = null;
    }

    @Override
    public void fetchData() {
        if (useCase != null) useCase.fetchData();
    }

    @Override
    public void clearAllDtc() {
        if (useCase != null) useCase.clearAllDtc();

        if (modelObservable != null) modelObservable.clearAllDtc();
    }

    @Override
    public void onDataFetched(List<DtcEntity> data) {
        dtcDetected = !data.isEmpty();

        MainThreadUtil.post(() -> {
            if (view != null) view.fillList(data);
        });

        notifyDtcState();
    }

    private void bindConnector() {
        _serviceBindHelper = new ServiceBindHelper(connector -> {
            connector.addListener(ManagerConnectorType.ObdRead, this);
            modelObservable = (IObdReadObservable) connector.provideObservable(ManagerConnectorType.ObdRead);
            if (view != null) view.onPresenterReady(this);
        });
    }

    private void unbindConnector() {
        if (_serviceBindHelper != null) {
            if (_serviceBindHelper.getServiceConnector() != null) {
                _serviceBindHelper.getServiceConnector().removeListener(
                        ManagerConnectorType.ObdRead,
                        this);
            }
            _serviceBindHelper.onDestroy();
            _serviceBindHelper = null;
        }
    }

    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        _currentConnectionState = state;
        notifyDtcState();
    }

    @WorkerThread
    private void notifyDtcState() {
        MainThreadUtil.post(() -> {
            if (view != null) {
                if (_currentConnectionState == ObdManager.ConnectionState.Connected && dtcDetected) {
                    view.showClearDtcIcon(true);
                } else {
                    view.showClearDtcIcon(false);
                }
            }
        });
    }

    @Override
    public void onNewObdData(ObdCommandModel data) {
        //Ignore
    }

    @Override
    public void onDeviceMalfunction() {
        //Ignore
    }

    @Override
    public void onNewDtcDetected() {
        fetchData();
    }

    @Override
    public void onBluetoothAdapterStateChanged(@NotNull BluetoothAdapterState state) {
        //Ignore
    }
}

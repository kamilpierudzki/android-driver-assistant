package com.google.kpierudzki.driverassistant.debug.obd_recording;

import com.google.kpierudzki.driverassistant.debug.obd_recording.connector.IObdProbesRecordingObservable;
import com.google.kpierudzki.driverassistant.debug.obd_recording.usecase.DatabaseUseCase;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.service.connector.IConnectorSelectable;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Kamil on 17.09.2017.
 */

public class ObdProbesRecordingPresenter implements ObdProbesRecordingContract.Presenter, DatabaseUseCase.Callback {

    private ObdProbesRecordingContract.View view;
    private IObdProbesRecordingObservable modelObservable;
    private DatabaseUseCase databaseUseCase;

    public ObdProbesRecordingPresenter(ObdProbesRecordingContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        databaseUseCase = new DatabaseUseCase();
        databaseUseCase.provideSamplesCount(this);

//        bindConnector(App.getServiceConnector());
    }

    private void bindConnector(IConnectorSelectable connector) {
        connector.addListener(ManagerConnectorType.ObdProbesRecording, ObdProbesRecordingPresenter.this);
        modelObservable = (IObdProbesRecordingObservable) connector.provideObservable(
                ManagerConnectorType.ObdProbesRecording);
        if (view != null) view.onPresenterReady(this);
    }

    private void unbindConnector(IConnectorSelectable connector) {
        connector.removeListener(ManagerConnectorType.ObdProbesRecording,
                ObdProbesRecordingPresenter.this);
    }

    @Override
    public void stop() {
        databaseUseCase.onDestroy();

        if (modelObservable != null) {
            modelObservable.forcePersistBuffer();
            modelObservable = null;
        }
//        unbindConnector(App.getServiceConnector());
    }

    @Override
    public void startRecord() {
        if (modelObservable != null) modelObservable.startRecord();
    }

    @Override
    public void stopRecord() {
        if (modelObservable != null) modelObservable.stopRecord();
    }

    @Override
    public void exportDatabase(String filename) {
        if (modelObservable != null) modelObservable.forcePersistBuffer();
        databaseUseCase.exportDatabase(this, filename);
    }

    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        //Ignore
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
    public void onNewObdProbesRecordStatus(ObdProbesRecordingContract.RecordStatus status) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onNewRecordStatus(status);
        });
    }

    @Override
    public void onNewSamplesCount(ObdParamType paramType, int count) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onNewSamplesCount(paramType, count);
        });
    }

    @Override
    public void onDatabaseExported(boolean success) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onExportDatabase(success);
        });
    }

    @Override
    public void onNewDtcDetected() {
        //Ignore
    }

    @Override
    public void onBluetoothAdapterStateChanged(@NotNull com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterState state) {
        //Ignore
    }
}

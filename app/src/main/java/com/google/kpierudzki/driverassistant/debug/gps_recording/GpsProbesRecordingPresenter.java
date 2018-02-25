package com.google.kpierudzki.driverassistant.debug.gps_recording;

import com.google.kpierudzki.driverassistant.debug.gps_recording.connector.IGpsProbesRecordingObservable;
import com.google.kpierudzki.driverassistant.debug.gps_recording.usecase.DatabaseUseCase;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.helper.ServiceBindHelper;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

/**
 * Created by Kamil on 26.06.2017.
 */

public class GpsProbesRecordingPresenter implements GpsProbesRecordingContract.Presenter, DatabaseUseCase.Callback {

    private GpsProbesRecordingContract.View view;
    private IGpsProbesRecordingObservable modelObservable;

    private DatabaseUseCase databaseUseCase;
    private ServiceBindHelper serviceBindHelper;

    public GpsProbesRecordingPresenter(GpsProbesRecordingContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        databaseUseCase = new DatabaseUseCase();
        databaseUseCase.provideSamplesCount(this);
        bindConnector();
    }

    private void bindConnector() {
        serviceBindHelper = new ServiceBindHelper(connector -> {
            connector.addListener(ManagerConnectorType.GpsProbesRecording, GpsProbesRecordingPresenter.this);
            modelObservable = (IGpsProbesRecordingObservable) connector
                    .provideObservable(ManagerConnectorType.GpsProbesRecording);
            if (view != null) view.onPresenterReady(this);
        });
    }

    private void unbindConnector() {
        if (serviceBindHelper != null) {
            if (serviceBindHelper.getServiceConnector() != null)
                serviceBindHelper.getServiceConnector().removeListener(
                        ManagerConnectorType.GpsProbesRecording, GpsProbesRecordingPresenter.this);
            serviceBindHelper.onDestroy();
            serviceBindHelper = null;
        }
    }

    @Override
    public void stop() {
        databaseUseCase.onDestroy();

        if (modelObservable != null) {
            modelObservable.forcePersistBuffer();
            modelObservable = null;
        }

        unbindConnector();
    }

    @Override
    public void onNewRecordStatus(GpsProbesRecordingContract.RecordStatus status) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onNewRecordStatus(status);
        });
    }

    @Override
    public void onNewSamplesCount(int count) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onNewSamplesCount(count);
        });
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
    public void onSamplesCount(int count) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onNewSamplesCount(count);
        });
    }

    @Override
    public void onDatabaseExported(boolean success) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onExportDatabase(success);
        });
    }
}

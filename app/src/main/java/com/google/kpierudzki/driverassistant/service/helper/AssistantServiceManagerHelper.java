package com.google.kpierudzki.driverassistant.service.helper;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.kpierudzki.driverassistant.background_work.service.BackgroundWorkManager;
import com.google.kpierudzki.driverassistant.common.IDestroyable;
import com.google.kpierudzki.driverassistant.debug.gps_recording.service.DebugGpsProbesRecordingManager;
import com.google.kpierudzki.driverassistant.debug.obd_recording.service.DebugObdProbesRecordingManager;
import com.google.kpierudzki.driverassistant.eco_driving.service.EcoDrivingGpsBasedManager;
import com.google.kpierudzki.driverassistant.eco_driving.service.EcoDrivingObdBasedManager;
import com.google.kpierudzki.driverassistant.geo_samples.service.GeoSamplesManager;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;
import com.google.kpierudzki.driverassistant.service.connector.IConnectorSelectable;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.manager.BaseServiceManager;

import java.util.ArrayList;
import java.util.List;

import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 28.06.2017.
 */

public class AssistantServiceManagerHelper implements IDestroyable, IConnectorSelectable {

    private List<BaseServiceManager> managers = new ArrayList<>();

    @MainThread
    public AssistantServiceManagerHelper(Context context) {
        GeoSamplesManager geoSamplesMng = new GeoSamplesManager(context);
        managers.add(geoSamplesMng);
        managers.add(new BackgroundWorkManager(context));

        ObdManager obdMng = new ObdManager(context);
        managers.add(obdMng);

        prepareEcoDrivingGpsBasedMng(geoSamplesMng);
        prepareGpsRecordingMng();
        prepareObdProbesRecordMng(context);
        prepareEcoDrivingObdBasedMng(obdMng);
        prepareObdManager(geoSamplesMng, obdMng);
    }

    private void prepareEcoDrivingGpsBasedMng(GeoSamplesManager geoSamplesMng) {
        EcoDrivingGpsBasedManager ecoDrivingGpsMng = new EcoDrivingGpsBasedManager();
        addListener(ManagerConnectorType.GeoSamples, ecoDrivingGpsMng);
        addListener(ManagerConnectorType.ObdRead, ecoDrivingGpsMng);
        ecoDrivingGpsMng.setGeoSampleObservable(geoSamplesMng);
        managers.add(ecoDrivingGpsMng);
    }

    private void prepareObdProbesRecordMng(Context context) {
        DebugObdProbesRecordingManager dbgObdProbesRecMng = new DebugObdProbesRecordingManager(context);
        addListener(ManagerConnectorType.ObdRead, dbgObdProbesRecMng);
        managers.add(dbgObdProbesRecMng);
    }

    private void prepareEcoDrivingObdBasedMng(ObdManager obdMng) {
        EcoDrivingObdBasedManager ecoDrivingObdMng = new EcoDrivingObdBasedManager();
        addListener(ManagerConnectorType.GeoSamples, ecoDrivingObdMng);
        addListener(ManagerConnectorType.ObdRead, ecoDrivingObdMng);
        ecoDrivingObdMng.setObdReadObservable(obdMng);
        managers.add(ecoDrivingObdMng);
    }

    private void prepareObdManager(GeoSamplesManager geoSamplesMng, ObdManager obdMng) {
        addListener(ManagerConnectorType.GeoSamples, obdMng);
        obdMng.setGeoSampleObservable(geoSamplesMng);
    }

    private void prepareGpsRecordingMng() {
        DebugGpsProbesRecordingManager dbgGpsRecorder = new DebugGpsProbesRecordingManager();
        addListener(ManagerConnectorType.GeoSamples, dbgGpsRecorder);
        managers.add(dbgGpsRecorder);
    }

    @Override
    public void onDestroy() {
        StreamSupport.stream(managers).forEach(BaseServiceManager::onDestroy);
        managers.clear();
    }

    @MainThread
    @Override
    public void addListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManager.IBaseManagerListener listener) {
        StreamSupport.stream(managers).filter(baseServiceManager ->
                baseServiceManager.connectorMatches(connectorType))
                .findFirst().get().addListener(connectorType, listener);
    }

    @MainThread
    @Override
    public void removeListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManager.IBaseManagerListener listener) {
        StreamSupport.stream(managers).filter(baseServiceManager ->
                baseServiceManager.connectorMatches(connectorType))
                .findFirst().get().removeListener(connectorType, listener);
    }

    @Nullable
    @Override
    public IBaseManager.IBaseManagerObservable provideObservable(@NonNull ManagerConnectorType connectorType) {
        return StreamSupport.stream(managers).filter(baseServiceManager ->
                baseServiceManager.connectorMatches(connectorType))
                .findFirst().get().provideObservable(connectorType);
    }
}

package com.google.kpierudzki.driverassistant.obd.service.database;

import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.obd.database.ObdParamsDao;
import com.google.kpierudzki.driverassistant.obd.database.ObdParamsEntity;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadListener;
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadObservable;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterState;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kamil on 15.11.2017.
 */

public class ObdProbesToDbSaver implements IObdReadListener, IObdReadObservable {

    private List<ObdParamsEntity> buffer;
    private ObdParamsDao obdParamsDao;
    private int bufferLimit;
    private Map<ObdParamType, ObdCommandModel> paramsCollector;

    public ObdProbesToDbSaver(ObdParamsDao obdParamsDao, int bufferLimit) {
        this.obdParamsDao = obdParamsDao;
        this.bufferLimit = bufferLimit;
        buffer = new ArrayList<>();
        paramsCollector = new HashMap<>();
    }

    @Override
    public void forcePersistBuffer(boolean async) {
        synchronized (this) {
            if (!buffer.isEmpty()) {
                obdParamsDao.addAll(buffer);
                buffer.clear();
            }
        }
    }

    @MainThread
    @Override
    public void clearAllDtc() {
        synchronized (this) {
            buffer.clear();
        }
    }

    @Override
    public boolean dtcDetected() {
        return false;//Ignore
    }

    @WorkerThread
    @Override
    public void onNewObdData(ObdCommandModel data) {
        synchronized (this) {
            paramsCollector.put(data.paramType, data);
        }
    }

    @WorkerThread
    public void updateGeoData(IGeoSampleListener.GeoSamplesSwappableData geoData) {
        synchronized (this) {
            ObdParamsEntity obdParamsEntity = new ObdParamsEntity(geoData,
                    prepareValueOrDefault(ObdParamType.SPEED),
                    prepareValueOrDefault(ObdParamType.ENGINE_RPM),
                    prepareValueOrDefault(ObdParamType.COOLANT_TEMP),
                    prepareValueOrDefault(ObdParamType.ENGINE_LOAD),
                    prepareValueOrDefault(ObdParamType.OIL_TEMP),
                    prepareValueOrDefault(ObdParamType.MAF),
                    prepareValueOrDefault(ObdParamType.AMBIENT_AIR_TEMP),
                    prepareValueOrDefault(ObdParamType.BAROMETRIC_PRESSURE));

            buffer.add(obdParamsEntity);

            if (buffer.size() >= bufferLimit) {
                obdParamsDao.addAll(buffer);
                buffer.clear();
            }
        }
    }

    private float prepareValueOrDefault(ObdParamType paramType) {
        ObdCommandModel obdCommandModel = paramsCollector.get(paramType);
        if (obdCommandModel == null)
            return 0;
        else return obdCommandModel.value;
    }

    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {

    }

    @Override
    public void onDeviceMalfunction() {
        //Ignore
    }

    @Override
    public void disconnect() {
        //Ignore
    }

    @Override
    public void onPermissionGranted() {
        // Ignore
    }

    @Override
    public void onNewDtcDetected() {
        //Ignore
    }

    @Override
    public void onBluetoothAdapterStateChanged(@NotNull BluetoothAdapterState state) {
        //Ignore
    }
}

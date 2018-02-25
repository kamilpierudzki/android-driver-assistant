package com.google.kpierudzki.driverassistant.obd.service.database;

import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.dtc.database.DtcEntity;
import com.google.kpierudzki.driverassistant.dtc.datamodel.DtcModel;
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadObservable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 25.12.2017.
 */

public class DtcSaver implements IObdReadObservable {

    private Set<DtcModel> detectedDtc;

    public DtcSaver() {
        detectedDtc = new HashSet<>();
    }

    @WorkerThread
    public boolean addModels(List<DtcModel> dtcModels) {
        synchronized (this) {
            StreamSupport.stream(dtcModels).forEach(dtcModel ->
                    detectedDtc.add(new DtcModel(dtcModel.code, dtcModel.desc)));

            //If size is NOT equal for both collections, there was change and listeners should be notified
            return dtcModels.size() != detectedDtc.size();
        }
    }

    @Override
    public void disconnect() {
        //Ignore
    }

    @Override
    public void onPermissionGranted() {
        //Ignore
    }

    @WorkerThread
    @Override
    public void forcePersistBuffer(boolean async) {
        synchronized (this) {
            if (detectedDtc != null && !detectedDtc.isEmpty()) {
                App.getDatabase().getDtcDao().addAll(
                        StreamSupport.stream(detectedDtc)
                                .map(DtcEntity::new)
                                .collect(Collectors.toSet()));
                detectedDtc.clear();
            }
        }
    }

    @MainThread
    @Override
    public void clearAllDtc() {
        synchronized (this) {
            detectedDtc.clear();
        }
    }

    public boolean dtcDetected() {
        synchronized (this) {
            return !detectedDtc.isEmpty();
        }
    }
}

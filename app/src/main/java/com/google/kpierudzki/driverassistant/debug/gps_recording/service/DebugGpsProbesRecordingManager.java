package com.google.kpierudzki.driverassistant.debug.gps_recording.service;

import android.location.Location;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.debug.gps_recording.GpsProbesRecordingContract;
import com.google.kpierudzki.driverassistant.debug.gps_recording.connector.IGpsProbesRecordingListener;
import com.google.kpierudzki.driverassistant.debug.gps_recording.connector.IGpsProbesRecordingObservable;
import com.google.kpierudzki.driverassistant.debug.gps_recording.database.GpsProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.manager.BaseServiceManager;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

import java.util.ArrayList;
import java.util.List;

import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 26.06.2017.
 */

public class DebugGpsProbesRecordingManager extends BaseServiceManager implements
        IGpsProbesRecordingObservable, IGeoSampleListener {

    private final int BUFFER_SIZE = 300;
    private List<GpsProbeRecordingEntity> buffer;

    private GpsProbesRecordingContract.RecordStatus _currentStatus =
            GpsProbesRecordingContract.RecordStatus.NOT_RECORDING;

    public DebugGpsProbesRecordingManager() {
        super(new ManagerConnectorType[]{ManagerConnectorType.GpsProbesRecording});
        buffer = new ArrayList<>();
    }

    @MainThread
    @Override
    public void onDestroy() {
        if (threadPool != null && !threadPool.isTerminating()) {
            threadPool.execute(() -> {
                synchronized (this) {
                    persistBuffer();
                    MainThreadUtil.post(super::onDestroy);
                }
            });
        } else {
            super.onDestroy();
        }
    }

    @Override
    public void addListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManager.IBaseManagerListener listener) {
        super.addListener(connectorType, listener);
        notifyRecordStatusChanged();
        notifySamplesCountChanged();
    }

    @Nullable
    @Override
    public IBaseManager.IBaseManagerObservable provideObservable(@NonNull ManagerConnectorType connectorType) {
        return this;
    }

    @Override
    public void onNewData(GeoSamplesSwappableData newData) {
        // Ignore
    }

    @Override
    public void onGpsProviderStateChanged(GpsProviderState state) {
        // Ignore
    }

    @WorkerThread
    @Override
    public void onRawLocation(Location location) {
        if (threadPool != null && !threadPool.isTerminating()) {
            threadPool.execute(() -> {
                synchronized (this) {
                    if (_currentStatus == GpsProbesRecordingContract.RecordStatus.RECORDING) {
                        if (buffer.size() < BUFFER_SIZE) {
                            addSampleToBuffer(location);
                        } else {
                            App.getDatabase().getGpsProbeRecordingDao().addAll(buffer);
                            buffer.clear();
                            addSampleToBuffer(location);
                        }
                    }
                }
            });
        }
    }

    @WorkerThread
    private void addSampleToBuffer(Location location) {
        buffer.add(new GpsProbeRecordingEntity(System.currentTimeMillis(), location));
        notifySamplesCountChanged();
    }

    @MainThread
    @WorkerThread
    private void notifySamplesCountChanged() {
        if (threadPool != null && !threadPool.isTerminating())
            threadPool.execute(() -> {
                synchronized (this) {
                    StreamSupport.stream(getListeners())
                            .filter(listener -> listener instanceof IGpsProbesRecordingListener)
                            .map(listener -> (IGpsProbesRecordingListener) listener)
                            .forEach(listener -> listener.onNewSamplesCount(
                                    App.getDatabase().getGpsProbeRecordingDao().getProbesCount() + buffer.size()));
                }
            });
    }

    @MainThread
    @Override
    public void startRecord() {
        synchronized (this) {
            _currentStatus = GpsProbesRecordingContract.RecordStatus.RECORDING;
            notifyRecordStatusChanged();
        }
    }

    @MainThread
    @Override
    public void stopRecord() {
        synchronized (this) {
            _currentStatus = GpsProbesRecordingContract.RecordStatus.NOT_RECORDING;
            notifyRecordStatusChanged();
        }
    }

    @MainThread
    @Override
    public void forcePersistBuffer() {
        if (threadPool != null && !threadPool.isTerminating()) {
            threadPool.execute(this::persistBuffer);
        }
    }

    @WorkerThread
    private void persistBuffer() {
        synchronized (this) {
            if (!buffer.isEmpty()) {
                App.getDatabase().getGpsProbeRecordingDao().addAll(buffer);
                buffer.clear();
            }
        }
    }

    @MainThread
    private void notifyRecordStatusChanged() {
        threadPool.execute(() -> {
            synchronized (this) {
                StreamSupport.stream(getListeners())
                        .filter(listener -> listener instanceof IGpsProbesRecordingListener)
                        .map(listener -> (IGpsProbesRecordingListener) listener)
                        .forEach(listener -> listener.onNewRecordStatus(_currentStatus));
            }
        });
    }
}

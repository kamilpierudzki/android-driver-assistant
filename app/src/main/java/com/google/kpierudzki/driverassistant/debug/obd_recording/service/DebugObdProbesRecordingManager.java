package com.google.kpierudzki.driverassistant.debug.obd_recording.service;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.debug.obd_recording.ObdProbesRecordingContract;
import com.google.kpierudzki.driverassistant.debug.obd_recording.connector.IObdProbesRecordingListener;
import com.google.kpierudzki.driverassistant.debug.obd_recording.connector.IObdProbesRecordingObservable;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdAmbientAirTempProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdBarometricPressProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdCoolantTempProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdLoadProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdMafProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdOilTempProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdRpmProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdSpeedProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadListener;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.manager.BaseServiceManager;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 17.09.2017.
 */

public class DebugObdProbesRecordingManager extends BaseServiceManager implements
        IObdProbesRecordingObservable, IObdReadListener {

    private final int BUFFER_SIZE = 500;
    private List<ObdSpeedProbeRecordingEntity> speedBuffer;
    private List<ObdRpmProbeRecordingEntity> rpmBuffer;
    private List<ObdMafProbeRecordingEntity> mafBuffer;
    private List<ObdCoolantTempProbeRecordingEntity> coolantTempBuffer;
    private List<ObdLoadProbeRecordingEntity> loadBuffer;
    private List<ObdBarometricPressProbeRecordingEntity> barometricPressBuffer;
    private List<ObdOilTempProbeRecordingEntity> oilTempBuffer;
    private List<ObdAmbientAirTempProbeRecordingEntity> ambientAirTempBuffer;

    private ObdProbesRecordingContract.RecordStatus _currentStatus =
            ObdProbesRecordingContract.RecordStatus.NOT_RECORDING;

    public DebugObdProbesRecordingManager(Context context) {
        super(new ManagerConnectorType[]{ManagerConnectorType.ObdProbesRecording});
        speedBuffer = new ArrayList<>();
        rpmBuffer = new ArrayList<>();
        mafBuffer = new ArrayList<>();
        coolantTempBuffer = new ArrayList<>();
        loadBuffer = new ArrayList<>();
        barometricPressBuffer = new ArrayList<>();
        oilTempBuffer = new ArrayList<>();
        ambientAirTempBuffer = new ArrayList<>();
    }

    @MainThread
    @Override
    public void onDestroy() {
        if (threadPool != null && !threadPool.isTerminating()) {
            threadPool.execute(() -> {
                synchronized (this) {
                    persistBuffers();
                    MainThreadUtil.post(super::onDestroy);
                }
            });
        } else {
            super.onDestroy();
        }
    }

    @MainThread
    @Nullable
    @Override
    public IBaseManager.IBaseManagerObservable provideObservable(@NonNull ManagerConnectorType connectorType) {
        return this;
    }

    @Override
    public void addListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManagerListener listener) {
        super.addListener(connectorType, listener);
        notifyRecordStatusChanged();
        notifySamplesCountChanged(ObdParamType.SPEED, speedBuffer.size());
        notifySamplesCountChanged(ObdParamType.ENGINE_RPM, rpmBuffer.size());
        notifySamplesCountChanged(ObdParamType.MAF, rpmBuffer.size());
        notifySamplesCountChanged(ObdParamType.COOLANT_TEMP, rpmBuffer.size());
        notifySamplesCountChanged(ObdParamType.ENGINE_LOAD, rpmBuffer.size());
        notifySamplesCountChanged(ObdParamType.BAROMETRIC_PRESSURE, rpmBuffer.size());
        notifySamplesCountChanged(ObdParamType.OIL_TEMP, rpmBuffer.size());
        notifySamplesCountChanged(ObdParamType.AMBIENT_AIR_TEMP, rpmBuffer.size());
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
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        //Ignore
    }

    @WorkerThread
    @Override
    public void onNewObdData(ObdCommandModel data) {
        if (threadPool != null)
            synchronized (this) {
                if (_currentStatus == ObdProbesRecordingContract.RecordStatus.RECORDING) {
                    threadPool.execute(() -> {
                        switch (data.paramType) {
                            case SPEED:
                                if (speedBuffer.size() > BUFFER_SIZE) {
                                    addSampleToSpeedBuffer(data.value);
                                } else {
                                    App.getDatabase().getObdSpeedProbeRecordingDao().addAll(speedBuffer);
                                    speedBuffer.clear();
                                    addSampleToSpeedBuffer(data.value);
                                }
                                break;
                            case ENGINE_RPM:
                                if (rpmBuffer.size() > BUFFER_SIZE) {
                                    addSampleToRpmBuffer(data.value);
                                } else {
                                    App.getDatabase().getObdRpmProbeRecordingDao().addAll(rpmBuffer);
                                    rpmBuffer.clear();
                                    addSampleToRpmBuffer(data.value);
                                }
                                break;
                            case MAF:
                                if (mafBuffer.size() > BUFFER_SIZE) {
                                    addSampleToMafBuffer(data.value);
                                } else {
                                    App.getDatabase().getObdMafProbeRecordingDao().addAll(mafBuffer);
                                    mafBuffer.clear();
                                    addSampleToMafBuffer(data.value);
                                }
                                break;
                            case COOLANT_TEMP:
                                if (coolantTempBuffer.size() > BUFFER_SIZE) {
                                    addSampleToCoolantTempBuffer(data.value);
                                } else {
                                    App.getDatabase().getObdCoolantTempProbeRecordingDao().addAll(coolantTempBuffer);
                                    coolantTempBuffer.clear();
                                    addSampleToCoolantTempBuffer(data.value);
                                }
                                break;
                            case ENGINE_LOAD:
                                if (loadBuffer.size() > BUFFER_SIZE) {
                                    addSampleToLoadBuffer(data.value);
                                } else {
                                    App.getDatabase().getObdLoadProbeRecordingDao().addAll(loadBuffer);
                                    loadBuffer.clear();
                                    addSampleToLoadBuffer(data.value);
                                }
                                break;
                            case BAROMETRIC_PRESSURE:
                                if (barometricPressBuffer.size() > BUFFER_SIZE) {
                                    addSampleToBarometricPressBuffer(data.value);
                                } else {
                                    App.getDatabase().getObdBarometricPressProbeRecordingDao().addAll(barometricPressBuffer);
                                    barometricPressBuffer.clear();
                                    addSampleToBarometricPressBuffer(data.value);
                                }
                                break;
                            case OIL_TEMP:
                                if (oilTempBuffer.size() > BUFFER_SIZE) {
                                    addSampleToOilTempBuffer(data.value);
                                } else {
                                    App.getDatabase().getObdOilTempProbeRecordingDao().addAll(oilTempBuffer);
                                    oilTempBuffer.clear();
                                    addSampleToOilTempBuffer(data.value);
                                }
                                break;
                            case AMBIENT_AIR_TEMP:
                                if (ambientAirTempBuffer.size() > BUFFER_SIZE) {
                                    addSampleToAmbientAirTempBuffer(data.value);
                                } else {
                                    App.getDatabase().getObdAmbientAirTempProbeRecordingDao().addAll(ambientAirTempBuffer);
                                    ambientAirTempBuffer.clear();
                                    addSampleToAmbientAirTempBuffer(data.value);
                                }
                                break;
                        }
                    });
                }
            }
    }

    @Override
    public void onDeviceMalfunction() {
        //Ignore
    }

    @MainThread
    @Override
    public void startRecord() {
        if (threadPool != null && !threadPool.isTerminating())
            threadPool.execute(() -> {
                synchronized (this) {
                    _currentStatus = ObdProbesRecordingContract.RecordStatus.RECORDING;
                    notifyRecordStatusChanged();
                }
            });
    }

    @MainThread
    @Override
    public void stopRecord() {
        threadPool.execute(() -> {
            synchronized (this) {
                forcePersistBuffer();
                _currentStatus = ObdProbesRecordingContract.RecordStatus.NOT_RECORDING;
                notifyRecordStatusChanged();
            }
        });
    }

    @MainThread
    @Override
    public void forcePersistBuffer() {
        if (threadPool != null && !threadPool.isTerminating()) {
            threadPool.execute(this::persistBuffers);
        }
    }

    @WorkerThread
    private void persistBuffers() {
        synchronized (this) {
            if (!speedBuffer.isEmpty()) {
                App.getDatabase().getObdSpeedProbeRecordingDao().addAll(speedBuffer);
                speedBuffer.clear();
            }

            if (!rpmBuffer.isEmpty()) {
                App.getDatabase().getObdRpmProbeRecordingDao().addAll(rpmBuffer);
                rpmBuffer.clear();
            }

            if (!mafBuffer.isEmpty()) {
                App.getDatabase().getObdMafProbeRecordingDao().addAll(mafBuffer);
                mafBuffer.clear();
            }

            if (!coolantTempBuffer.isEmpty()) {
                App.getDatabase().getObdCoolantTempProbeRecordingDao().addAll(coolantTempBuffer);
                coolantTempBuffer.clear();
            }

            if (!loadBuffer.isEmpty()) {
                App.getDatabase().getObdLoadProbeRecordingDao().addAll(loadBuffer);
                loadBuffer.clear();
            }

            if (!barometricPressBuffer.isEmpty()) {
                App.getDatabase().getObdBarometricPressProbeRecordingDao().addAll(barometricPressBuffer);
                barometricPressBuffer.clear();
            }

            if (!oilTempBuffer.isEmpty()) {
                App.getDatabase().getObdOilTempProbeRecordingDao().addAll(oilTempBuffer);
                oilTempBuffer.clear();
            }

            if (!ambientAirTempBuffer.isEmpty()) {
                App.getDatabase().getObdAmbientAirTempProbeRecordingDao().addAll(ambientAirTempBuffer);
                ambientAirTempBuffer.clear();
            }
        }
    }

    @MainThread
    private void notifyRecordStatusChanged() {
        threadPool.execute(() -> {
            synchronized (this) {
                StreamSupport.stream(getListeners())
                        .filter(listener -> listener instanceof IObdProbesRecordingListener)
                        .map(listener -> (IObdProbesRecordingListener) listener)
                        .forEach(iObdReadListener -> iObdReadListener.onNewObdProbesRecordStatus(_currentStatus));
            }
        });
    }

    @MainThread
    @WorkerThread
    private void notifySamplesCountChanged(ObdParamType paramType, int bufferSize) {
        threadPool.execute(() -> {
            synchronized (this) {
                StreamSupport.stream(getListeners())
                        .filter(listener -> listener instanceof IObdProbesRecordingListener)
                        .map(listener -> (IObdProbesRecordingListener) listener)
                        .forEach(listener -> {
                            int databaseCount;
                            switch (paramType) {
                                case SPEED:
                                    databaseCount = App.getDatabase().getObdSpeedProbeRecordingDao().getProbesCount();
                                    break;
                                case ENGINE_RPM:
                                    databaseCount = App.getDatabase().getObdRpmProbeRecordingDao().getProbesCount();
                                    break;
                                case MAF:
                                    databaseCount = App.getDatabase().getObdMafProbeRecordingDao().getProbesCount();
                                    break;
                                case COOLANT_TEMP:
                                    databaseCount = App.getDatabase().getObdCoolantTempProbeRecordingDao().getProbesCount();
                                    break;
                                case ENGINE_LOAD:
                                    databaseCount = App.getDatabase().getObdLoadProbeRecordingDao().getProbesCount();
                                    break;
                                case BAROMETRIC_PRESSURE:
                                    databaseCount = App.getDatabase().getObdBarometricPressProbeRecordingDao().getProbesCount();
                                    break;
                                case OIL_TEMP:
                                    databaseCount = App.getDatabase().getObdOilTempProbeRecordingDao().getProbesCount();
                                    break;
                                case AMBIENT_AIR_TEMP:
                                    databaseCount = App.getDatabase().getObdAmbientAirTempProbeRecordingDao().getProbesCount();
                                    break;
                                default:
                                    databaseCount = 0;
                                    break;
                            }

                            listener.onNewSamplesCount(paramType, databaseCount + bufferSize);
                        });
            }
        });
    }

    @WorkerThread
    private void addSampleToSpeedBuffer(float newValue) {
        speedBuffer.add(new ObdSpeedProbeRecordingEntity(System.currentTimeMillis(), newValue));
        notifySamplesCountChanged(ObdParamType.SPEED, speedBuffer.size());
    }

    @WorkerThread
    private void addSampleToRpmBuffer(float newValue) {
        rpmBuffer.add(new ObdRpmProbeRecordingEntity(System.currentTimeMillis(), newValue));
        notifySamplesCountChanged(ObdParamType.ENGINE_RPM, rpmBuffer.size());
    }

    @WorkerThread
    private void addSampleToMafBuffer(float newValue) {
        mafBuffer.add(new ObdMafProbeRecordingEntity(System.currentTimeMillis(), newValue));
        notifySamplesCountChanged(ObdParamType.MAF, mafBuffer.size());
    }

    @WorkerThread
    private void addSampleToCoolantTempBuffer(float newValue) {
        coolantTempBuffer.add(new ObdCoolantTempProbeRecordingEntity(System.currentTimeMillis(), newValue));
        notifySamplesCountChanged(ObdParamType.COOLANT_TEMP, coolantTempBuffer.size());
    }

    @WorkerThread
    private void addSampleToLoadBuffer(float newValue) {
        loadBuffer.add(new ObdLoadProbeRecordingEntity(System.currentTimeMillis(), newValue));
        notifySamplesCountChanged(ObdParamType.ENGINE_LOAD, loadBuffer.size());
    }

    @WorkerThread
    private void addSampleToBarometricPressBuffer(float newValue) {
        barometricPressBuffer.add(new ObdBarometricPressProbeRecordingEntity(System.currentTimeMillis(), newValue));
        notifySamplesCountChanged(ObdParamType.BAROMETRIC_PRESSURE, barometricPressBuffer.size());
    }

    @WorkerThread
    private void addSampleToOilTempBuffer(float newValue) {
        oilTempBuffer.add(new ObdOilTempProbeRecordingEntity(System.currentTimeMillis(), newValue));
        notifySamplesCountChanged(ObdParamType.OIL_TEMP, oilTempBuffer.size());
    }

    @WorkerThread
    private void addSampleToAmbientAirTempBuffer(float newValue) {
        ambientAirTempBuffer.add(new ObdAmbientAirTempProbeRecordingEntity(System.currentTimeMillis(), newValue));
        notifySamplesCountChanged(ObdParamType.AMBIENT_AIR_TEMP, ambientAirTempBuffer.size());
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

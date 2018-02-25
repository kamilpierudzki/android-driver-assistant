package com.google.kpierudzki.driverassistant.eco_driving.service;

import android.location.Location;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.eco_driving.connector.IEcoDrivingListener;
import com.google.kpierudzki.driverassistant.eco_driving.connector.IEcoDrivingObservable;
import com.google.kpierudzki.driverassistant.eco_driving.helper.EcoDrivingCalculatorCallbacks;
import com.google.kpierudzki.driverassistant.eco_driving.helper.EcoDrivingObdCalculator;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadListener;
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadObservable;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.IObdCommonListener;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterState;
import com.google.kpierudzki.driverassistant.service.manager.BaseServiceManager;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

import org.jetbrains.annotations.NotNull;

import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 05.10.2017.
 */

public class EcoDrivingObdBasedManager extends BaseServiceManager implements IEcoDrivingObservable,
        IGeoSampleListener, IObdReadListener, EcoDrivingCalculatorCallbacks {

    private final static int ECO_DRIVING_PROBES_BUFFER_LIMIT = 300;

    private IObdReadObservable _obdReadObservable;
    private EcoDrivingObdCalculator ecoDrivingCalculator;
    private boolean isManagerAllowedToWork = false;

    public EcoDrivingObdBasedManager() {
        super(new ManagerConnectorType[]{ManagerConnectorType.EcoDrivingObd});
        ecoDrivingCalculator = new EcoDrivingObdCalculator(App.getDatabase().getEcoDrivingDao(), this,
                ECO_DRIVING_PROBES_BUFFER_LIMIT);
    }

    @MainThread
    @Override
    public void onDestroy() {
        if (threadPool != null && !threadPool.isTerminating()) {
            threadPool.execute(() -> {
                synchronized (this) {
                    _obdReadObservable = null;
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
    public IBaseManagerObservable provideObservable(@NonNull ManagerConnectorType connectorType) {
        return this;
    }

    @WorkerThread
    @Override
    public void onNewData(GeoSamplesSwappableData newData) {
        if (isManagerAllowedToWork) {
            if (threadPool != null && !threadPool.isTerminating())
                threadPool.execute(() -> ecoDrivingCalculator.updateGeoData(newData));
        }
    }

    @Override
    public void onGpsProviderStateChanged(GpsProviderState state) {
        //Ignore
    }

    @Override
    public void onRawLocation(Location location) {
        // Ignore
    }

    @WorkerThread
    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        synchronized (this) {
            switch (state) {
                case Connected:
                    isManagerAllowedToWork = true;
                    break;
                default:
                    isManagerAllowedToWork = false;
                    break;
            }
        }
    }

    @WorkerThread
    @Override
    public void onNewObdData(ObdCommandModel data) {
        if (isManagerAllowedToWork && data.paramType == ObdParamType.SPEED)
            ecoDrivingCalculator.onNewSpeed(data.value);
    }

    @Override
    public void onDeviceMalfunction() {
        notifyListenersWithDeviceMalfunction();
    }

    @WorkerThread
    private void notifyListenersWithDeviceMalfunction() {
        synchronized (this) {
            StreamSupport.stream(getListeners())
                    .filter(listener -> listener instanceof IObdCommonListener)
                    .map(listener -> (IObdCommonListener) listener)
                    .forEach(IObdCommonListener::onDeviceMalfunction);
        }
    }

    @Override
    public void onSpeedChanged(float speed) {
        notifyListenersWithSpeedChange(speed);
    }

    @Override
    public void onAccelerationChanged(float acceleration) {
        notifyListenersWithAccelerationChange(acceleration);
    }

    @Override
    public void onAvgScoreChanged(float score) {
        notifyListenersWithAvgScoreChange(score);
    }

    @WorkerThread
    private void notifyListenersWithAccelerationChange(float newAcceleration) {
        synchronized (this) {
            StreamSupport.stream(getListeners())
                    .filter(listener -> listener instanceof IEcoDrivingListener)
                    .map(listener -> (IEcoDrivingListener) listener)
                    .forEach(listener -> listener.onAccelerationChanged(newAcceleration));
        }
    }

    @WorkerThread
    private void notifyListenersWithAvgScoreChange(float newScore) {
        synchronized (this) {
            StreamSupport.stream(getListeners())
                    .filter(listener -> listener instanceof IEcoDrivingListener)
                    .map(listener -> (IEcoDrivingListener) listener)
                    .forEach(listener -> listener.onAvgScoreChanged(newScore));
        }
    }

    @WorkerThread
    private void notifyListenersWithSpeedChange(float newSpeed) {
        synchronized (this) {
            StreamSupport.stream(getListeners())
                    .filter(listener -> listener instanceof IEcoDrivingListener)
                    .map(listener -> (IEcoDrivingListener) listener)
                    .forEach(listener -> listener.onSpeedChanged(newSpeed));
        }
    }

    @Override
    public void forcePersistBuffer(boolean async) {
        if (async) {
            if (threadPool != null) {
                threadPool.execute(this::persistBuffers);
            }
        } else {
            persistBuffers();
        }
    }

    @Override
    public void onPermissionGranted() {
        //Ignore
    }

    @WorkerThread
    private void persistBuffers() {
        synchronized (this) {
            if (ecoDrivingCalculator != null) ecoDrivingCalculator.forcePersistBuffer();
            if (_obdReadObservable != null) _obdReadObservable.forcePersistBuffer(false);
        }
    }

    @MainThread
    public void setObdReadObservable(IObdReadObservable obdReadObservable) {
        this._obdReadObservable = obdReadObservable;
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

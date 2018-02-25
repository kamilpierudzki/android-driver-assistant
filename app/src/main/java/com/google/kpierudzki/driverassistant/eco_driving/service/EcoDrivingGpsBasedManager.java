package com.google.kpierudzki.driverassistant.eco_driving.service;

import android.location.Location;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.eco_driving.EcoDrivingContract;
import com.google.kpierudzki.driverassistant.eco_driving.connector.IEcoDrivingListener;
import com.google.kpierudzki.driverassistant.eco_driving.connector.IEcoDrivingObservable;
import com.google.kpierudzki.driverassistant.eco_driving.helper.EcoDrivingCalculatorCallbacks;
import com.google.kpierudzki.driverassistant.eco_driving.helper.EcoDrivingGpsCalculator;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleObservable;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.IObdCommonListener;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterState;
import com.google.kpierudzki.driverassistant.service.manager.BaseServiceManager;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

import org.jetbrains.annotations.NotNull;

import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 03.07.2017.
 */

public class EcoDrivingGpsBasedManager extends BaseServiceManager implements IEcoDrivingObservable,
        EcoDrivingCalculatorCallbacks, IGeoSampleListener, IObdCommonListener {

    private final static int ECO_DRIVING_PROBES_BUFFER_LIMIT = 300;

    private IGeoSampleObservable geoSampleObservable;
    private EcoDrivingGpsCalculator ecoDrivingCalculator;
    private boolean isManagerAllowedToWork = false;
    private GpsProviderState _currentState;

    public EcoDrivingGpsBasedManager() {
        super(new ManagerConnectorType[]{ManagerConnectorType.EcoDrivingGps});
        _currentState = GpsProviderState.Disabled;
        ecoDrivingCalculator = new EcoDrivingGpsCalculator(App.getDatabase().getEcoDrivingDao(), this,
                ECO_DRIVING_PROBES_BUFFER_LIMIT);
    }

    @MainThread
    @Override
    public void onDestroy() {
        if (threadPool != null && !threadPool.isTerminating()) {
            threadPool.execute(() -> {
                synchronized (this) {
                    geoSampleObservable = null;
                    persistBuffers();
                    MainThreadUtil.post(super::onDestroy);
                }
            });
        } else {
            super.onDestroy();
        }
    }

    @MainThread
    @Override
    public void addListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManagerListener listener) {
        super.addListener(connectorType, listener);
        notifyListenerWithConnectionStateChanged(listener);
        notifyWithProviderState(listener);
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

    @MainThread
    @Override
    public void onGpsProviderStateChanged(GpsProviderState state) {
        _currentState = state;
        notifyListenersWithGpsProviderStateChange(state);
    }

    @Override
    public void onRawLocation(Location location) {
        // Ignore
    }

    @Override
    public void forcePersistBuffer(boolean async) {
        if (async) {
            if (threadPool != null && !threadPool.isTerminating()) {
                threadPool.execute(this::persistBuffers);
            }
        } else {
            persistBuffers();
        }
    }

    @Override
    public void onPermissionGranted() {
        if (geoSampleObservable != null) geoSampleObservable.onPermissionGranted();
    }

    @WorkerThread
    private void persistBuffers() {
        synchronized (this) {
            if (geoSampleObservable != null) geoSampleObservable.forcePersistBuffer(false);
            if (ecoDrivingCalculator != null) ecoDrivingCalculator.forcePersistBuffer();
        }
    }

    @MainThread
    public void setGeoSampleObservable(IGeoSampleObservable geoSampleObservable) {
        this.geoSampleObservable = geoSampleObservable;
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

    @WorkerThread
    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        synchronized (this) {
            switch (state) {
                case Connected:
                    isManagerAllowedToWork = false;
                    break;
                default:
                    isManagerAllowedToWork = true;
                    break;
            }
            notifyListenersWithConnectionStateChanged();
        }
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

    @MainThread
    private void notifyListenersWithGpsProviderStateChange(GpsProviderState state) {
        if (state != null) {
            synchronized (this) {
                StreamSupport.stream(getListeners())
                        .filter(listener -> listener instanceof IEcoDrivingListener)
                        .map(listener -> (IEcoDrivingListener) listener)
                        .forEach(listener -> listener.onGpsProviderStateChanged(state));
            }
        }
    }

    @MainThread
    @WorkerThread
    private void notifyListenersWithConnectionStateChanged() {
        if (threadPool != null && !threadPool.isTerminating())
            threadPool.execute(() -> {
                synchronized (this) {
                    EcoDrivingContract.EcoDrivingDataProvider provider;
                    if (isManagerAllowedToWork)
                        provider = EcoDrivingContract.EcoDrivingDataProvider.Gps;
                    else
                        provider = EcoDrivingContract.EcoDrivingDataProvider.Obd;

                    StreamSupport.stream(getListeners())
                            .filter(listener -> listener instanceof IEcoDrivingListener)
                            .map(listener -> (IEcoDrivingListener) listener)
                            .forEach(listener -> listener.onDataProviderChanged(provider));
                }
            });
    }

    @MainThread
    @WorkerThread
    private void notifyListenerWithConnectionStateChanged(IBaseManager.IBaseManagerListener listener) {
        if (threadPool != null && !threadPool.isTerminating())
            threadPool.execute(() -> {
                synchronized (this) {
                    EcoDrivingContract.EcoDrivingDataProvider provider;
                    if (isManagerAllowedToWork)
                        provider = EcoDrivingContract.EcoDrivingDataProvider.Gps;
                    else
                        provider = EcoDrivingContract.EcoDrivingDataProvider.Obd;

                    if (listener instanceof IEcoDrivingListener)
                        ((IEcoDrivingListener) listener).onDataProviderChanged(provider);
                }
            });
    }

    @MainThread
    private void notifyWithProviderState(IBaseManagerListener listener) {
        if (listener instanceof IEcoDrivingListener)
            ((IEcoDrivingListener) listener).onGpsProviderStateChanged(_currentState);
    }

    @Override
    public void onBluetoothAdapterStateChanged(@NotNull BluetoothAdapterState state) {
        //Ignore
    }
}

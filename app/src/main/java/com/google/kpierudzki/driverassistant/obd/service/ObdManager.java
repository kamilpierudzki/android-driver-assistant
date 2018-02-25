package com.google.kpierudzki.driverassistant.obd.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.location.Location;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.GlobalConfig;
import com.google.kpierudzki.driverassistant.dtc.datamodel.DtcModel;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleObservable;
import com.google.kpierudzki.driverassistant.obd.connect.connector.IObdConnectListener;
import com.google.kpierudzki.driverassistant.obd.connect.connector.IObdConnectObservable;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadListener;
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadObservable;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.service.database.DtcSaver;
import com.google.kpierudzki.driverassistant.obd.service.database.ObdProbesToDbSaver;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.ConnectManager;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.IObdCommonListener;
import com.google.kpierudzki.driverassistant.obd.service.provider.DtcProvider;
import com.google.kpierudzki.driverassistant.obd.service.provider.MruProtocolProvider;
import com.google.kpierudzki.driverassistant.obd.start.ObdStartContract;
import com.google.kpierudzki.driverassistant.obd.start.connector.IObdStartListener;
import com.google.kpierudzki.driverassistant.obd.start.connector.IObdStartObservable;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterHelper;
import com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterState;
import com.google.kpierudzki.driverassistant.service.helper.Callbacks;
import com.google.kpierudzki.driverassistant.service.manager.BaseServiceManager;
import com.google.kpierudzki.driverassistant.service.mock.obd.MockConnectManager;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

import java.util.List;

import java8.util.J8Arrays;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 09.09.2017.
 */

public class ObdManager extends BaseServiceManager implements IObdStartObservable, IObdConnectObservable,
        IObdReadObservable, ConnectManager.Callbacks, IGeoSampleListener, Callbacks {

    private final static int OBD_PROBES_BUFFER_LIMIT = 300;

    private volatile boolean isManagerWorking = false;
    private BluetoothDevice device;
    private ObdProtocol protocol;
    private ConnectionState _currentConnectionState = ConnectionState.Failed;
    private IGeoSampleObservable geoSampleObservable;
    private ObdProbesToDbSaver obdProbesToDbSaver;
    private DtcProvider dtcProvider;
    private DtcSaver dtcSaver;
    private ConnectManager connectManager;
    private BluetoothAdapterHelper _btAdapterHelper;

    public ObdManager(Context context) {
        super(new ManagerConnectorType[]{
                ManagerConnectorType.ObdStart,
                ManagerConnectorType.ObdConnect,
                ManagerConnectorType.ObdRead});
        dtcProvider = new DtcProvider();
        obdProbesToDbSaver = new ObdProbesToDbSaver(App.getDatabase().getObdParamsDao(), OBD_PROBES_BUFFER_LIMIT);
        dtcSaver = new DtcSaver();
        _btAdapterHelper = new BluetoothAdapterHelper(context, this);
    }

    @MainThread
    @Override
    public void onDestroy() {
        if (threadPool != null && !threadPool.isTerminating()) {
            threadPool.execute(() -> {
                synchronized (this) {
                    geoSampleObservable = null;
                    isManagerWorking = false;

                    if (_btAdapterHelper != null) _btAdapterHelper.onDestroy();
                    persistBuffers();
                    if (connectManager != null) {
                        connectManager.onDestroy();
                        connectManager = null;
                    }
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

    @MainThread
    @Override
    public void addListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManagerListener listener) {
        super.addListener(connectorType, listener);
        notifyListenersWithConnectionConfig(device, protocol);
        connectionStateChanged(_currentConnectionState);
        _btAdapterHelper.checkStateAndReturnAdapter(true);
    }

    @MainThread
    @Override
    public void provideBluetoothDevices() {
        threadPool.execute(() -> {
            BluetoothAdapter adapter = _btAdapterHelper.checkStateAndReturnAdapter(false);
            if (adapter != null) notifyListenersWithBondedDevices(getBondedDevices(adapter));
        });
    }

    @NonNull
    private List<ObdStartContract.BluetoothDeviceModel> getBondedDevices(BluetoothAdapter adapter) {
        return StreamSupport.stream(adapter.getBondedDevices()).map(ObdStartContract.BluetoothDeviceModel::new)
                .collect(Collectors.toList());
    }

    @Override
    public void connectToDevice(BluetoothDevice device, ObdProtocol protocol) {
        threadPool.execute(() -> {
            this.device = device;
            this.protocol = protocol;
            isManagerWorking = true;

            if (GlobalConfig.DEMO_MODE) {
                connectManager = new MockConnectManager(
                        threadPool, device, new MruProtocolProvider(
                        protocol, App.getDatabase().getMruDao()), this);
            } else
                connectManager = new ConnectManager(
                        threadPool, device, new MruProtocolProvider(
                        protocol, App.getDatabase().getMruDao()), this);

            connectManager.connect();
        });
    }

    @MainThread
    @Override
    public void disconnect() {
        isManagerWorking = false;
    }

    @Override
    public void onPermissionGranted() {
        if (geoSampleObservable != null) geoSampleObservable.onPermissionGranted();
        _btAdapterHelper.checkStateAndReturnAdapter(true);
    }

    @WorkerThread
    @Override
    public boolean isManagerWorking() {
        BluetoothAdapter adapter = _btAdapterHelper.checkStateAndReturnAdapter(false);
        return adapter != null && adapter.isEnabled() && isManagerWorking && !Thread.interrupted();
    }

    @WorkerThread
    @Override
    public void onConnectionStateChanged(@NonNull ConnectionState state) {
        this._currentConnectionState = state;
        notifyListenersWithState(state);
    }

    @WorkerThread
    @Override
    public void onNewObdData(ObdCommandModel data) {
        notifyListenersWithData(data);
        obdProbesToDbSaver.onNewObdData(data);

        if (data.paramType == ObdParamType.TROUBLE_CODES) {
            TroubleCodesCommand dtcCommand = (TroubleCodesCommand) data.command;
            String codes = dtcCommand.getCalculatedResult();
            dtcCommand.clearDtc();

            if (codes != null && !codes.isEmpty()) {
                if (dtcSaver.addModels(
                        J8Arrays.stream(codes.split("\n"))
                                .map(s -> new DtcModel(s, dtcProvider.getDescription(s)))
                                .collect(Collectors.toList())))
                    notifyListenersWithDtc();
            }
        }
    }

    @WorkerThread
    @Override
    public void onConnectionInfo(String info) {
        notifyListenersWithInfo(info);
    }

    @WorkerThread
    @Override
    public void onDeviceMalfunction() {
        notifyListenersWithDeviceMalfunction();
    }

    @WorkerThread
    @Override
    public void onConnectFailed(@NonNull ConnectManager currentConnectManager) {
        currentConnectManager.onDestroy();
        onConnectionStateChanged(ConnectionState.Failed);
    }

    @WorkerThread
    private void notifyListenersWithBondedDevices(List<ObdStartContract.BluetoothDeviceModel> devices) {
        synchronized (this) {
            StreamSupport.stream(getListeners())
                    .filter(listener -> listener instanceof IObdStartListener)
                    .map(listener -> (IObdStartListener) listener)
                    .forEach(listener -> listener.onDevicesLoaded(
                            devices, ObdProtocol.values()));
        }
    }

    @WorkerThread
    private void notifyListenersWithInfo(String info) {
        synchronized (this) {
            StreamSupport.stream(getListeners())
                    .filter(listener -> listener instanceof IObdConnectListener)
                    .map(listener -> (IObdConnectListener) listener)
                    .forEach(listener -> listener.onConnectionInfo(info));
        }
    }

    @WorkerThread
    private void notifyListenersWithState(@NonNull ConnectionState state) {
        synchronized (this) {
            StreamSupport.stream(getListeners())
                    .filter(listener -> listener instanceof IObdCommonListener)
                    .map(listener -> (IObdCommonListener) listener)
                    .forEach(listener -> listener.onConnectionStateChanged(state));
        }
    }

    @WorkerThread
    private void notifyListenersWithData(ObdCommandModel data) {
        synchronized (this) {
            StreamSupport.stream(getListeners())
                    .filter(listener -> listener instanceof IObdReadListener)
                    .map(listener -> (IObdReadListener) listener)
                    .forEach(listener -> listener.onNewObdData(data));
        }
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
    private void notifyListenersWithConnectionConfig(BluetoothDevice bluetoothDevice, ObdProtocol protocol) {
        if (threadPool != null && !threadPool.isTerminating())
            threadPool.execute(() -> {
                synchronized (this) {
                    StreamSupport.stream(getListeners())
                            .filter(listener -> listener instanceof IObdConnectListener)
                            .map(listener -> (IObdConnectListener) listener)
                            .forEach(listener -> listener.onConnectingDevice(bluetoothDevice, protocol));
                }
            });
    }

    @WorkerThread
    private void notifyListenersWithDtc() {
        synchronized (this) {
            StreamSupport.stream(getListeners())
                    .filter(listener -> listener instanceof IObdReadListener)
                    .map(listener -> (IObdReadListener) listener)
                    .forEach(IObdReadListener::onNewDtcDetected);
        }
    }

    @WorkerThread
    @Override
    public void onNewData(GeoSamplesSwappableData newData) {
        if (threadPool != null && _currentConnectionState == ConnectionState.Connected)
            threadPool.execute(() -> obdProbesToDbSaver.updateGeoData(newData));
    }

    @Override
    public void onGpsProviderStateChanged(GpsProviderState state) {
        //ignore
    }

    @Override
    public void onRawLocation(Location location) {
        // Ignore
    }

    @MainThread
    public void setGeoSampleObservable(IGeoSampleObservable geoSampleObservable) {
        this.geoSampleObservable = geoSampleObservable;
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

    @MainThread
    @Override
    public void clearAllDtc() {
        if (obdProbesToDbSaver != null) obdProbesToDbSaver.clearAllDtc();
        if (connectManager != null) connectManager.clearAllDtc();
    }

    @MainThread
    @Override
    public boolean dtcDetected() {
        synchronized (this) {
            return dtcSaver.dtcDetected();
        }
    }

    @WorkerThread
    private void persistBuffers() {
        synchronized (this) {
            if (obdProbesToDbSaver != null) obdProbesToDbSaver.forcePersistBuffer(false);
            dtcSaver.forcePersistBuffer(false);
            if (geoSampleObservable != null) geoSampleObservable.forcePersistBuffer(false);
        }
    }

    @MainThread
    @Override
    public void onBluetoothAdapterStateChanged(@NonNull BluetoothAdapterState state) {
        if (state == BluetoothAdapterState.Disabled) isManagerWorking = false;
        notifyListenersWithAdapterState(state);
    }

    @MainThread
    private void notifyListenersWithAdapterState(BluetoothAdapterState state) {
        synchronized (this) {
            StreamSupport.stream(getListeners())
                    .filter(listener -> listener instanceof IObdCommonListener)
                    .map(listener -> (IObdCommonListener) listener)
                    .forEach(listener -> listener.onBluetoothAdapterStateChanged(state));
        }
    }

    public enum ConnectionState {
        Connecting, Connected, Failed
    }

    @MainThread
    private void connectionStateChanged(@NonNull ConnectionState state) {
        if (threadPool != null && !threadPool.isTerminating()) {
            threadPool.execute(() -> onConnectionStateChanged(state));
        }
    }
}

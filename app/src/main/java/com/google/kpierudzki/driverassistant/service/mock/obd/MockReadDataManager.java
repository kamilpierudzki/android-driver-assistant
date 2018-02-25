package com.google.kpierudzki.driverassistant.service.mock.obd;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.SpeedCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.LoadCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.MassAirFlowCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.OilTempCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.RPMCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.pressure.BarometricPressureCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.temperature.AmbientAirTemperatureCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.temperature.EngineCoolantTemperatureCommand;
import com.google.kpierudzki.driverassistant.obd.service.obdmanager.ReadDataManager;
import com.google.kpierudzki.driverassistant.obd.service.provider.BluetoothSocketSppProvider;
import com.google.kpierudzki.driverassistant.obd.service.provider.MruProtocolProvider;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Kamil on 13.11.2017.
 */

public class MockReadDataManager extends ReadDataManager {

    private int globalIdx = 0;
    private List<ObdMockDataProvider.Probe> probes;
    private Handler threadHandler;

    MockReadDataManager(ThreadPoolExecutor workerThreadHandler, BluetoothSocketSppProvider bluetoothSocketSppProvider,
                        MruProtocolProvider mruProtocolProvider, Callbacks callbacks) {
        super(workerThreadHandler, bluetoothSocketSppProvider, mruProtocolProvider, callbacks);

        probes = ObdMockDataProvider.prepareData();
        HandlerThread handlerThread = new HandlerThread("MockReadDataManager_thread",
                Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        threadHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (threadHandler != null) {
            threadHandler.removeCallbacksAndMessages(null);
            threadHandler.getLooper().quit();
            threadHandler = null;
        }
    }

    @WorkerThread
    @Override
    public void readData() {
        callbacks.onConnectionStateChanged(ObdManager.ConnectionState.Connected);
        startProduceData();
    }

    @WorkerThread
    private void startProduceData() {
        if (threadHandler != null) {
            if (globalIdx < probes.size()) {
                threadHandler.postDelayed(() -> {
                    ObdCommandModel command = buildCommand(probes.get(globalIdx));
                    if (command != null) callbacks.onNewObdData(command);

                    if (callbacks.isManagerWorking()) {
                        globalIdx++;
                        startProduceData();
                    } else {
                        callbacks.onConnectionStateChanged(ObdManager.ConnectionState.Connecting);
                        callbacks.onReadDataFailed();
                    }
                }, probes.get(globalIdx).getDelay());
            } else if (callbacks.isManagerWorking()) {
                globalIdx = 0;
                startProduceData();
            } else {
                callbacks.onConnectionStateChanged(ObdManager.ConnectionState.Connecting);
                callbacks.onReadDataFailed();
            }
        }
    }

    @Nullable
    private ObdCommandModel buildCommand(ObdMockDataProvider.Probe probe) {
        ObdCommandModel command = null;
        switch (probe.paramType) {
            case SPEED:
                command = new SpeedCommand();
                command.value = probe.value;
                break;
            case ENGINE_RPM:
                command = new RPMCommand();
                command.value = probe.value;
                break;
            case MAF:
                command = new MassAirFlowCommand();
                command.value = probe.value;
                break;
            case COOLANT_TEMP:
                command = new EngineCoolantTemperatureCommand();
                command.value = probe.value;
                break;
            case ENGINE_LOAD:
                command = new LoadCommand();
                command.value = probe.value;
                break;
            case BAROMETRIC_PRESSURE:
                command = new BarometricPressureCommand();
                command.value = probe.value;
                break;
            case OIL_TEMP:
                command = new OilTempCommand();
                command.value = probe.value;
                break;
            case AMBIENT_AIR_TEMP:
                command = new AmbientAirTemperatureCommand();
                command.value = probe.value;
                break;
        }
        return command;
    }
}

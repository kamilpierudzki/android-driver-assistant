package com.google.kpierudzki.driverassistant.obd.service.obdmanager;

import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.initialization.AdaptiveTimingAuto1;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.initialization.DescribeProtocolNumber;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.initialization.HeadersOffCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol.EchoOffCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol.LineFeedOffCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol.ObdResetCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol.ProtocolMemoryOffCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol.SelectProtocolCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol.SpacesOffObdCommand;
import com.google.kpierudzki.driverassistant.obd.service.provider.BluetoothSocketSppProvider;
import com.google.kpierudzki.driverassistant.obd.service.provider.MruProtocolProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Kamil on 15.09.2017.
 */

public class InitializeManager extends CommonCommandsManager implements TestManager.Callbacks, IDtcRemovable {

    private final static int EACH_COMMAND_TRIES_LIMIT = 3;

    protected TestManager testManager;

    protected Callbacks callbacks;
    protected List<ObdCommandModel> staticInitCommands;

    public InitializeManager(ThreadPoolExecutor workerThreadHandler, BluetoothSocketSppProvider bluetoothSocketSppProvider,
                             MruProtocolProvider mruProtocolProvider, Callbacks callbacks) {
        super(workerThreadHandler, bluetoothSocketSppProvider, mruProtocolProvider);
        this.callbacks = callbacks;
        testManager = new TestManager(workerThreadHandler, bluetoothSocketSppProvider, mruProtocolProvider, this);

        staticInitCommands = new ArrayList<ObdCommandModel>() {{
            add(new ObdResetCommand());
            add(new EchoOffCommand());
            add(new ProtocolMemoryOffCommand());
            add(new LineFeedOffCommand());
            add(new SpacesOffObdCommand());
            add(new HeadersOffCommand());
            add(new AdaptiveTimingAuto1());
            add(new DescribeProtocolNumber());
        }};
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (testManager != null) testManager.onDestroy();
    }

    @WorkerThread
    public void initialize() {
        if (callbacks.isManagerWorking()) {
            callbacks.onConnectionInfo(App.getAppContext().getString(R.string.Obd_Connect_Step_Initializing,
                    mruProtocolProvider.getProtocol().toString()));

            boolean isOk = false;

            List<ObdCommandModel> commands = new ArrayList<ObdCommandModel>(staticInitCommands) {{
                add(new SelectProtocolCommand(ObdProtocol.getProtocolObjectAtIndex(
                        mruProtocolProvider.getProtocol().protocolNumber)));
            }};

            for (int i = 0; i < commands.size() && callbacks.isManagerWorking(); i++) {
                isOk = false;
                for (int j = 0; j < EACH_COMMAND_TRIES_LIMIT && callbacks.isManagerWorking(); j++) {
                    if (queryCommand(commands.get(i), callbacks))
                        isOk = commands.get(i).isOK();

                    //If response is correct
                    if (isOk) break;
                }

                //If fail occurred during init
                if (!isOk) break;
            }

            if (isOk) testManager.test();
            else callbacks.onInitializeFail();
        } else {
            callbacks.onInitializeFail();
        }
    }

    @Override
    public boolean isManagerWorking() {
        return callbacks.isManagerWorking();
    }

    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        callbacks.onConnectionStateChanged(state);
    }

    @Override
    public void onNewObdData(ObdCommandModel data) {
        callbacks.onNewObdData(data);
    }

    @Override
    public void onConnectionInfo(String info) {
        callbacks.onConnectionInfo(info);
    }

    @Override
    public void onDeviceMalfunction() {
        callbacks.onDeviceMalfunction();
    }

    @Override
    public void onTestFailed() {
        if (threadPool != null) threadPool.execute(this::initialize);
    }

    @Override
    public void clearAllDtc() {
        if (testManager != null) testManager.clearAllDtc();
    }

    public interface Callbacks extends IObdManagerCallbacks {
        void onInitializeFail();
    }
}

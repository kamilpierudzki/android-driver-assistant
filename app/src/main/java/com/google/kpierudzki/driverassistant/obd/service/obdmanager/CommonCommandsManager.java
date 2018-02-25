package com.google.kpierudzki.driverassistant.obd.service.obdmanager;

import android.util.Log;

import com.github.pires.obd.exceptions.BusInitException;
import com.github.pires.obd.exceptions.MisunderstoodCommandException;
import com.github.pires.obd.exceptions.NoDataException;
import com.github.pires.obd.exceptions.UnsupportedCommandException;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.service.exception.ShiftedResponseException;
import com.google.kpierudzki.driverassistant.obd.service.provider.BluetoothSocketSppProvider;
import com.google.kpierudzki.driverassistant.obd.service.provider.MruProtocolProvider;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Kamil on 15.09.2017.
 */

public class CommonCommandsManager extends BaseObdManager {

    private final static String TAG = "CommonCommandsManager";
    protected BluetoothSocketSppProvider bluetoothSocketSppProvider;
    protected MruProtocolProvider mruProtocolProvider;

    CommonCommandsManager(ThreadPoolExecutor threadPool, BluetoothSocketSppProvider bluetoothSocketSppProvider,
                          MruProtocolProvider mruProtocolProvider) {
        super(threadPool);
        this.bluetoothSocketSppProvider = bluetoothSocketSppProvider;
        this.mruProtocolProvider = mruProtocolProvider;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothSocketSppProvider.closeSocket();
    }

    boolean queryAllCommands(List<ObdCommandModel> commands, IObdManagerCallbacks callbacks) {
        for (int i = 0; i < commands.size() && callbacks.isManagerWorking(); i++)
            if (!queryCommand(commands.get(i), callbacks)) return false;
        return callbacks.isManagerWorking();
    }

    boolean checkCommands(List<ObdCommandModel> commands, IObdManagerCallbacks callbacks) {
        boolean isOK = false;
        //Check any command is OK
        for (int i = 0; i < commands.size() && callbacks.isManagerWorking(); i++)
            isOK = isOK || commands.get(i).isOK();

        //If response of all commands is NO_DATA
        if (!isOK) {
            mruProtocolProvider.incProtocol();
            return false;
        }

        //Special case
        for (ObdCommandModel command : commands) {
            if (command.paramType == ObdParamType.COOLANT_TEMP) {
                //If temperature is lower-equal -30°C, return fail
                if (command.value <= -30) isOK = false;
                break;
            }
        }

        return isOK;
    }

    boolean queryCommand(final ObdCommandModel model, IObdManagerCallbacks callbacks) {
        Log.d(TAG, "queryCommand() - model[" + model.command.getName() + "]");
        try {
            model.query(bluetoothSocketSppProvider.getSocketNew());
            Log.d(TAG, "queryCommand() - model[" + model.command.getName() + "], response[" + model.response + "]");
        } catch (IOException | BusInitException e) {
            Log.d(TAG, "queryCommand() - class[" + e.getClass() + "], name[" + model.command.getName() + "], message[" + e.getLocalizedMessage() + "], {IOException | BusInitException}");
            return false;
        } catch (NoDataException | UnsupportedCommandException | MisunderstoodCommandException e) {
            Log.d(TAG, "queryCommand() - class[" + e.getClass() + "], name[" + model.command.getName() + "], message[" + e.getLocalizedMessage() + "], {NoDataException | UnsupportedCommandException | MisunderstoodCommandException}");
        } catch (ShiftedResponseException e) {
            Log.d(TAG, "queryCommand() - class[" + e.getClass() + "], name[" + model.command.getName() + "], message[" + e.getLocalizedMessage() + "], {ShiftedResponseException}");
            callbacks.onDeviceMalfunction();
            return false;
        } catch (Exception e) {
            Log.d(TAG, "queryCommand() - class[" + e.getClass() + "], name[" + model.command.getName() + "], message[" + e.getLocalizedMessage() + "], {Exception}");
            mruProtocolProvider.incProtocol();
            return false;
        }
        return true;
    }

    public boolean queryCommandLight(final ObdCommandModel model) {
        Log.d(TAG, "queryCommandLight() - model[" + model.command.getName() + "]");
        try {
            model.query(bluetoothSocketSppProvider.getSocketNew());
            Log.d(TAG, "queryCommandLight() - model[" + model.command.getName() + "], response[" + model.response + "]");
            return true;
        } catch (Exception e) {
            Log.d(TAG, "queryCommandLight() - class[" + model.command.getName() + "], message[" + e.getLocalizedMessage() + "], {Exception}");
            return false;
        }
    }
}

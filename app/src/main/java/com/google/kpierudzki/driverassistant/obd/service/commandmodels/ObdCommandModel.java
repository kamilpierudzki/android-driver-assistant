package com.google.kpierudzki.driverassistant.obd.service.commandmodels;

import android.bluetooth.BluetoothSocket;

import com.github.pires.obd.commands.ObdCommand;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.exception.ShiftedResponseException;

/**
 * Created by Kamil on 15.09.2017.
 */
public abstract class ObdCommandModel {

    private final static int SHIFTED_RESPONSE_COUNT_LIMIT = 3;
    public static final String NO_DATA = "NO_DATA";

    public String response = NO_DATA;
    public float value;
    private int shiftedResponseCount;

    public ObdCommand command;
    private boolean parseValue;
    public ObdParamType paramType;

    protected ObdCommandModel(final ObdCommand command, final boolean parseValue, final ObdParamType paramType) {
        this.command = command;
        this.parseValue = parseValue;
        this.paramType = paramType;
        value = 0f;
        shiftedResponseCount = 0;
    }

    public void query(final BluetoothSocket socket) throws Exception {
        try {
            command.run(socket.getInputStream(), socket.getOutputStream());
            response = command.getCalculatedResult();
            if (parseValue) {
                response = response.replaceAll(",", ".");
                value = Float.valueOf(response);
            }
        } catch (NumberFormatException e) {
            //Recognized shifted response
            if (++shiftedResponseCount >= SHIFTED_RESPONSE_COUNT_LIMIT) {
                shiftedResponseCount = 0;
                throw new ShiftedResponseException();
            }
            response = NO_DATA;
            value = 0f;
            throw e;
        } catch (Exception e) {
            response = NO_DATA;
            value = 0f;
            throw e;
        }
    }

    public boolean isOK() {
        return !response.equals(NO_DATA);
    }

    public int getPidNumber() {
        return -1;
    }

    public ObdCommandModel updateValue(float value) {
        this.value = value;
        return this;
    }
}

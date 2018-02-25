package com.google.kpierudzki.driverassistant.obd.service.commandmodels;

import android.bluetooth.BluetoothSocket;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;

/**
 * Created by Kamil on 15.09.2017.
 */
public class SpeedCommand extends ObdCommandModel {

    public SpeedCommand() {
        super(new com.github.pires.obd.commands.SpeedCommand(), true, ObdParamType.SPEED);
    }

    @Override
    public int getPidNumber() {
        return 13;
    }

    @Override
    public void query(BluetoothSocket socket) throws Exception {
        super.query(socket);
        value = value * (0.277778f);//[km/h] -> [m/s]
    }
}

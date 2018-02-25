package com.google.kpierudzki.driverassistant.obd.service.commandmodels.control;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class VinCommand extends ObdCommandModel {

    public VinCommand() {
        super(new com.github.pires.obd.commands.control.VinCommand(), false, ObdParamType.VIN);
    }

    @Override
    public int getPidNumber() {
        return -1;
    }
}

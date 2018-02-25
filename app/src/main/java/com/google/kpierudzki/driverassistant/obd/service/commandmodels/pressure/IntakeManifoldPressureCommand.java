package com.google.kpierudzki.driverassistant.obd.service.commandmodels.pressure;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class IntakeManifoldPressureCommand extends ObdCommandModel {

    public IntakeManifoldPressureCommand() {
        super(new com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand(), true, ObdParamType.IMAP);
    }

    @Override
    public int getPidNumber() {
        return 11;
    }
}

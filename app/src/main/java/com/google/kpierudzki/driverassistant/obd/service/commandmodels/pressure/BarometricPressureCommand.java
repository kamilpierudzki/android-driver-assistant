package com.google.kpierudzki.driverassistant.obd.service.commandmodels.pressure;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class BarometricPressureCommand extends ObdCommandModel {

    public BarometricPressureCommand() {
        super(new com.github.pires.obd.commands.pressure.BarometricPressureCommand(), true, ObdParamType.BAROMETRIC_PRESSURE);
    }

    @Override
    public int getPidNumber() {
        return 51;
    }
}

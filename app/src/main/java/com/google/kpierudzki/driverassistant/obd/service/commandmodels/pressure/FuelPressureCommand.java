package com.google.kpierudzki.driverassistant.obd.service.commandmodels.pressure;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class FuelPressureCommand extends ObdCommandModel {

    public FuelPressureCommand() {
        super(new com.github.pires.obd.commands.pressure.FuelPressureCommand(), true, ObdParamType.FUEL_PRESSURE);
    }

    @Override
    public int getPidNumber() {
        return 10;
    }
}

package com.google.kpierudzki.driverassistant.obd.service.commandmodels.fuel;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class FindFuelTypeCommand extends ObdCommandModel {

    public FindFuelTypeCommand() {
        super(new com.github.pires.obd.commands.fuel.FindFuelTypeCommand(), false, ObdParamType.FUEL_TYPE);
    }

    @Override
    public int getPidNumber() {
        return 81;
    }
}

package com.google.kpierudzki.driverassistant.obd.service.commandmodels.fuel;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class FuelLevelCommand extends ObdCommandModel {

    public FuelLevelCommand() {
        super(new com.github.pires.obd.commands.fuel.FuelLevelCommand(), true, ObdParamType.FUEL_LEVEL);
    }

    @Override
    public int getPidNumber() {
        return 47;
    }
}

package com.google.kpierudzki.driverassistant.obd.service.commandmodels.temperature;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class EngineCoolantTemperatureCommand extends ObdCommandModel {

    public EngineCoolantTemperatureCommand() {
        super(new com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand(), true, ObdParamType.COOLANT_TEMP);
    }

    @Override
    public int getPidNumber() {
        return 5;
    }
}

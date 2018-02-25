package com.google.kpierudzki.driverassistant.obd.service.commandmodels.temperature;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class AmbientAirTemperatureCommand extends ObdCommandModel {

    public AmbientAirTemperatureCommand() {
        super(new com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand(), true, ObdParamType.AMBIENT_AIR_TEMP);
    }

    @Override
    public int getPidNumber() {
        return 70;
    }
}

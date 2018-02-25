package com.google.kpierudzki.driverassistant.obd.service.commandmodels.temperature;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class AirIntakeTemperatureCommand extends ObdCommandModel {

    public AirIntakeTemperatureCommand() {
        super(new com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand(), true, ObdParamType.AIT);
    }

    @Override
    public int getPidNumber() {
        return 15;
    }

}

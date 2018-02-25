package com.google.kpierudzki.driverassistant.obd.service.commandmodels.fuel;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class ConsumptionRateCommand extends ObdCommandModel {

    public ConsumptionRateCommand() {
        super(new com.github.pires.obd.commands.fuel.ConsumptionRateCommand(), true, ObdParamType.CONSUMPTION_RATE);
    }

    @Override
    public int getPidNumber() {
        return 94;
    }
}

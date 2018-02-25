package com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class MassAirFlowCommand extends ObdCommandModel {

    public MassAirFlowCommand() {
        super(new com.github.pires.obd.commands.engine.MassAirFlowCommand(), true, ObdParamType.MAF);
    }

    @Override
    public int getPidNumber() {
        return 16;
    }
}

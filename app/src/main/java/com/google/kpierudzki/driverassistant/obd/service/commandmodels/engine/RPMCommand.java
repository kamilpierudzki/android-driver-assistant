package com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class RPMCommand extends ObdCommandModel {

    public RPMCommand() {
        super(new com.github.pires.obd.commands.engine.RPMCommand(), true, ObdParamType.ENGINE_RPM);
    }

    @Override
    public int getPidNumber() {
        return 12;
    }
}

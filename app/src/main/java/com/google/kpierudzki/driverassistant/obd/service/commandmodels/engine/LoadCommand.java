package com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class LoadCommand extends ObdCommandModel {

    public LoadCommand() {
        super(new com.github.pires.obd.commands.engine.LoadCommand(), true, ObdParamType.ENGINE_LOAD);
    }

    @Override
    public int getPidNumber() {
        return 4;
    }
}

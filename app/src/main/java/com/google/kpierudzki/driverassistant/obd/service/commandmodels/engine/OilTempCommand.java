package com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class OilTempCommand extends ObdCommandModel {

    public OilTempCommand() {
        super(new com.github.pires.obd.commands.engine.OilTempCommand(), true, ObdParamType.OIL_TEMP);
    }

    @Override
    public int getPidNumber() {
        return 92;
    }
}

package com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class ThrottlePositionCommand extends ObdCommandModel {

    public ThrottlePositionCommand() {
        super(new com.github.pires.obd.commands.engine.ThrottlePositionCommand(), true, ObdParamType.THROTTLE_POSITION);
    }

    @Override
    public int getPidNumber() {
        return 17;
    }
}

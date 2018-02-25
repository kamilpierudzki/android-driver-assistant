package com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.service.commands.ProtocolMemoryOff;

/**
 * Created by Kamil on 15.09.2017.
 */
public class ProtocolMemoryOffCommand extends ObdCommandModel {

    public ProtocolMemoryOffCommand() {
        super(new ProtocolMemoryOff(), false, ObdParamType.UNKNOWN);
    }

    @Override
    public boolean isOK() {
        return response.matches("OK");
    }
}

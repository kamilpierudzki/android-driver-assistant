package com.google.kpierudzki.driverassistant.obd.service.commandmodels.initialization;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class HeadersOffCommand extends ObdCommandModel {

    public HeadersOffCommand() {
        super(new com.github.pires.obd.commands.protocol.HeadersOffCommand(), false, ObdParamType.UNKNOWN);
    }

    @Override
    public boolean isOK() {
        return response.matches("OK");
    }
}

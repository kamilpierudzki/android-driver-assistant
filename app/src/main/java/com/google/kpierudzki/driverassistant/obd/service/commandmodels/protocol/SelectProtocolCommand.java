package com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol;

import com.github.pires.obd.enums.ObdProtocols;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class SelectProtocolCommand extends ObdCommandModel {

    public SelectProtocolCommand(final ObdProtocols protocol) {
        super(new com.github.pires.obd.commands.protocol.SelectProtocolCommand(protocol), false, ObdParamType.UNKNOWN);
    }

    @Override
    public boolean isOK() {
        return response.matches("OK");
    }
}

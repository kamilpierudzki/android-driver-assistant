package com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class AvailablePidsCommand_41_60 extends ObdCommandModel {

    public AvailablePidsCommand_41_60() {
        super(new com.github.pires.obd.commands.protocol.AvailablePidsCommand_41_60(), false, ObdParamType.UNKNOWN);//todo !!!
    }
}

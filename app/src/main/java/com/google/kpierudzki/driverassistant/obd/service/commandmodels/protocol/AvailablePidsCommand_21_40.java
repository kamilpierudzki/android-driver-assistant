package com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class AvailablePidsCommand_21_40 extends ObdCommandModel {

    public AvailablePidsCommand_21_40() {
        super(new com.github.pires.obd.commands.protocol.AvailablePidsCommand_21_40(), false, ObdParamType.UNKNOWN);
    }
}

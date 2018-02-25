package com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class ResetTroubleCodesCommand extends ObdCommandModel {

    public ResetTroubleCodesCommand() {
        super(new com.github.pires.obd.commands.protocol.ResetTroubleCodesCommand(), false, ObdParamType.UNKNOWN);
    }
}

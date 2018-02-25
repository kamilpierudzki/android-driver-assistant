package com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 24.12.2017.
 */

public class TroubleCodesCommand extends ObdCommandModel {

    public TroubleCodesCommand() {
        super(new com.github.pires.obd.commands.control.TroubleCodesCommand(), false,
                ObdParamType.TROUBLE_CODES);
    }
}

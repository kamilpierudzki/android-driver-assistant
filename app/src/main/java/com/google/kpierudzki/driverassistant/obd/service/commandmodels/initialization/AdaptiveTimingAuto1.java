package com.google.kpierudzki.driverassistant.obd.service.commandmodels.initialization;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class AdaptiveTimingAuto1 extends ObdCommandModel {

    public AdaptiveTimingAuto1() {
        super(new com.google.kpierudzki.driverassistant.obd.service.commands.AdaptiveTimingAuto1(), false, ObdParamType.UNKNOWN);
    }

    @Override
    public boolean isOK() {
        return response.matches("OK");
    }
}

package com.google.kpierudzki.driverassistant.obd.service.commandmodels.control;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class EquivalentRatioCommand extends ObdCommandModel {

    public EquivalentRatioCommand() {
        super(new com.github.pires.obd.commands.control.EquivalentRatioCommand(), true, ObdParamType.EQUIVALENT_RATIO);
    }

    @Override
    public int getPidNumber() {
        return 68;
    }
}

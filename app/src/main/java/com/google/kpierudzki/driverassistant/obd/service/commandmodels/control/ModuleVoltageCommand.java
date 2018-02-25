package com.google.kpierudzki.driverassistant.obd.service.commandmodels.control;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class ModuleVoltageCommand extends ObdCommandModel {

    public ModuleVoltageCommand() {
        super(new com.github.pires.obd.commands.control.ModuleVoltageCommand(), true, ObdParamType.MODULE_VOLTAGE);
    }

    @Override
    public int getPidNumber() {
        return 66;
    }
}

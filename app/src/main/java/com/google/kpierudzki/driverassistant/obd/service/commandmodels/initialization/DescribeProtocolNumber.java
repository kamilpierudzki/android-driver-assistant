package com.google.kpierudzki.driverassistant.obd.service.commandmodels.initialization;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;

/**
 * Created by Kamil on 15.09.2017.
 */
public class DescribeProtocolNumber extends ObdCommandModel {

    public DescribeProtocolNumber() {
        super(new com.google.kpierudzki.driverassistant.obd.service.commands.DescribeProtocolNumber(), false, ObdParamType.UNKNOWN);
    }

    @Override
    public boolean isOK() {
        return true; //todo Zbadać temat !!!
    }
}

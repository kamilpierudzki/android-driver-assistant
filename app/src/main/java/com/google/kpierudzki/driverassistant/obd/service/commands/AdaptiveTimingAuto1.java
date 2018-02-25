package com.google.kpierudzki.driverassistant.obd.service.commands;

import com.github.pires.obd.commands.protocol.ObdProtocolCommand;

/**
 * Created by Kamil on 15.09.2017.
 */
public class AdaptiveTimingAuto1 extends ObdProtocolCommand {

    public AdaptiveTimingAuto1() {
        super("ATAT1");
    }

    @Override
    public String getFormattedResult() {
        return getResult();
    }

    @Override
    public String getName() {
        return "Adaptive Timing Auto 1";
    }
}

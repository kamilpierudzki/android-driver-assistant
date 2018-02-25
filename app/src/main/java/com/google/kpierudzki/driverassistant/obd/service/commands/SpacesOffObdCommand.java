package com.google.kpierudzki.driverassistant.obd.service.commands;

import com.github.pires.obd.commands.protocol.ObdProtocolCommand;

/**
 * Created by Kamil on 15.09.2017.
 */
public class SpacesOffObdCommand extends ObdProtocolCommand {

    public SpacesOffObdCommand() {
        super("AT S0");
    }

    @Override
    public String getFormattedResult() {
        return getResult();
    }

    @Override
    public String getName() {
        return "Spaces Off";
    }
}

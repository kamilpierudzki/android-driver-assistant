package com.google.kpierudzki.driverassistant.obd.service.commands;

import com.github.pires.obd.commands.protocol.ObdProtocolCommand;

/**
 * Created by Kamil on 15.09.2017.
 */
public class UnknownCommand_3 extends ObdProtocolCommand {

    public UnknownCommand_3() {
        super("ATAT1");
    }

    @Override
    public String getFormattedResult() {
        return getResult();
    }

    @Override
    public String getName() {
        return "Unknown Command #3";
    }
}

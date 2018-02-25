package com.google.kpierudzki.driverassistant.obd.service.commands;

import com.github.pires.obd.commands.protocol.ObdProtocolCommand;

/**
 * Created by Kamil on 15.09.2017.
 */
public class UnknownCommand_4 extends ObdProtocolCommand {

    public UnknownCommand_4() {
        super("ATDPN");
    }

    @Override
    public String getFormattedResult() {
        return getResult();
    }

    @Override
    public String getName() {
        return "Unknown Command #4";
    }
}

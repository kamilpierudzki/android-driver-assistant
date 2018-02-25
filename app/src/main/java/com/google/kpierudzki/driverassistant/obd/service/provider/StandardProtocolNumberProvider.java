package com.google.kpierudzki.driverassistant.obd.service.provider;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;

/**
 * Created by Kamil on 15.09.2017.
 */

public class StandardProtocolNumberProvider {

    private int startNumber;
    private int protocol;

    public StandardProtocolNumberProvider(int startNumber) {
        this.startNumber = startNumber;
        this.protocol = startNumber;
    }

    public void incrementProtocol() {
        if (++protocol >= ObdProtocol.values().length)
            protocol = ObdProtocol.Auto.protocolNumber;
    }

    public int getProtocol() {
        if (startNumber == ObdProtocol.Auto.protocolNumber) return protocol;
        else return startNumber;
    }
}

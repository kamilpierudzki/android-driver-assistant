package com.google.kpierudzki.driverassistant.obd.datamodel;

import com.github.pires.obd.enums.ObdProtocols;
import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;

import java8.util.J8Arrays;
import java8.util.Optional;

/**
 * Created by Kamil on 09.09.2017.
 */

public enum ObdProtocol {
    Auto(0, ObdProtocols.AUTO, App.getAppContext().getString(R.string.Obd_Connect_Protocol_Default)),
    SAE_J1850_PWM(1, ObdProtocols.SAE_J1850_PWM, "SAE_J1850_PWM"),
    ISO_15765_4_CAN(2, ObdProtocols.ISO_15765_4_CAN, "ISO_15765_4_CAN"),
    ISO_9141_2(3, ObdProtocols.ISO_9141_2, "ISO_9141_2"),
    SAE_J1850_VPW(4, ObdProtocols.SAE_J1850_VPW, "SAE_J1850_VPW"),
    ISO_14230_4_KWP_FAST(5, ObdProtocols.ISO_14230_4_KWP_FAST, "ISO_14230_4_KWP_FAST"),
    ISO_14230_4_KWP(6, ObdProtocols.ISO_14230_4_KWP, "ISO_14230_4_KWP"),
    ISO_15765_4_CAN_C(7, ObdProtocols.ISO_15765_4_CAN_C, "ISO_15765_4_CAN_C"),
    ISO_15765_4_CAN_B(8, ObdProtocols.ISO_15765_4_CAN_B, "ISO_15765_4_CAN_B"),
    ISO_15765_4_CAN_D(9, ObdProtocols.ISO_15765_4_CAN_D, "ISO_15765_4_CAN_D");

    public final int protocolNumber;
    public final ObdProtocols protocol;
    public final String protocolName;

    ObdProtocol(int protocolNumber, ObdProtocols protocol, String protocolName) {
        this.protocolNumber = protocolNumber;
        this.protocol = protocol;
        this.protocolName = protocolName;
    }

    @Override
    public String toString() {
        return protocolName;
    }

    public static ObdProtocols getProtocolObjectAtIndex(int protocol) {
        Optional<ObdProtocol> optional = J8Arrays.stream(values())
                .filter(obdProtocol -> obdProtocol.protocolNumber == protocol).findFirst();
        if (optional.isPresent()) return optional.get().protocol;
        else return Auto.protocol;
    }
}

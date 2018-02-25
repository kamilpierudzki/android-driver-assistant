package com.google.kpierudzki.driverassistant.util.preferences;

/**
 * Created by Kamil on 28.06.2017.
 */

public enum PrefItem {

    LAST_TRACK_DATE_START_IN_SEC("LAST_TRACK_DATE_START_IN_SEC", Long.class),
    MAC_OF_LAST_BLUETOOTH_DEVICE("mac_of_last_bluetooth_device", String.class),
    LAST_OBD_PROTOCOL("last_obd_protocol", Integer.class),
    ;

    private final String id;
    private final Class aClass;

    PrefItem(String id, Class aClass) {
        this.id = id;
        this.aClass = aClass;
    }

    public String getId() {
        return id;
    }

    public Class getClassOfItem() {
        return aClass;
    }
}

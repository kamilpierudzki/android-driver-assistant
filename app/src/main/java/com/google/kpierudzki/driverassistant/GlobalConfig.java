package com.google.kpierudzki.driverassistant;

/**
 * Created by Kamil on 28.06.2017.
 */

public class GlobalConfig {
    public static boolean DEBUG_MODE = false;//todo przenieść do BuildTypes
    public static boolean DEMO_MODE = false;
    public final static String MOCK_TRACK = "gps_probe_recording_poz_czar.json";

    public final static String[] OBD_PROBES = {
            "poz_czar/obd_speed_probe_recording.json",
            "poz_czar/obd_rpm_probe_recording.json",
            "poz_czar/obd_oil_temp_probe_recording.json",
            "poz_czar/obd_maf_probe_recording.json",
            "poz_czar/obd_load_probe_recording.json",
            "poz_czar/obd_coolant_temp_probe_recording.json",
            "poz_czar/obd_barometric_pressure_probe_recording.json"
    };

    public static double ECO_DRIVING_GPS_OPTIMAL_ACCELERATION_LIMIT = 0.8f;
    public static double ECO_DRIVING_OBD_OPTIMAL_ACCELERATION_LIMIT = 1.0f;
}

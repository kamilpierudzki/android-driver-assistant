package com.google.kpierudzki.driverassistant.obd.service.provider;

import com.google.kpierudzki.driverassistant.App;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kamil on 23.12.2017.
 */

public class DtcProvider {

    private Map<String, String> dtcs;

    public DtcProvider() {
        dtcs = new HashMap<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    App.getAppContext().getAssets().open("obdii_dtc.csv")));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split("\\|");
                if (split.length == 2) {
                    dtcs.put(split[0], split[1]);
                }
            }
        } catch (IOException e) {
            //...
        }
    }

    @Nullable
    public String getDescription(String code) {
        String description = dtcs.get(code.toUpperCase());
        return description != null ? description : "<unknown>";
    }
}

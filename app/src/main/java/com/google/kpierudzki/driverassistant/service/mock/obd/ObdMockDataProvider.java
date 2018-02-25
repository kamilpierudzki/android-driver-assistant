package com.google.kpierudzki.driverassistant.service.mock.obd;

import android.util.Log;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.GlobalConfig;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import java8.util.J8Arrays;
import java8.util.stream.Collectors;

/**
 * Created by Kamil on 14.11.2017.
 */

public class ObdMockDataProvider {

    private static final String CURRENT_ID = "id";
    private static final String CURRENT_TIMESTAMP = "timestamp";
    private static final String CURRENT_SPEED = "speed";
    private static final String CURRENT_RPM = "rpm";
    private static final String CURRENT_OIL_TEMP = "oil_temp";
    private static final String CURRENT_MAF = "maf";
    private static final String CURRENT_LOAD = "load";
    private static final String CURRENT_COOLANT_TEMP = "coolant_temp";
    private static final String CURRENT_BAROMETRIC_PRESSURE = "barometric_pressure";

    public static List<Probe> prepareData() {
        List<JSONArray> mockSamples = readDataFromFiles();
        List<Probe> probes = new ArrayList<>();
        probes.addAll(prepareProbes(mockSamples.get(0), ObdParamType.SPEED, CURRENT_SPEED));
        probes.addAll(prepareProbes(mockSamples.get(1), ObdParamType.ENGINE_RPM, CURRENT_RPM));
        probes.addAll(prepareProbes(mockSamples.get(2), ObdParamType.OIL_TEMP, CURRENT_OIL_TEMP));
        probes.addAll(prepareProbes(mockSamples.get(3), ObdParamType.MAF, CURRENT_MAF));
        probes.addAll(prepareProbes(mockSamples.get(4), ObdParamType.ENGINE_LOAD, CURRENT_LOAD));
        probes.addAll(prepareProbes(mockSamples.get(5), ObdParamType.COOLANT_TEMP, CURRENT_COOLANT_TEMP));
        probes.addAll(prepareProbes(mockSamples.get(6), ObdParamType.BAROMETRIC_PRESSURE, CURRENT_BAROMETRIC_PRESSURE));

        sortProbes(probes);
        prepareDelays(probes);

        return probes;
    }

    public static void sortProbes(List<Probe> probes) {
        Collections.sort(probes, (probe1, probe2) -> (int) (probe1.timestamp - probe2.timestamp));
    }

    public static List<JSONArray> readDataFromFiles() {
        return J8Arrays.stream(GlobalConfig.OBD_PROBES)
                .map(s -> {
                    BufferedReader reader = null;
                    try {
                        StringBuilder stringBuilder = new StringBuilder();
                        reader = new BufferedReader(
                                new InputStreamReader(App.getAppContext().getAssets().open(
                                        String.format(Locale.getDefault(), "obd_probes/%s", s))));
                        for (String line; (line = reader.readLine()) != null; ) {
                            stringBuilder.append(line).append("\n");
                        }

                        return new JSONArray(stringBuilder.toString());
                    } catch (Exception e) {
                        return new JSONArray();
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                Log.e("MockReadDataManager", "prepareData(): " + e.getLocalizedMessage());
                            }
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    public static void prepareDelays(List<Probe> probes) {
        for (int i = 0; i < probes.size(); i++) {
            if (i - 1 >= 0) {
                Probe currentProbe = probes.get(i);
                Probe previousProbe = probes.get(i - 1);
                currentProbe.setDelay(currentProbe.timestamp - previousProbe.timestamp);
            }
        }
    }

    public static List<Probe> prepareProbes(JSONArray array, ObdParamType paramType, String valueKey) {
        List<Probe> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject currentObject = array.getJSONObject(i);
                result.add(new Probe(currentObject.getInt(CURRENT_ID),
                        (float) currentObject.getDouble(valueKey),
                        currentObject.getLong(CURRENT_TIMESTAMP),
                        paramType));
            } catch (JSONException e) {
                //...
            }
        }
        return result;
    }

    public static class Probe {
        public int id;
        public float value;
        public long timestamp;
        public ObdParamType paramType;

        private long delay;

        public Probe(int id, float value, long timestamp, ObdParamType paramType) {
            this.id = id;
            this.value = value;
            this.timestamp = timestamp;
            this.paramType = paramType;
        }

        public long getDelay() {
            return delay;
        }

        void setDelay(long delay) {
            this.delay = delay;
        }

        @Override
        public String toString() {
            return "Probe{" +
                    "id=" + id +
                    ", value=" + value +
                    ", timestamp=" + timestamp +
                    ", paramType=" + paramType +
                    ", delay=" + delay +
                    '}';
        }
    }
}

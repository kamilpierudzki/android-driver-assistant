package com.google.kpierudzki.driverassistant.service.mock.location;

import android.util.Log;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.GlobalConfig;
import com.google.kpierudzki.driverassistant.common.model.Coordinate;

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

/**
 * Created by Kamil on 07.12.2017.
 */

public class LocationMockDataProvider {

    private final static String ID_COL = "id";
    private final static String TIMESTAMP_COL = "timestamp";
    private final static String SPEED_COL = "speed";
    private static final String POSITION = "position";

    public static List<Probe> prepareData() {
        JSONArray mockSamples = readDataFromFile();
        List<Probe> probes = new ArrayList<>(prepareProbes(mockSamples));

        sortProbes(probes);
        prepareDelays(probes);

        return probes;
    }

    public static void sortProbes(List<Probe> probes) {
        Collections.sort(probes, (probe1, probe2) -> (int) (probe1.timestamp - probe2.timestamp));
    }

    private static JSONArray readDataFromFile() {
        BufferedReader reader = null;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            reader = new BufferedReader(
                    new InputStreamReader(App.getAppContext().getAssets().open(
                            String.format(Locale.getDefault(), "geo_samples/%s", GlobalConfig.MOCK_TRACK)
                    )));
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
                    Log.e("MockLocationManager", "prepareMockArray(): " + e.getLocalizedMessage());
                }
            }
        }
    }

    private static List<Probe> prepareProbes(JSONArray array) {
        List<Probe> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject currentObject = array.getJSONObject(i);
                result.add(new Probe(currentObject.getLong(ID_COL), currentObject.getLong(TIMESTAMP_COL),
                        Coordinate.Converter.fromString(currentObject.getString(POSITION)),
                        (float) currentObject.getDouble(SPEED_COL)));
            } catch (JSONException e) {
                //...
            }
        }
        return result;
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

    public static class Probe {

        public long id;
        public long timestamp;
        public Coordinate position;
        public float speed;

        private long delay;

        public Probe(long id, long timestamp, Coordinate position, float speed) {
            this.id = id;
            this.timestamp = timestamp;
            this.position = position;
            this.speed = speed;
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
                    ", timestamp=" + timestamp +
                    ", position=" + position +
                    ", speed=" + speed +
                    ", delay=" + delay +
                    '}';
        }
    }
}

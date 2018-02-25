package com.google.kpierudzki.driverassistant;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.service.mock.obd.ObdMockDataProvider;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 14.11.2017.
 */

public class ObdMockObdProviderTest {

    @Test
    public void testDelay() {
        List<ObdMockDataProvider.Probe> probes = new ArrayList<>();
        probes.add(new ObdMockDataProvider.Probe(-1, -1, 1000, ObdParamType.UNKNOWN));
        probes.add(new ObdMockDataProvider.Probe(-1, -1, 1050, ObdParamType.UNKNOWN));
        probes.add(new ObdMockDataProvider.Probe(-1, -1, 1070, ObdParamType.UNKNOWN));
        probes.add(new ObdMockDataProvider.Probe(-1, -1, 1150, ObdParamType.UNKNOWN));
        probes.add(new ObdMockDataProvider.Probe(-1, -1, 1170, ObdParamType.UNKNOWN));
        probes.add(new ObdMockDataProvider.Probe(-1, -1, 1250, ObdParamType.UNKNOWN));

        ObdMockDataProvider.prepareDelays(probes);

        Assert.assertTrue(probes.get(0).getDelay() == 0);
        Assert.assertTrue(probes.get(1).getDelay() == 50);
        Assert.assertTrue(probes.get(2).getDelay() == 20);
        Assert.assertTrue(probes.get(3).getDelay() == 80);
        Assert.assertTrue(probes.get(4).getDelay() == 20);
        Assert.assertTrue(probes.get(5).getDelay() == 80);
    }

    @Test
    public void testSort() {
        List<ObdMockDataProvider.Probe> probes = new ArrayList<>();
        probes.add(new ObdMockDataProvider.Probe(5, -1, 1250, ObdParamType.UNKNOWN));
        probes.add(new ObdMockDataProvider.Probe(2, -1, 1070, ObdParamType.UNKNOWN));
        probes.add(new ObdMockDataProvider.Probe(1, -1, 1050, ObdParamType.UNKNOWN));
        probes.add(new ObdMockDataProvider.Probe(0, -1, 1000, ObdParamType.UNKNOWN));
        probes.add(new ObdMockDataProvider.Probe(4, -1, 1170, ObdParamType.UNKNOWN));
        probes.add(new ObdMockDataProvider.Probe(3, -1, 1150, ObdParamType.UNKNOWN));

        ObdMockDataProvider.sortProbes(probes);

        Assert.assertTrue(probes.get(0).id == 0);
        Assert.assertTrue(probes.get(1).id == 1);
        Assert.assertTrue(probes.get(2).id == 2);
        Assert.assertTrue(probes.get(3).id == 3);
        Assert.assertTrue(probes.get(4).id == 4);
        Assert.assertTrue(probes.get(5).id == 5);
    }
}

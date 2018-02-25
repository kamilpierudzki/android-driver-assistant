package com.google.kpierudzki.driverassistant;

import com.google.kpierudzki.driverassistant.common.model.Coordinate;
import com.google.kpierudzki.driverassistant.service.mock.location.LocationMockDataProvider;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 07.12.2017.
 */

public class LocationMockProviderTest {

    @Test
    public void testDelay() {
        List<LocationMockDataProvider.Probe> probes = new ArrayList<>();
        probes.add(new LocationMockDataProvider.Probe(-1, 1000, new Coordinate(), 0));
        probes.add(new LocationMockDataProvider.Probe(-1, 1050, new Coordinate(), 0));
        probes.add(new LocationMockDataProvider.Probe(-1, 1070, new Coordinate(), 0));
        probes.add(new LocationMockDataProvider.Probe(-1, 1150, new Coordinate(), 0));
        probes.add(new LocationMockDataProvider.Probe(-1, 1170, new Coordinate(), 0));
        probes.add(new LocationMockDataProvider.Probe(-1, 1250, new Coordinate(), 0));

        LocationMockDataProvider.prepareDelays(probes);

        Assert.assertTrue(probes.get(0).getDelay() == 0);
        Assert.assertTrue(probes.get(1).getDelay() == 50);
        Assert.assertTrue(probes.get(2).getDelay() == 20);
        Assert.assertTrue(probes.get(3).getDelay() == 80);
        Assert.assertTrue(probes.get(4).getDelay() == 20);
        Assert.assertTrue(probes.get(5).getDelay() == 80);
    }

    @Test
    public void testSort() {
        List<LocationMockDataProvider.Probe> probes = new ArrayList<>();
        probes.add(new LocationMockDataProvider.Probe(5,1250, new Coordinate(), 0));
        probes.add(new LocationMockDataProvider.Probe(2,1070, new Coordinate(), 0));
        probes.add(new LocationMockDataProvider.Probe(1,1050, new Coordinate(), 0));
        probes.add(new LocationMockDataProvider.Probe(0,1000, new Coordinate(), 0));
        probes.add(new LocationMockDataProvider.Probe(4,1170, new Coordinate(), 0));
        probes.add(new LocationMockDataProvider.Probe(3,1150, new Coordinate(), 0));

        LocationMockDataProvider.sortProbes(probes);

        Assert.assertTrue(probes.get(0).id == 0);
        Assert.assertTrue(probes.get(1).id == 1);
        Assert.assertTrue(probes.get(2).id == 2);
        Assert.assertTrue(probes.get(3).id == 3);
        Assert.assertTrue(probes.get(4).id == 4);
        Assert.assertTrue(probes.get(5).id == 5);
    }
}

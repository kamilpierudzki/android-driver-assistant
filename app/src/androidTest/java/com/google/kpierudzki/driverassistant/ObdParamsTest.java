package com.google.kpierudzki.driverassistant;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.kpierudzki.driverassistant.common.model.Coordinate;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesDao;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesEntity;
import com.google.kpierudzki.driverassistant.history.map.HistoryMapContract;
import com.google.kpierudzki.driverassistant.obd.database.ObdParamsDao;
import com.google.kpierudzki.driverassistant.obd.database.ObdParamsEntity;
import com.google.kpierudzki.driverassistant.service.database.AssistantDatabase;
import com.google.kpierudzki.driverassistant.util.MapUtils;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 26.11.2017.
 */

@RunWith(AndroidJUnit4.class)
public class ObdParamsTest {

    private AssistantDatabase mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AssistantDatabase.class).build();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void testProbes() {
        final long TRACK_ID = 0;
        GeoSamplesDao geoSamplesDao = mDb.getGeoSamplesDao();
        ObdParamsDao obdParamsDao = mDb.getObdParamsDao();

        List<ObdParamsEntity> params = new ArrayList<>();
        for (int i = 0; i < 15; i++)
            params.add(new ObdParamsEntity(TRACK_ID, i, i, 0, 0, 0, 0, 0, 0, 0));
        obdParamsDao.addAll(params);

        List<GeoSamplesEntity> probes = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            probes.add(new GeoSamplesEntity(TRACK_ID, new Coordinate(), i, 999));
        geoSamplesDao.addAll(probes);

        List<HistoryMapContract.MapData> result = new ArrayList<>();
        //Prepare geo data
        for (GeoSamplesEntity geoSamplesEntity : geoSamplesDao.getSamplesForTrack(TRACK_ID))
            result.add(new HistoryMapContract.MapData(geoSamplesEntity));

        //Prepare obd data
        MapUtils.prepareMapDataWithObdParams(result, obdParamsDao);

        for (int i = 0; i < result.size(); i++) {
            Assert.assertTrue(result.get(i).obdParamsEntity != null);
            if (i < 15) {
                Assert.assertTrue(result.get(i).obdParamsEntity.getCurrentSpeed() != 999);
            } else {
                Assert.assertTrue(result.get(i).obdParamsEntity.getCurrentSpeed() == 999);
            }
        }
    }
}
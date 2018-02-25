package com.google.kpierudzki.driverassistant;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.obd.database.ObdParamsEntity;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.SpeedCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.LoadCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.OilTempCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.RPMCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.temperature.EngineCoolantTemperatureCommand;
import com.google.kpierudzki.driverassistant.obd.service.database.ObdProbesToDbSaver;
import com.google.kpierudzki.driverassistant.service.database.AssistantDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import junit.framework.Assert;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Kamil on 15.11.2017.
 */

@RunWith(AndroidJUnit4.class)
public class ObdProbesToDbSaverTest {

    private AssistantDatabase mDb;
    private ObdProbesToDbSaver obdProbesToDbSaver;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AssistantDatabase.class).build();
        obdProbesToDbSaver = new ObdProbesToDbSaver(mDb.getObdParamsDao(), 4);
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void testSaver() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long offset;

        ArrayList<ObdCommandModel> commands = new ArrayList<ObdCommandModel>() {{
            add(new SpeedCommand().updateValue(1f));
            add(new RPMCommand().updateValue(1f));
            add(new SpeedCommand().updateValue(2f));
            add(new EngineCoolantTemperatureCommand().updateValue(1f));
            add(new SpeedCommand().updateValue(3f));
            add(new LoadCommand().updateValue(1f));
            add(new OilTempCommand().updateValue(1f));
        }};

        for (int i = 0; i < 4; i++) {
            offset = System.currentTimeMillis() - startTime;
            obdProbesToDbSaver.updateGeoData(new IGeoSampleListener.GeoSamplesSwappableData(0, offset, -1/*ignore*/));
            for (ObdCommandModel command : commands) {
                obdProbesToDbSaver.onNewObdData(command);
                Thread.sleep(100);
            }
        }
        obdProbesToDbSaver.forcePersistBuffer();

        for (ObdParamsEntity obdParamsEntity : mDb.getObdParamsDao().getAll()) {
            Log.d("ObdProbesSaver", obdParamsEntity.toString());
        }

        verifyValues();
    }

    private void verifyValues() {
        for (ObdParamsEntity obdParamsEntity : mDb.getObdParamsDao().getAll()) {
            Assert.assertTrue(obdParamsEntity.getCurrentSpeed() <= 1f);
            Assert.assertTrue(obdParamsEntity.getRpm() <= 1f);
            Assert.assertTrue(obdParamsEntity.getRpm() <= 1f);
            Assert.assertTrue(obdParamsEntity.getCoolantTemp() <= 1f);
            Assert.assertTrue(obdParamsEntity.getLoad() <= 1f);
            Assert.assertTrue(obdParamsEntity.getOilTemp() <= 1f);
        }
    }
}

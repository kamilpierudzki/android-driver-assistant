package com.google.kpierudzki.driverassistant;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingEntity;
import com.google.kpierudzki.driverassistant.eco_driving.helper.EcoDrivingCalculatorCallbacks;
import com.google.kpierudzki.driverassistant.eco_driving.helper.EcoDrivingGpsCalculator;
import com.google.kpierudzki.driverassistant.eco_driving.helper.EcoDrivingObdCalculator;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.service.database.AssistantDatabase;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

/**
 * Created by Kamil on 08.10.2017.
 */

@RunWith(AndroidJUnit4.class)
public class EcoDrivingCalculatorTest implements EcoDrivingCalculatorCallbacks {

    private AssistantDatabase mDb;
    private EcoDrivingObdCalculator ecoDrivingObdCalculator;
    private EcoDrivingGpsCalculator ecoDrivingGpsCalculator;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AssistantDatabase.class).build();
        ecoDrivingObdCalculator = new EcoDrivingObdCalculator(mDb.getEcoDrivingDao(), this, 4);
        ecoDrivingGpsCalculator = new EcoDrivingGpsCalculator(mDb.getEcoDrivingDao(), this, 4);
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void testObdVariant() throws InterruptedException {
        Log.d("ObdCalculator", "------------------------");
        long startTime = System.currentTimeMillis();
        long offset;
        float speed = 0;

        for (int i = 0; i < 3; i++) {
            offset = System.currentTimeMillis() - startTime;
            ecoDrivingObdCalculator.updateGeoData(new IGeoSampleListener.GeoSamplesSwappableData(0, offset, -1/*ignore*/));
            for (int j = 0; j < 4; j++) {
                if (j == 2) speed += GlobalConfig.ECO_DRIVING_OBD_OPTIMAL_ACCELERATION_LIMIT * 1.2f;
                else speed += GlobalConfig.ECO_DRIVING_OBD_OPTIMAL_ACCELERATION_LIMIT * 0.1f;

                ecoDrivingObdCalculator.onNewSpeed(speed);
                Thread.sleep(300);
            }
        }
        ecoDrivingObdCalculator.forcePersistBuffer();

        for (EcoDrivingEntity ecoDrivingEntity : mDb.getEcoDrivingDao().getAll())
            Log.d("ObdCalculator", ecoDrivingEntity.toString());

        verifyOffsets();
        verifyScore(1f);
    }

    @Test
    public void testGpsVariant() throws InterruptedException {
        Log.d("GpsCalculator", "------------------------");
        long startTime = System.currentTimeMillis();
        long offset;
        float speed = 0;

        for (int i = 0; i < 12; i++) {
            offset = System.currentTimeMillis() - startTime;
            if (i % 3 == 0) speed += GlobalConfig.ECO_DRIVING_GPS_OPTIMAL_ACCELERATION_LIMIT * 1.2f;
            else speed += GlobalConfig.ECO_DRIVING_GPS_OPTIMAL_ACCELERATION_LIMIT * 0.1f;
            ecoDrivingGpsCalculator.updateGeoData(new IGeoSampleListener.GeoSamplesSwappableData(1, offset, speed));
            Thread.sleep(900);
        }
        ecoDrivingObdCalculator.forcePersistBuffer();

        for (EcoDrivingEntity ecoDrivingEntity : mDb.getEcoDrivingDao().getAll())
            Log.d("GpsCalculator", ecoDrivingEntity.toString());

        verifyOffsets();
        verifyScore(0.75f);
    }

    private void verifyOffsets() {
        long lastOffset = 0;
        for (EcoDrivingEntity ecoDrivingEntity : mDb.getEcoDrivingDao().getAll()) {
            Assert.assertTrue(lastOffset <= ecoDrivingEntity.getOffset());
            lastOffset = ecoDrivingEntity.getOffset();
        }
    }

    private void verifyScore(float expectedValue) {
        List<EcoDrivingEntity> all = mDb.getEcoDrivingDao().getAll();

        int sumOfScores = 0;
        for (EcoDrivingEntity ecoDrivingEntity : all)
            sumOfScores += ecoDrivingEntity.getCurrentScore();

        float score = sumOfScores / (all.size() * 1.0f);

        Assert.assertTrue(Math.abs(score - expectedValue) <= 0.01);
    }

    @Override
    public void onSpeedChanged(float speed) {
        Log.d("Calculator.Speed", "speed[" + speed + "]");
    }

    @Override
    public void onAccelerationChanged(float acceleration) {
        Log.d("Calculator.Acceleration", "acceleration[" + acceleration + "]");
    }

    @Override
    public void onAvgScoreChanged(float score) {
        Log.d("Calculator.Score", "score[" + score + "]");
    }
}

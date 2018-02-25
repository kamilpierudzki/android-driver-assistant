package com.google.kpierudzki.driverassistant.eco_driving.helper;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.GlobalConfig;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingDao;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingEntity;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;

/**
 * Created by Kamil on 06.10.2017.
 */

public class EcoDrivingObdCalculator extends BaseEcoDrivingCalculator {

    private IGeoSampleListener.GeoSamplesSwappableData currentGeoData;
    private int bufferLimit;

    public EcoDrivingObdCalculator(EcoDrivingDao ecoDrivingDao, @Nullable EcoDrivingCalculatorCallbacks callbacks, int bufferLimit) {
        super(ecoDrivingDao, callbacks);
        this.bufferLimit = bufferLimit;
        currentGeoData = new IGeoSampleListener.GeoSamplesSwappableData(0, 0, 0);
    }

    @WorkerThread
    @Override
    public void updateGeoData(IGeoSampleListener.GeoSamplesSwappableData geoData) {
        synchronized (this) {
            this.currentGeoData = new IGeoSampleListener.GeoSamplesSwappableData(geoData);
        }
    }

    @WorkerThread
    public void onNewSpeed(float newSpeed) {
        synchronized (this) {
            long currentTime = System.currentTimeMillis();

            if (previousSpeed > -1 && previousTime > 0) {//first shoot
                //If user is not moving, stop calculations and don't insert data to buffer/database.
                if (isUserMoving(newSpeed)) {
                    float deltaSpeed = newSpeed - previousSpeed;//[m/s]
                    float deltaTime = (currentTime - previousTime) / 1_000f;//[ms] -> [s]
                    float acceleration = deltaSpeed / deltaTime * 1.0f;//[m/s^2]
                    int currentScore = Math.abs(acceleration) <= GlobalConfig.ECO_DRIVING_OBD_OPTIMAL_ACCELERATION_LIMIT ? 1 : 0;
                    float avgScore = calculateAvgScore(true, currentGeoData.trackId);

                    lastEcoDrivingEntity = new EcoDrivingEntity(currentGeoData, acceleration, currentScore, avgScore);

                    if (callbacks != null) {
                        callbacks.onAccelerationChanged(acceleration);
                        callbacks.onAvgScoreChanged(avgScore);
                    }
                }
            }

            if (lastEcoDrivingEntity != null) {
                buffer.add(lastEcoDrivingEntity);

                if (buffer.size() >= bufferLimit) {
                    ecoDrivingDao.addAll(buffer);
                    buffer.clear();
                    refreshDbStatistic(currentGeoData.trackId);
                }
            }

            if (callbacks != null) callbacks.onSpeedChanged(newSpeed);

            previousSpeed = newSpeed;
            previousTime = currentTime;
        }
    }

    private boolean isUserMoving(float newSpeed) {
        return previousSpeed != 0 && newSpeed != 0;
    }
}

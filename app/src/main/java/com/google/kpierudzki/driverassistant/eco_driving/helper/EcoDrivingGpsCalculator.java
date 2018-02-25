package com.google.kpierudzki.driverassistant.eco_driving.helper;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.GlobalConfig;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingDao;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingEntity;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;

/**
 * Created by Kamil on 08.10.2017.
 */

public class EcoDrivingGpsCalculator extends BaseEcoDrivingCalculator {

    private int bufferLimit;

    public EcoDrivingGpsCalculator(EcoDrivingDao ecoDrivingDao, @Nullable EcoDrivingCalculatorCallbacks callbacks, int bufferLimit) {
        super(ecoDrivingDao, callbacks);
        this.bufferLimit = bufferLimit;
    }

    @WorkerThread
    @Override
    public void updateGeoData(IGeoSampleListener.GeoSamplesSwappableData newData) {
        synchronized (this) {
            long currentTime = System.currentTimeMillis();

            if (previousSpeed > -1 && previousTime > 0) {//first shoot
                //If user is not moving, stop calculations and don't insert data to buffer/database.
                if (isUserMoving(newData)) {
                    float deltaSpeed = newData.speed - previousSpeed;//[m/s]
                    float deltaTime = (currentTime - previousTime) / 1_000f;//[ms] -> [s]
                    float acceleration = deltaSpeed / deltaTime * 1.0f;//[m/s^2]
                    int currentScore = Math.abs(acceleration) <= GlobalConfig.ECO_DRIVING_GPS_OPTIMAL_ACCELERATION_LIMIT ? 1 : 0;
                    float avgScore = calculateAvgScore(true, newData.trackId);

                    lastEcoDrivingEntity = new EcoDrivingEntity(newData, acceleration, currentScore, avgScore);

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
                    refreshDbStatistic(newData.trackId);
                }
            }

            if (callbacks != null) callbacks.onSpeedChanged(newData.speed);

            previousSpeed = newData.speed;
            previousTime = currentTime;
        }
    }

    private boolean isUserMoving(IGeoSampleListener.GeoSamplesSwappableData newData) {
        return previousSpeed != 0 && newData.speed != 0;
    }
}

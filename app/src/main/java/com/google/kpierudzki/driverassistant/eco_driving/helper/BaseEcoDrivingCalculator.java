package com.google.kpierudzki.driverassistant.eco_driving.helper;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingDao;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingEntity;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingStatistic;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 08.10.2017.
 */

abstract class BaseEcoDrivingCalculator {

    EcoDrivingDao ecoDrivingDao;
    protected EcoDrivingCalculatorCallbacks callbacks;
    float previousSpeed;
    List<EcoDrivingEntity> buffer;
    private EcoDrivingStatistic currentDbStatistic;
    long previousTime;
    EcoDrivingEntity lastEcoDrivingEntity;

    BaseEcoDrivingCalculator(EcoDrivingDao ecoDrivingDao, @Nullable EcoDrivingCalculatorCallbacks callbacks) {
        this.ecoDrivingDao = ecoDrivingDao;
        this.callbacks = callbacks;
        previousSpeed = -1;
        buffer = new ArrayList<>();
        previousTime = -1;
    }

    public abstract void updateGeoData(IGeoSampleListener.GeoSamplesSwappableData geoData);

    float calculateAvgScore(boolean includePersistedData, long trackId) {
        if (currentDbStatistic == null) refreshDbStatistic(trackId);

        int sumOfScore = MathUtil.sum(
                StreamSupport.stream(buffer)
                        .map(EcoDrivingEntity::getCurrentScore)
                        .toArray(Integer[]::new));

        if (buffer.isEmpty()) return 1;

        if (includePersistedData)
            return (currentDbStatistic.sum + sumOfScore) /
                    (currentDbStatistic.count + buffer.size() * 1.0f);
        else
            return sumOfScore / (buffer.size() * 1.0f);
    }

    float calculateAvgAcceleration() {
        float sumOfAcceleration = MathUtil.sumKahan(StreamSupport.stream(buffer)
                .map(EcoDrivingEntity::getCurrentAcceleration).toArray(Float[]::new));

        return MathUtil.sumKahan(sumOfAcceleration) / buffer.size();
    }

    void refreshDbStatistic(long trackId) {
        currentDbStatistic = EcoDrivingStatistic.merge(
                ecoDrivingDao.getScoreStatisticsForTrackId(trackId),
                ecoDrivingDao.getCountStatisticsForTrackId(trackId));
    }

    @WorkerThread
    public void forcePersistBuffer() {
        synchronized (this) {
            if (!buffer.isEmpty()) {
                ecoDrivingDao.addAll(buffer);
                buffer.clear();
                currentDbStatistic = null;
            }
        }
    }
}

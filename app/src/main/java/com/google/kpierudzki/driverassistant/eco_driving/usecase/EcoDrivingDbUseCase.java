package com.google.kpierudzki.driverassistant.eco_driving.usecase;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.eco_driving.EcoDrivingContract;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingDao;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingEntity;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesDao;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesEntity;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesTracksEntity;
import com.google.kpierudzki.driverassistant.obd.database.ObdParamsEntity;
import com.google.kpierudzki.driverassistant.service.helper.BackgroundThreadPoolHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 18.07.2017.
 */

public class EcoDrivingDbUseCase extends BackgroundThreadPoolHelper {

    private Callback callback;

    public EcoDrivingDbUseCase(Callback callback) {
        super();
        this.callback = callback;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callback = null;
    }

    public void provideLastNSamplesForParam(int N, EcoDrivingContract.EcoDrivingParameter parameter) {
        if (threadPool != null)
            threadPool.execute(() -> {
                GeoSamplesTracksEntity lastTrackEntry = App.getDatabase().getGeoSamplesDao().getLastTrack();

            if (lastTrackEntry != null) {
                EcoDrivingDao ecoDrivingDao = App.getDatabase().getEcoDrivingDao();
                List<EcoDrivingEntity> entities = ecoDrivingDao.getLastDataForTrackId(N,
                        lastTrackEntry.getTrackID());
                List<Float> result = null;
                switch (parameter) {
                    case SCORE:
                        if (entities != null) {
                            result = StreamSupport.stream(entities)
                                    .map(EcoDrivingEntity::getGeneralScore)
                                    .collect(Collectors.toList());
                        }
                        break;
                    case ACCELERATION:
                        if (entities != null && !entities.isEmpty()) {
                            result = StreamSupport.stream(entities)
                                    .map(EcoDrivingEntity::getCurrentAcceleration)
                                    .collect(Collectors.toList());
                        }
                        break;
                    case SPEED:
                        List<ObdParamsEntity> speedData = App.getDatabase().getObdParamsDao().getLastDataForTrackId(N,
                                lastTrackEntry.getTrackID());
                        if (speedData != null && speedData.isEmpty()) {
                            GeoSamplesDao geoSamplesDao = App.getDatabase().getGeoSamplesDao();
                            List<GeoSamplesEntity> geoSpeedData = geoSamplesDao.getLastDataForTrackId(N,
                                    lastTrackEntry.getTrackID());
                            if (geoSpeedData != null && !geoSpeedData.isEmpty()) {
                                result = StreamSupport.stream(geoSpeedData)
                                        .map(GeoSamplesEntity::getSpeed)
                                        .collect(Collectors.toList());
                            }
                        } else if (speedData != null) {
                            result = StreamSupport.stream(speedData)
                                    .map(ObdParamsEntity::getCurrentSpeed)
                                    .collect(Collectors.toList());
                        }
                        break;
                }

                if (callback != null) {
                    if (result == null) result = new ArrayList<>();
                    Collections.reverse(result);
                    callback.onLastDataOfParam(result, parameter);
                }
            }
        });
    }

    public interface Callback {
        void onLastDataOfParam(List<Float> data, EcoDrivingContract.EcoDrivingParameter parameter);
    }
}

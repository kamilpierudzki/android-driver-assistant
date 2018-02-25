package com.google.kpierudzki.driverassistant.util;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingDao;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingEntity;
import com.google.kpierudzki.driverassistant.history.map.HistoryMapContract;
import com.google.kpierudzki.driverassistant.obd.database.ObdParamsDao;
import com.google.kpierudzki.driverassistant.obd.database.ObdParamsEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 26.11.2017.
 */

public class MapUtils {

    @WorkerThread
    public static void prepareMapDataWithObdParams(
            @NonNull List<HistoryMapContract.MapData> data,
            @NonNull ObdParamsDao obdParamsDao) {
        for (HistoryMapContract.MapData mapData : data) {
            mapData.obdParamsEntity = obdParamsDao.getObdEntityForTrackIdWithOffset(
                    mapData.geoSamplesEntity.getTrackId(),
                    mapData.geoSamplesEntity.getOffset());
            //If obd entity does not exist
            if (mapData.obdParamsEntity == null)
                mapData.obdParamsEntity = new ObdParamsEntity(mapData.geoSamplesEntity);
        }
    }

    @WorkerThread
    public static ArrayList<HistoryMapContract.MapData> prepareMapDataWithEcoDrivingParams(
            @NonNull List<HistoryMapContract.MapData> data,
            @NonNull EcoDrivingDao ecoDrivingDao) {
        ArrayList<HistoryMapContract.MapData> result = new ArrayList<>();
        for (HistoryMapContract.MapData mapData : data) {
            EcoDrivingEntity entity = ecoDrivingDao.getEntityForTrackIdWithOffset(
                    mapData.geoSamplesEntity.getTrackId(),
                    mapData.geoSamplesEntity.getOffset());
            if (entity != null) {
                mapData.ecoDrivingEntity = entity;
                result.add(mapData);
            }
        }
        return result;
    }
}

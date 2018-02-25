package com.google.kpierudzki.driverassistant.eco_driving.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Kamil on 15.07.2017.
 */

@Dao
public interface EcoDrivingDao {

    @Query("SELECT * FROM " + EcoDrivingEntity.TABLE_NAME)
    List<EcoDrivingEntity> getAll();

    @Insert
    void addAll(List<EcoDrivingEntity> ecoDrivings);

    @Insert
    void addAll(EcoDrivingEntity... ecoDrivings);

    @Query("DELETE FROM " + EcoDrivingEntity.TABLE_NAME + " WHERE " + EcoDrivingEntity.TRACK_ID_COL + " IN(:idsToRemove)")
    void deleteTracksData(List<Long> idsToRemove);

    @Query("SELECT * FROM " + EcoDrivingEntity.TABLE_NAME +
            " WHERE " + EcoDrivingEntity.TRACK_ID_COL + " = :trackId ORDER BY " +
            EcoDrivingEntity.OFFSET_COL + " DESC LIMIT :number")
    List<EcoDrivingEntity> getLastNSamplesForTrackId(int number, long trackId);

    @Query("SELECT * FROM "
            + EcoDrivingEntity.TABLE_NAME
            + " WHERE " + EcoDrivingEntity.TRACK_ID_COL + " = :trackId ORDER BY " +
            EcoDrivingEntity.OFFSET_COL + " DESC LIMIT :count")
    List<EcoDrivingEntity> getLastDataForTrackId(int count, long trackId);

    @Query("SELECT " + EcoDrivingEntity.CURRENT_SCORE_COL + " AS scores FROM " +
            EcoDrivingEntity.TABLE_NAME + " WHERE " + EcoDrivingEntity.TRACK_ID_COL + " = :trackId")
    List<Integer> getScoreStatisticsForTrackId(long trackId);

    @Query("SELECT COUNT(*) AS count FROM " +
            EcoDrivingEntity.TABLE_NAME + " WHERE " + EcoDrivingEntity.TRACK_ID_COL + " = :trackId")
    int getCountStatisticsForTrackId(long trackId);

    @Query("SELECT * FROM " + EcoDrivingEntity.TABLE_NAME + " WHERE " +
            EcoDrivingEntity.TRACK_ID_COL + "=:trackId AND " + EcoDrivingEntity.OFFSET_COL + "=:offset")
    EcoDrivingEntity getEntityForTrackIdWithOffset(long trackId, long offset);
}

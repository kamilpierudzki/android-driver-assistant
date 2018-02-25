package com.google.kpierudzki.driverassistant.geo_samples.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.google.kpierudzki.driverassistant.eco_driving.EcoDrivingContract;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingEntity;

import java.util.List;

/**
 * Created by Kamil on 15.07.2017.
 */

@Dao
public interface GeoSamplesDao {

    @Query("SELECT * FROM " + GeoSamplesTracksEntity.TABLE_NAME)
    List<GeoSamplesTracksEntity> getAllTracks();

    @Insert
    void addTrack(GeoSamplesTracksEntity track);

    @Query("DELETE FROM " + GeoSamplesTracksEntity.TABLE_NAME + " WHERE " + GeoSamplesTracksEntity.TRACK_ID_COL + " IN(:idsToRemove)")
    void deleteTracks(List<Long> idsToRemove);

    @Query("DELETE FROM " + GeoSamplesEntity.TABLE_NAME + " WHERE " + GeoSamplesEntity.TRACK_ID_COLUMN + " IN(:idsToRemove)")
    void deleteTracksData(List<Long> idsToRemove);

    @Query("SELECT * FROM " + GeoSamplesTracksEntity.TABLE_NAME +
            " ORDER BY `" + GeoSamplesTracksEntity.TRACK_ID_COL + "` DESC LIMIT 1")
    GeoSamplesTracksEntity getLastTrack();

    @Query("SELECT * FROM " + GeoSamplesEntity.TABLE_NAME + " WHERE " +
            GeoSamplesEntity.TRACK_ID_COLUMN + " = :trackId ORDER BY " +
            GeoSamplesEntity.OFFSET_COL_IN_SEC + " DESC LIMIT 1")
    GeoSamplesEntity getLastSampleForTrackId(long trackId);

    @Insert
    void addAll(List<GeoSamplesEntity> geoSamplesEntries);

    @Query("SELECT * FROM " + GeoSamplesEntity.TABLE_NAME +
            " WHERE " + GeoSamplesEntity.TRACK_ID_COLUMN + " = :trackId ORDER BY " +
            GeoSamplesEntity.OFFSET_COL_IN_SEC + " DESC LIMIT :number")
    List<GeoSamplesEntity> getLastNSamplesForTrackId(int number, long trackId);

    @Query("SELECT * FROM " + GeoSamplesTracksEntity.TABLE_NAME +
            " ORDER BY " + GeoSamplesTracksEntity.START_TIME_IN_SEC_COL +
            " DESC LIMIT :n")
    List<GeoSamplesTracksEntity> getLastNTracks(int n);

    @Query("SELECT * FROM " + GeoSamplesTracksEntity.TABLE_NAME + " WHERE " +
            GeoSamplesTracksEntity.START_TIME_IN_SEC_COL + " BETWEEN :startTimeInSec AND :endTimeInSec ORDER BY "
            + GeoSamplesTracksEntity.START_TIME_IN_SEC_COL + " DESC")
    List<GeoSamplesTracksEntity> getTracksBetween(long startTimeInSec, long endTimeInSec);

    @Query("SELECT "
            + GeoSamplesEntity.TABLE_NAME + "." + GeoSamplesEntity.SPEED_MPS2 + " AS speed, "
            + EcoDrivingEntity.TABLE_NAME + "." + EcoDrivingEntity.CURRENT_ACCELERATION_COL_MPS2 + " AS currentAcceleration, "
            + " 0 AS avgScore FROM "
            + GeoSamplesEntity.TABLE_NAME + " INNER JOIN " + EcoDrivingEntity.TABLE_NAME + " ON "
            + GeoSamplesEntity.TABLE_NAME + "." + GeoSamplesEntity.OFFSET_COL_IN_SEC + "="
            + EcoDrivingEntity.TABLE_NAME + "." + EcoDrivingEntity.OFFSET_COL + " WHERE "
            + GeoSamplesEntity.TABLE_NAME + "." + GeoSamplesEntity.TRACK_ID_COLUMN + "= :trackId AND "
            + EcoDrivingEntity.TABLE_NAME + "." + EcoDrivingEntity.TRACK_ID_COL + "= :trackId LIMIT :limit")
    List<EcoDrivingContract.EcoDrivingInfo> getSynchronizedLastNTrackInfoForTrackId(int limit, long trackId);

    @Query("SELECT * FROM " + GeoSamplesEntity.TABLE_NAME + " WHERE "
            + GeoSamplesEntity.TRACK_ID_COLUMN + "=:trackId ORDER BY "
            + GeoSamplesEntity.OFFSET_COL_IN_SEC + " ASC")
    List<GeoSamplesEntity> getSamplesForTrack(long trackId);

    @Query("SELECT COUNT(*) AS count FROM " + GeoSamplesEntity.TABLE_NAME + " WHERE " +
            GeoSamplesEntity.TRACK_ID_COLUMN + "=:trackId")
    long getProbesCountForTrack(long trackId);

    @Query("SELECT * FROM " + GeoSamplesEntity.TABLE_NAME +
            " WHERE " + GeoSamplesEntity.TRACK_ID_COLUMN + "=:trackId" +
            " ORDER BY `" + GeoSamplesEntity.TRACK_ID_COLUMN + "` ASC LIMIT :n,1")
    GeoSamplesEntity getExactProbe(long trackId, int n);

    @Query("SELECT * FROM "
            + GeoSamplesEntity.TABLE_NAME
            + " WHERE " + EcoDrivingEntity.TRACK_ID_COL + " = :trackId ORDER BY " +
            EcoDrivingEntity.OFFSET_COL + " DESC LIMIT :count")
    List<GeoSamplesEntity> getLastDataForTrackId(int count, long trackId);
}

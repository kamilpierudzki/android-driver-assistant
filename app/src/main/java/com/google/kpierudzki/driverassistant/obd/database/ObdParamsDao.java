package com.google.kpierudzki.driverassistant.obd.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Kamil on 15.11.2017.
 */

@Dao
public interface ObdParamsDao {

    @Query("SELECT * FROM " + ObdParamsEntity.TABLE_NAME)
    List<ObdParamsEntity> getAll();

    @Insert
    void addAll(List<ObdParamsEntity> ecoDrivings);

    @Query("DELETE FROM " + ObdParamsEntity.TABLE_NAME + " WHERE " + ObdParamsEntity.TRACK_ID_COL + " IN(:idsToRemove)")
    void deleteTracksData(List<Long> idsToRemove);

    @Query("SELECT COUNT(*) AS count FROM " + ObdParamsEntity.TABLE_NAME + " WHERE " +
            ObdParamsEntity.TRACK_ID_COL + "=:trackId")
    long getProbesCountForTrack(long trackId);

    @Query("SELECT * FROM " + ObdParamsEntity.TABLE_NAME +
            " WHERE " + ObdParamsEntity.TRACK_ID_COL + "=:trackId" +
            " ORDER BY `" + ObdParamsEntity.TRACK_ID_COL + "` ASC LIMIT :n,1")
    ObdParamsEntity getExactProbe(long trackId, int n);

    @Query("SELECT * FROM " + ObdParamsEntity.TABLE_NAME + " WHERE " +
            ObdParamsEntity.TRACK_ID_COL + "=:trackId AND " + ObdParamsEntity.OFFSET_COL + "=:offset")
    ObdParamsEntity getObdEntityForTrackIdWithOffset(long trackId, long offset);

    @Query("SELECT * FROM "
            + ObdParamsEntity.TABLE_NAME
            + " WHERE " + ObdParamsEntity.TRACK_ID_COL + " = :trackId ORDER BY " +
            ObdParamsEntity.OFFSET_COL + " DESC LIMIT :count")
    List<ObdParamsEntity> getLastDataForTrackId(int count, long trackId);
}

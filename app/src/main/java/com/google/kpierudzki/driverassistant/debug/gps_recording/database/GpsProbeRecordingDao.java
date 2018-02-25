package com.google.kpierudzki.driverassistant.debug.gps_recording.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Collection;

/**
 * Created by Kamil on 07.12.2017.
 */

@Dao
public interface GpsProbeRecordingDao {

    @Insert
    void addAll(Collection<GpsProbeRecordingEntity> entities);

    @Query("SELECT COUNT(*) AS COUNT FROM " + GpsProbeRecordingEntity.TABLE_NAME)
    int getProbesCount();

    @Query("DELETE FROM " + GpsProbeRecordingEntity.TABLE_NAME)
    void clearTable();
}

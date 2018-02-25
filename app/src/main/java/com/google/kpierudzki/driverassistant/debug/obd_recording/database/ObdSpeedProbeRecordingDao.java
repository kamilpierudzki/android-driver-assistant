package com.google.kpierudzki.driverassistant.debug.obd_recording.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Collection;

/**
 * Created by kamilpierudzki on 20/09/2017.
 */

@Dao
public interface ObdSpeedProbeRecordingDao {

    @Insert
    void addAll(Collection<ObdSpeedProbeRecordingEntity> entities);

    @Query("SELECT COUNT(*) AS COUNT FROM " + ObdSpeedProbeRecordingEntity.TABLE_NAME)
    int getProbesCount();

    @Query("DELETE FROM " + ObdSpeedProbeRecordingEntity.TABLE_NAME)
    void clearTable();
}

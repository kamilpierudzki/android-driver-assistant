package com.google.kpierudzki.driverassistant.debug.obd_recording.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Collection;

/**
 * Created by kamilpierudzki on 02/11/2017.
 */

@Dao
public interface ObdRpmProbeRecordingDao {

    @Insert
    void addAll(Collection<ObdRpmProbeRecordingEntity> entries);

    @Query("SELECT COUNT(*) AS COUNT FROM " + ObdRpmProbeRecordingEntity.TABLE_NAME)
    int getProbesCount();

    @Query("DELETE FROM " + ObdRpmProbeRecordingEntity.TABLE_NAME)
    void clearTable();
}

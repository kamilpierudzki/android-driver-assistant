package com.google.kpierudzki.driverassistant.debug.obd_recording.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Collection;

/**
 * Created by Kamil on 22.12.2017.
 */

@Dao
public interface ObdOilTempProbeRecordingDao {

    @Insert
    void addAll(Collection<ObdOilTempProbeRecordingEntity> entries);

    @Query("SELECT COUNT(*) AS COUNT FROM " + ObdOilTempProbeRecordingEntity.TABLE_NAME)
    int getProbesCount();

    @Query("DELETE FROM " + ObdOilTempProbeRecordingEntity.TABLE_NAME)
    void clearTable();
}

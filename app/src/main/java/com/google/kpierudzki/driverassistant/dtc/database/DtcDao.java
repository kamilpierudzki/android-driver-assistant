package com.google.kpierudzki.driverassistant.dtc.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;
import java.util.Set;

/**
 * Created by Kamil on 25.12.2017.
 */

@Dao
public interface DtcDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addAll(Set<DtcEntity> entity);

    @Query("SELECT * FROM " + DtcEntity.TABLE_NAME)
    List<DtcEntity> getEntities();

    @Query("DELETE FROM " + DtcEntity.TABLE_NAME)
    void clearTable();
}

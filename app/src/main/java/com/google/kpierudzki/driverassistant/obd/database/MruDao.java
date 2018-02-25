package com.google.kpierudzki.driverassistant.obd.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.Collection;
import java.util.List;

/**
 * Created by Kamil on 15.09.2017.
 */

@Dao
public interface MruDao {

    @Query("SELECT * FROM " + MruEntity.TABLE_NAME + " ORDER BY " + MruEntity.PROTOCOL_SUCCESSES_COL + " DESC")
    List<MruEntity> getMruProtocols();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void syncMruProtocolsInDb(Collection<MruEntity> protocols);
}

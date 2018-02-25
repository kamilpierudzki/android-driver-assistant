package com.google.kpierudzki.driverassistant.history.calendar.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

/**
 * Created by Kamil on 06.08.2017.
 */

@Dao
public interface HistoryCalendarDao {

    @Insert
    void addTranslation(HistoryTranslationEntity translationEntry);

    @Query("SELECT * FROM " + HistoryTranslationEntity.TABLE_NAME + " WHERE "
            + HistoryTranslationEntity.TRACK_ID_COL + "=:trackId AND "
            + HistoryTranslationEntity.POSITION_COL + "=:position")
    HistoryTranslationEntity getTranslation(long trackId, String position);
}

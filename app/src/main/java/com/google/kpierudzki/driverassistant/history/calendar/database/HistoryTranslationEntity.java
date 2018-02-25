package com.google.kpierudzki.driverassistant.history.calendar.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.kpierudzki.driverassistant.common.model.Coordinate;

import static com.google.kpierudzki.driverassistant.history.calendar.database.HistoryTranslationEntity.TABLE_NAME;

/**
 * Created by Kamil on 06.08.2017.
 */
@Entity(tableName = TABLE_NAME)
public class HistoryTranslationEntity {

    public final static String TABLE_NAME = "translations";
    public final static String POSITION_COL = "position";
    public final static String TRANSLATION_ID_COL = "translation_id";
    public final static String TRANSLATION_COL = "translation";
    public final static String TRACK_ID_COL = "track_id";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = HistoryTranslationEntity.TRANSLATION_ID_COL)
    private long translationId;

    @ColumnInfo(name = HistoryTranslationEntity.POSITION_COL)
    @TypeConverters({Coordinate.Converter.class})
    private Coordinate position;

    @ColumnInfo(name = HistoryTranslationEntity.TRANSLATION_COL)
    private String translation;

    @ColumnInfo(name = HistoryTranslationEntity.TRACK_ID_COL)
    private long trackId;

    @Ignore
    public HistoryTranslationEntity() {
        this.translationId = -1;
        this.position = new Coordinate();
        this.translation = "";
        this.trackId = -1;
    }

    public HistoryTranslationEntity(Coordinate position, String translation, long trackId) {
        this.position = position;
        this.translation = translation;
        this.trackId = trackId;
    }

    public long getTranslationId() {
        return translationId;
    }

    public void setTranslationId(long translationId) {
        this.translationId = translationId;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }
}

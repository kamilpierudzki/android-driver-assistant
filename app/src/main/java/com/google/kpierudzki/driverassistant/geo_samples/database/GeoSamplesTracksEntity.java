package com.google.kpierudzki.driverassistant.geo_samples.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.kpierudzki.driverassistant.common.model.Coordinate;

/**
 * Created by Kamil on 15.07.2017.
 */

@Entity(tableName = GeoSamplesTracksEntity.TABLE_NAME)
public class GeoSamplesTracksEntity {

    public final static String TABLE_NAME = "geosamples_tracks";
    public final static String TRACK_ID_COL = "track_id";
    public final static String POSITION_COL = "position";
    public final static String START_TIME_IN_SEC_COL = "start_time";//[s]

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = GeoSamplesTracksEntity.TRACK_ID_COL)
    private long trackID;

    @ColumnInfo(name = GeoSamplesTracksEntity.START_TIME_IN_SEC_COL)
    private long startTime;

    @ColumnInfo(name = GeoSamplesTracksEntity.POSITION_COL)
    @TypeConverters({Coordinate.Converter.class})
    private Coordinate coordinate;

    public GeoSamplesTracksEntity(long startTime, Coordinate coordinate) {
        this.startTime = startTime;
        this.coordinate = coordinate;
    }

    public long getTrackID() {
        return trackID;
    }

    public void setTrackID(long trackID) {
        this.trackID = trackID;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
}

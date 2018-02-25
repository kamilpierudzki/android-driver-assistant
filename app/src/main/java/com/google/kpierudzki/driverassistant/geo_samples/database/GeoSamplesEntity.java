package com.google.kpierudzki.driverassistant.geo_samples.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.kpierudzki.driverassistant.common.model.Coordinate;

/**
 * Created by Kamil on 16.07.2017.
 */

@Entity(tableName = GeoSamplesEntity.TABLE_NAME)
public class GeoSamplesEntity {

    public final static String TABLE_NAME = "geosamples";
    public final static String ID_COLUMN = "id";
    public final static String TRACK_ID_COLUMN = "track_id";
    public final static String POSITION_COL = "position";
    public final static String OFFSET_COL_IN_SEC = "offset";//[s]
    public final static String SPEED_MPS2 = "speed";//[m/s]

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = GeoSamplesEntity.ID_COLUMN)
    private long id;

    @ColumnInfo(name = GeoSamplesEntity.TRACK_ID_COLUMN)
    private long trackId;

    @ColumnInfo(name = GeoSamplesEntity.POSITION_COL)
    @TypeConverters({Coordinate.Converter.class})
    private Coordinate coordinate;

    @ColumnInfo(name = GeoSamplesEntity.OFFSET_COL_IN_SEC)
    private long offset;

    @ColumnInfo(name = GeoSamplesEntity.SPEED_MPS2)
    private float speed;

    @Ignore
    public GeoSamplesEntity() {
        this.id = -1;
        this.trackId = -1;
        this.coordinate = new Coordinate();
        this.offset = -1;
        this.speed = -1;
    }

    public GeoSamplesEntity(long trackId, Coordinate coordinate, long offset, float speed) {
        this.trackId = trackId;
        this.coordinate = coordinate;
        this.offset = offset;
        this.speed = speed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "GeoSamplesEntity{" +
                "id=" + id +
                ", trackId=" + trackId +
                ", coordinate=" + coordinate +
                ", offset=" + offset +
                ", speed=" + speed +
                '}';
    }
}

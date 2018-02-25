package com.google.kpierudzki.driverassistant.debug.gps_recording.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.location.Location;

import com.google.kpierudzki.driverassistant.common.model.Coordinate;

/**
 * Created by Kamil on 07.12.2017.
 */

@Entity(tableName = GpsProbeRecordingEntity.TABLE_NAME)
public class GpsProbeRecordingEntity {

    public final static String TABLE_NAME = "gps_probe_recording";
    public final static String ID_COL = "id";
    public final static String TIMESTAMP_COL = "timestamp";
    public final static String SPEED_COL = "speed";
    public final static String POSITION_COL = "position";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COL)
    private long id;

    @ColumnInfo(name = GpsProbeRecordingEntity.TIMESTAMP_COL)
    private long timestamp;

    @ColumnInfo(name = GpsProbeRecordingEntity.SPEED_COL)
    private float speed;//[m/s]

    @ColumnInfo(name = GpsProbeRecordingEntity.POSITION_COL)
    @TypeConverters({Coordinate.Converter.class})
    private Coordinate position;

    public GpsProbeRecordingEntity(long timestamp, float speed, Coordinate position) {
        this.timestamp = timestamp;
        this.speed = speed;
        this.position = position;
    }

    @Ignore
    public GpsProbeRecordingEntity(long timestamp, Location location) {
        this.timestamp = timestamp;
        this.speed = location.getSpeed();
        this.position = new Coordinate(location.getLatitude(), location.getLongitude());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "GpsProbeRecordingEntity{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", speed=" + speed +
                ", position=" + position +
                '}';
    }
}

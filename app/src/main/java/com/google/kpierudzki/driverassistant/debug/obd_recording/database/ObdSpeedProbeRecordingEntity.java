package com.google.kpierudzki.driverassistant.debug.obd_recording.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Kamil on 17.09.2017.
 */

@Entity(tableName = ObdSpeedProbeRecordingEntity.TABLE_NAME)
public class ObdSpeedProbeRecordingEntity {

    public final static String TABLE_NAME = "obd_speed_probe_recording";
    public final static String ID_COL = "id";
    public final static String TIMESTAMP_COL = "timestamp";
    public final static String SPEED_COL = "speed";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COL)
    private long id;

    @ColumnInfo(name = ObdSpeedProbeRecordingEntity.TIMESTAMP_COL)
    private long timestamp;

    @ColumnInfo(name = ObdSpeedProbeRecordingEntity.SPEED_COL)
    private float speed;

    public ObdSpeedProbeRecordingEntity(long timestamp, float speed) {
        this.timestamp = timestamp;
        this.speed = speed;
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

    @Override
    public String toString() {
        return "ObdSpeedProbeRecordingEntity{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", speed=" + speed +
                '}';
    }
}

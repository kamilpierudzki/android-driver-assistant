package com.google.kpierudzki.driverassistant.debug.obd_recording.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by kamilpierudzki on 02/11/2017.
 */

@Entity(tableName = ObdRpmProbeRecordingEntity.TABLE_NAME)
public class ObdRpmProbeRecordingEntity {

    public final static String TABLE_NAME = "obd_rpm_probe_recording";
    public final static String ID_COL = "id";
    public final static String TIMESTAMP_COL = "timestamp";
    public final static String RMP_COL = "rpm";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COL)
    private long id;

    @ColumnInfo(name = ObdRpmProbeRecordingEntity.TIMESTAMP_COL)
    private long timestamp;

    @ColumnInfo(name = ObdRpmProbeRecordingEntity.RMP_COL)
    private float rpm;

    public ObdRpmProbeRecordingEntity(long timestamp, float rpm) {
        this.timestamp = timestamp;
        this.rpm = rpm;
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

    public float getRpm() {
        return rpm;
    }

    public void setRpm(float rpm) {
        this.rpm = rpm;
    }

    @Override
    public String toString() {
        return "ObdRpmProbeRecordingEntity{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", rpm=" + rpm +
                '}';
    }
}

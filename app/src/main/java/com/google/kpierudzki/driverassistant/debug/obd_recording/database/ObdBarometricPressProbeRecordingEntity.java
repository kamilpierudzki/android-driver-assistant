package com.google.kpierudzki.driverassistant.debug.obd_recording.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Kamil on 22.12.2017.
 */

@Entity(tableName = ObdBarometricPressProbeRecordingEntity.TABLE_NAME)
public class ObdBarometricPressProbeRecordingEntity {

    public final static String TABLE_NAME = "obd_barometric_pressure_probe_recording";
    public final static String ID_COL = "id";
    public final static String TIMESTAMP_COL = "timestamp";
    public final static String BAROMETRIC_PRESSURE_COL = "barometric_pressure";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COL)
    private long id;

    @ColumnInfo(name = ObdBarometricPressProbeRecordingEntity.TIMESTAMP_COL)
    private long timestamp;

    @ColumnInfo(name = ObdBarometricPressProbeRecordingEntity.BAROMETRIC_PRESSURE_COL)
    private float barometricPressure;

    public ObdBarometricPressProbeRecordingEntity(long timestamp, float barometricPressure) {
        this.timestamp = timestamp;
        this.barometricPressure = barometricPressure;
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

    public float getBarometricPressure() {
        return barometricPressure;
    }

    public void setBarometricPressure(float barometricPressure) {
        this.barometricPressure = barometricPressure;
    }

    @Override
    public String toString() {
        return "ObdBarometricPressProbeRecordingEntity{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", barometricPressure=" + barometricPressure +
                '}';
    }
}

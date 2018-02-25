package com.google.kpierudzki.driverassistant.debug.obd_recording.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Kamil on 22.12.2017.
 */

@Entity(tableName = ObdAmbientAirTempProbeRecordingEntity.TABLE_NAME)
public class ObdAmbientAirTempProbeRecordingEntity {

    public final static String TABLE_NAME = "obd_ambient_temp_probe_recording";
    public final static String ID_COL = "id";
    public final static String TIMESTAMP_COL = "timestamp";
    public final static String AMBIENT_AIR_TEMP_COL = "ambient_air_temp";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COL)
    private long id;

    @ColumnInfo(name = ObdAmbientAirTempProbeRecordingEntity.TIMESTAMP_COL)
    private long timestamp;

    @ColumnInfo(name = ObdAmbientAirTempProbeRecordingEntity.AMBIENT_AIR_TEMP_COL)
    private float ambientAirTemp;

    public ObdAmbientAirTempProbeRecordingEntity(long timestamp, float ambientAirTemp) {
        this.timestamp = timestamp;
        this.ambientAirTemp = ambientAirTemp;
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

    public float getAmbientAirTemp() {
        return ambientAirTemp;
    }

    public void setAmbientAirTemp(float ambientAirTemp) {
        this.ambientAirTemp = ambientAirTemp;
    }

    @Override
    public String toString() {
        return "ObdAmbientAirTempProbeRecordingEntity{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", ambientAirTemp=" + ambientAirTemp +
                '}';
    }
}

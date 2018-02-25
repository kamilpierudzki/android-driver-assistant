package com.google.kpierudzki.driverassistant.debug.obd_recording.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Kamil on 22.12.2017.
 */

@Entity(tableName = ObdCoolantTempProbeRecordingEntity.TABLE_NAME)
public class ObdCoolantTempProbeRecordingEntity {

    public final static String TABLE_NAME = "obd_coolant_temp_probe_recording";
    public final static String ID_COL = "id";
    public final static String TIMESTAMP_COL = "timestamp";
    public final static String COOLANT_TEMP_COL = "coolant_temp";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COL)
    private long id;

    @ColumnInfo(name = ObdCoolantTempProbeRecordingEntity.TIMESTAMP_COL)
    private long timestamp;

    @ColumnInfo(name = ObdCoolantTempProbeRecordingEntity.COOLANT_TEMP_COL)
    private float coolantTemp;

    public ObdCoolantTempProbeRecordingEntity(long timestamp, float coolantTemp) {
        this.timestamp = timestamp;
        this.coolantTemp = coolantTemp;
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

    public float getCoolantTemp() {
        return coolantTemp;
    }

    public void setCoolantTemp(float coolantTemp) {
        this.coolantTemp = coolantTemp;
    }

    @Override
    public String toString() {
        return "ObdCoolantTempProbeRecordingEntity{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", coolantTemp=" + coolantTemp +
                '}';
    }
}

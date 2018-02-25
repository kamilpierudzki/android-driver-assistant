package com.google.kpierudzki.driverassistant.debug.obd_recording.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Kamil on 22.12.2017.
 */

@Entity(tableName = ObdOilTempProbeRecordingEntity.TABLE_NAME)
public class ObdOilTempProbeRecordingEntity {

    public final static String TABLE_NAME = "obd_oil_temp_probe_recording";
    public final static String ID_COL = "id";
    public final static String TIMESTAMP_COL = "timestamp";
    public final static String OIL_TEMP_COL = "oil_temp";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COL)
    private long id;

    @ColumnInfo(name = ObdOilTempProbeRecordingEntity.TIMESTAMP_COL)
    private long timestamp;

    @ColumnInfo(name = ObdOilTempProbeRecordingEntity.OIL_TEMP_COL)
    private float oilTemp;

    public ObdOilTempProbeRecordingEntity(long timestamp, float oilTemp) {
        this.timestamp = timestamp;
        this.oilTemp = oilTemp;
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

    public float getOilTemp() {
        return oilTemp;
    }

    public void setOilTemp(float oilTemp) {
        this.oilTemp = oilTemp;
    }

    @Override
    public String toString() {
        return "ObdOilTempProbeRecordingEntity{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", oilTemp=" + oilTemp +
                '}';
    }
}

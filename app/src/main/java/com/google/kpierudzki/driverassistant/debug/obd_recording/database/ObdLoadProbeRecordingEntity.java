package com.google.kpierudzki.driverassistant.debug.obd_recording.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Kamil on 22.12.2017.
 */

@Entity(tableName = ObdLoadProbeRecordingEntity.TABLE_NAME)
public class ObdLoadProbeRecordingEntity {

    public final static String TABLE_NAME = "obd_load_probe_recording";
    public final static String ID_COL = "id";
    public final static String TIMESTAMP_COL = "timestamp";
    public final static String LOAD_COL = "load";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COL)
    private long id;

    @ColumnInfo(name = ObdLoadProbeRecordingEntity.TIMESTAMP_COL)
    private long timestamp;

    @ColumnInfo(name = ObdLoadProbeRecordingEntity.LOAD_COL)
    private float load;

    public ObdLoadProbeRecordingEntity(long timestamp, float load) {
        this.timestamp = timestamp;
        this.load = load;
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

    public float getLoad() {
        return load;
    }

    public void setLoad(float load) {
        this.load = load;
    }

    @Override
    public String toString() {
        return "ObdLoadProbeRecordingEntity{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", load=" + load +
                '}';
    }
}

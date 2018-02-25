package com.google.kpierudzki.driverassistant.debug.obd_recording.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Kamil on 22.12.2017.
 */

@Entity(tableName = ObdMafProbeRecordingEntity.TABLE_NAME)
public class ObdMafProbeRecordingEntity {

    public final static String TABLE_NAME = "obd_maf_probe_recording";
    public final static String ID_COL = "id";
    public final static String TIMESTAMP_COL = "timestamp";
    public final static String MAF_COL = "maf";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COL)
    private long id;

    @ColumnInfo(name = ObdMafProbeRecordingEntity.TIMESTAMP_COL)
    private long timestamp;

    @ColumnInfo(name = ObdMafProbeRecordingEntity.MAF_COL)
    private float maf;

    public ObdMafProbeRecordingEntity(long timestamp, float maf) {
        this.timestamp = timestamp;
        this.maf = maf;
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

    public float getMaf() {
        return maf;
    }

    public void setMaf(float maf) {
        this.maf = maf;
    }

    @Override
    public String toString() {
        return "ObdMafProbeRecordingEntity{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", maf=" + maf +
                '}';
    }
}

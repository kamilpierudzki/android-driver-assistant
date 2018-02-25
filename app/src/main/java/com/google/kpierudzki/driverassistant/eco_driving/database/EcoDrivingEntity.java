package com.google.kpierudzki.driverassistant.eco_driving.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesEntity;

/**
 * Created by Kamil on 15.07.2017.
 */
@Entity(tableName = EcoDrivingEntity.TABLE_NAME)
public class EcoDrivingEntity {

    public final static String TABLE_NAME = "ecodriving";
    public final static String TRACK_ID_COL = "track_id";
    public final static String CURRENT_ACCELERATION_COL_MPS2 = "current_acceleration";//[m/s^2]
    public final static String OFFSET_COL = "offset";//[ms]
    public final static String ID_COL = "id";
    public final static String CURRENT_SCORE_COL = "current_score";//{0, 1}
    public final static String GENERAL_SCORE_COL = "general_score";//<0; 1>

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COL)
    private long id;

    @ColumnInfo(name = EcoDrivingEntity.TRACK_ID_COL)
    private long trackId;

    @ColumnInfo(name = EcoDrivingEntity.CURRENT_ACCELERATION_COL_MPS2)
    private float currentAcceleration;

    @ColumnInfo(name = EcoDrivingEntity.OFFSET_COL)
    private long offset;

    @ColumnInfo(name = EcoDrivingEntity.CURRENT_SCORE_COL)
    private int currentScore;

    @ColumnInfo(name = EcoDrivingEntity.GENERAL_SCORE_COL)
    private float generalScore;

    public EcoDrivingEntity(long trackId, long offset, float currentAcceleration, int currentScore, float generalScore) {
        this.trackId = trackId;
        this.currentAcceleration = currentAcceleration;
        this.offset = offset;
        this.currentScore = currentScore;
        this.generalScore = generalScore;
    }

    public EcoDrivingEntity(IGeoSampleListener.GeoSamplesSwappableData data, float currentAcceleration, int currentScore, float generalScore) {
        this.trackId = data.trackId;
        this.offset = data.offset;
        this.currentAcceleration = currentAcceleration;
        this.currentScore = currentScore;
        this.generalScore = generalScore;
    }

    @Ignore
    public EcoDrivingEntity(GeoSamplesEntity geoSamplesEntity) {
        this.trackId = geoSamplesEntity.getTrackId();
        this.offset = geoSamplesEntity.getOffset();
        this.currentAcceleration = 0;
        this.currentScore = 0;
        this.generalScore = 0;
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

    public float getCurrentAcceleration() {
        return currentAcceleration;
    }

    public void setCurrentAcceleration(float currentAcceleration) {
        this.currentAcceleration = currentAcceleration;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public float getGeneralScore() {
        return generalScore;
    }

    public void setGeneralScore(float generalScore) {
        this.generalScore = generalScore;
    }

    @Override
    public String toString() {
        return "EcoDrivingEntity{" +
                "id=" + id +
                ", trackId=" + trackId +
                ", currentAcceleration=" + currentAcceleration +
                ", offset=" + offset +
                ", currentScore=" + currentScore +
                ", generalScore=" + generalScore +
                '}';
    }
}

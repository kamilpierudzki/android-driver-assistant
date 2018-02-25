package com.google.kpierudzki.driverassistant.obd.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesEntity;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;

/**
 * Created by Kamil on 15.11.2017.
 */

@Entity(tableName = ObdParamsEntity.TABLE_NAME)
public class ObdParamsEntity {

    public final static String TABLE_NAME = "obd_params";
    public final static String TRACK_ID_COL = "track_id";
    public final static String ID_COL = "id";
    public final static String OFFSET_COL = "offset";//[ms]

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COL)
    private long id;

    @ColumnInfo(name = ObdParamsEntity.TRACK_ID_COL)
    private long trackId;

    @ColumnInfo(name = ObdParamsEntity.OFFSET_COL)
    private long offset;

    @ColumnInfo(name = ObdParamType.SPEED_COL)
    private float currentSpeed;

    @ColumnInfo(name = ObdParamType.RPM_COL)
    private float rpm;

    @ColumnInfo(name = ObdParamType.ENGINE_COOL_TEMP_COL)
    private float coolantTemp;

    @ColumnInfo(name = ObdParamType.LOAD_COL)
    private float load;

    @ColumnInfo(name = ObdParamType.BAROMETRIC_PRESSURE_COL)
    private float barometricPress;

    @ColumnInfo(name = ObdParamType.OIL_TEMP_COL)
    private float oilTemp;

    @ColumnInfo(name = ObdParamType.MAF_COL)
    private float maf;

    @ColumnInfo(name = ObdParamType.AMBIENT_TEMP_COL)
    private float ambientTemp;

    //todo rest of parametes

    @Ignore
    public ObdParamsEntity(GeoSamplesEntity geoSamplesEntity) {
        this.trackId = geoSamplesEntity.getId();
        this.offset = geoSamplesEntity.getOffset();
        this.currentSpeed = geoSamplesEntity.getSpeed();
    }

    public ObdParamsEntity(long trackId, long offset, float currentSpeed, float rpm, float coolantTemp, float load, float oilTemp, float maf, float ambientTemp, float barometricPress) {
        this.trackId = trackId;
        this.offset = offset;
        commonConstructor(currentSpeed, rpm, coolantTemp, load, oilTemp, maf, ambientTemp, barometricPress);
    }

    public ObdParamsEntity(IGeoSampleListener.GeoSamplesSwappableData data, float currentSpeed, float rpm, float coolantTemp, float load, float oilTemp, float maf, float ambientTemp, float barometricPress) {
        this.trackId = data.trackId;
        this.offset = data.offset;
        commonConstructor(currentSpeed, rpm, coolantTemp, load, oilTemp, maf, ambientTemp, barometricPress);
    }

    private void commonConstructor(float currentSpeed, float rpm, float coolantTemp, float load, float oilTemp, float maf, float ambientTemp, float barometricPress) {
        this.currentSpeed = currentSpeed;
        this.rpm = rpm;
        this.coolantTemp = coolantTemp;
        this.load = load;
        this.oilTemp = oilTemp;
        this.maf = maf;
        this.ambientTemp = ambientTemp;
        this.barometricPress = barometricPress;
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

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public float getRpm() {
        return rpm;
    }

    public void setRpm(float rpm) {
        this.rpm = rpm;
    }

    public float getCoolantTemp() {
        return coolantTemp;
    }

    public void setCoolantTemp(float coolantTemp) {
        this.coolantTemp = coolantTemp;
    }

    public float getLoad() {
        return load;
    }

    public void setLoad(float load) {
        this.load = load;
    }

    public float getOilTemp() {
        return oilTemp;
    }

    public void setOilTemp(float oilTemp) {
        this.oilTemp = oilTemp;
    }

    public float getMaf() {
        return maf;
    }

    public void setMaf(float maf) {
        this.maf = maf;
    }

    public float getAmbientTemp() {
        return ambientTemp;
    }

    public void setAmbientTemp(float ambientTemp) {
        this.ambientTemp = ambientTemp;
    }

    public float getBarometricPress() {
        return barometricPress;
    }

    public void setBarometricPress(float barometricPress) {
        this.barometricPress = barometricPress;
    }

    @Override
    public String toString() {
        return "ObdParamsEntity{" +
                "id=" + id +
                ", trackId=" + trackId +
                ", offset=" + offset +
                ", currentSpeed=" + currentSpeed +
                ", rpm=" + rpm +
                ", coolantTemp=" + coolantTemp +
                ", load=" + load +
                ", barometricPress=" + barometricPress +
                ", oilTemp=" + oilTemp +
                ", maf=" + maf +
                ", ambientTemp=" + ambientTemp +
                '}';
    }
}

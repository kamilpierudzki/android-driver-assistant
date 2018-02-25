package com.google.kpierudzki.driverassistant.dtc.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.kpierudzki.driverassistant.dtc.datamodel.DtcModel;

/**
 * Created by Kamil on 25.12.2017.
 */

@Entity(tableName = DtcEntity.TABLE_NAME, indices =
        {
                @Index(value = DtcEntity.CODE_COL, unique = true)
        })
public class DtcEntity {

    public final static String TABLE_NAME = "dtc_table";
    public final static String ID_COL = "id";
    public final static String CODE_COL = "code";
    public final static String DESC_COL = "desc";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DtcEntity.ID_COL)
    private long id;

    @ColumnInfo(name = DtcEntity.CODE_COL)
    private String code;

    @ColumnInfo(name = DtcEntity.DESC_COL)
    private String description;

    public DtcEntity(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Ignore
    public DtcEntity(DtcModel model) {
        this.code = model.code;
        this.description = model.desc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DtcEntity{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

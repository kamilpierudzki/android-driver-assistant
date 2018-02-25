package com.google.kpierudzki.driverassistant.obd.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Kamil on 15.09.2017.
 */

@Entity(tableName = MruEntity.TABLE_NAME)
public class MruEntity {

    public final static String TABLE_NAME = "mru_successes";
    public final static String PROTOCOL_NUMBER_COL = "protocol_number";
    public final static String PROTOCOL_SUCCESSES_COL = "protocol_successes";

    @PrimaryKey
    @ColumnInfo(name = MruEntity.PROTOCOL_NUMBER_COL)
    public int protocolNumber;

    @ColumnInfo(name = MruEntity.PROTOCOL_SUCCESSES_COL)
    public int successCount;

    public MruEntity(int protocolNumber, int successCount) {
        this.protocolNumber = protocolNumber;
        this.successCount = successCount;
    }

    public int getProtocolNumber() {
        return protocolNumber;
    }

    public void setProtocolNumber(int protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
}

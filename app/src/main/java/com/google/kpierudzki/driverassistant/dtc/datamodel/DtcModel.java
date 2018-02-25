package com.google.kpierudzki.driverassistant.dtc.datamodel;

/**
 * Created by Kamil on 27.12.2017.
 */

public class DtcModel {

    public String code;
    public String desc;

    public DtcModel(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "DtcModel{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtcModel dtcModel = (DtcModel) o;

        return code.equals(dtcModel.code) && desc.equals(dtcModel.desc);
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + desc.hashCode();
        return result;
    }
}

package com.google.kpierudzki.driverassistant.common.model;

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverter;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Created by Kamil on 26.06.2017.
 */

public class Coordinate implements Parcelable {

    public double latitude;
    public double longitude;

    @Ignore
    public Coordinate() {
        this.latitude = 0;
        this.longitude = 0;
    }

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinate(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%f,%f", latitude, longitude);
    }

    public static class Converter {

        @TypeConverter
        public static String fromCoordinate(Coordinate coordinate) {
            return String.format(Locale.US, "%f,%f",
                    coordinate.latitude, coordinate.longitude);
        }

        @TypeConverter
        public static Coordinate fromString(String coordinate) {
            String[] slices = coordinate.split(",");
            try {
                return new Coordinate(Double.valueOf(slices[0]), Double.valueOf(slices[1]));
            } catch (Exception e) {
                return new Coordinate(0, 0);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    protected Coordinate(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Parcelable.Creator<Coordinate> CREATOR = new Parcelable.Creator<Coordinate>() {
        @Override
        public Coordinate createFromParcel(Parcel source) {
            return new Coordinate(source);
        }

        @Override
        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };
}

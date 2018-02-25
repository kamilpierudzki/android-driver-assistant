package com.google.kpierudzki.driverassistant.common.model;

/**
 * Created by Kamil on 26.06.2017.
 */

public class DatabaseSample {

    public Coordinate coordinate;
    public float speed;
    public long timestamp;

    public DatabaseSample(Coordinate coordinate, float speed, long timestamp) {
        this.coordinate = coordinate;
        this.speed = speed;
        this.timestamp = timestamp;
    }
}

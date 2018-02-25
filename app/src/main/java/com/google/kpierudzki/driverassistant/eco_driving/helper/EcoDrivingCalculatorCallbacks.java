package com.google.kpierudzki.driverassistant.eco_driving.helper;

/**
 * Created by Kamil on 08.10.2017.
 */

public interface EcoDrivingCalculatorCallbacks {
    void onSpeedChanged(float speed);

    void onAccelerationChanged(float acceleration);

    void onAvgScoreChanged(float score);
}

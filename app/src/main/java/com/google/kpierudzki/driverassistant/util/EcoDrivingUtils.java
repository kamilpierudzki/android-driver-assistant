package com.google.kpierudzki.driverassistant.util;

import android.hardware.SensorEvent;

import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingEntity;

import java.util.ArrayList;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by kamilpierudzki on 06/09/2017.
 */

public class EcoDrivingUtils {

    public static float calculateCurrentScore(float currentAcceleration, double accelerationLimit) {
        float currentScore = (float) ((Math.abs(currentAcceleration) * 100.0f) / accelerationLimit);
        if (currentScore <= 100) currentScore = 100;
        else currentScore = Math.max(0, 200 - currentScore);

        return currentScore;
    }

    public static float calculateSumOfBuffer(List<EcoDrivingEntity> buffer) {
        float result = 0f;
        for (EcoDrivingEntity ecoDrivingEntity : buffer)
            result += ecoDrivingEntity.getCurrentScore();
        return result;
    }

    public static float calculateCurrentAcceleration(SensorEvent sensorEvent) {
        final float gravity = 9.81f;
        return (Math.abs(sensorEvent.values[0]) +
                Math.abs(sensorEvent.values[1]) +
                Math.abs(sensorEvent.values[2]) - gravity) / 3;
    }

    public static float calculateAvgAccelerationFromSamples(List<Float> accelerationSamples) {
        float sum = 0f;
        for (Float accelerationSample : accelerationSamples) sum += accelerationSample;
        return sum / (accelerationSamples.size() * 1.0f);
    }

    public static List<Float> rollbackScoreHistory(List<Integer> scores) {
        List<Float> result = new ArrayList<>();
        for (Integer score : scores) {
            List<Float> tmp = new ArrayList<Float>(result) {{
                add(score * 1f);
            }};
            float sum = MathUtil.sumKahan(tmp.toArray(new Float[0]));

            result.add(sum / tmp.size());
        }

        result = StreamSupport.stream(result).map(score -> score * 100f)
                .collect(Collectors.toList());
        return result;
    }
}

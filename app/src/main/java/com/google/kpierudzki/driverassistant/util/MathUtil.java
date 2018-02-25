package com.google.kpierudzki.driverassistant.util;

import java.util.List;

/**
 * Created by Kamil on 08.10.2017.
 */

public class MathUtil {

    public static float sumKahan(Float... floats) {
        float sum = 0.0f;
        float err = 0.0f;
        for (int i = 0; i < floats.length; ++i) {
            float y = floats[i] - err;
            float temp = sum + y;
            err = (temp - sum) - y;
            sum = temp;
        }
        return sum;
    }

    public static int sum(Integer... ints) {
        int sum = 0;
        for (Integer anInt : ints) sum += anInt;
        return sum;
    }

    public static float[] objectToPrimitives(List<Float> floats) {
        float[] result = new float[floats.size()];
        for (int i = 0; i < floats.size(); i++) result[i] = floats.get(i);
        return result;
    }
}

package com.google.kpierudzki.driverassistant.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Kamil on 18.07.2017.
 */

public class UnitUtils {

    public static float mpsToKmh(float mps) {
        return mps * 3.6f;
    }

    public static float roundScore(float score) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        try {
            return Float.valueOf(df.format(score).replace(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

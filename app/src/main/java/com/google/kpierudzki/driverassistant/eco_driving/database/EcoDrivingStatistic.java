package com.google.kpierudzki.driverassistant.eco_driving.database;

import android.arch.persistence.room.Ignore;

import com.google.kpierudzki.driverassistant.util.MathUtil;

import java.util.List;

/**
 * Created by Kamil on 27.07.2017.
 */

public class EcoDrivingStatistic {
    public int sum;
    public int count;

    @Ignore
    public EcoDrivingStatistic(List<Integer> scores, int count) {
        this.count = count;
        this.sum = MathUtil.sum(scores.toArray(new Integer[0]));
        scores.clear();
    }

    public static EcoDrivingStatistic merge(List<Integer> scores, int count) {
        return new EcoDrivingStatistic(scores, count);
    }
}

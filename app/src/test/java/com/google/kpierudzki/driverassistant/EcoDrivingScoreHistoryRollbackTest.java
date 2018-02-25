package com.google.kpierudzki.driverassistant;

import com.google.kpierudzki.driverassistant.util.EcoDrivingUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 07.12.2017.
 */

public class EcoDrivingScoreHistoryRollbackTest {

    @Test
    public void rollbackTest() {
        final int N = 30;

        List<Integer> allOnes = new ArrayList<>();
        for (int i = 0; i < N; i++) allOnes.add(1);
        List<Float> history1 = EcoDrivingUtils.rollbackScoreHistory(allOnes);

        List<Integer> halfOnes = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            if (i % 2 == 0)
                halfOnes.add(1);
            else
                halfOnes.add(0);
        }
        List<Float> history2 = EcoDrivingUtils.rollbackScoreHistory(halfOnes);
    }
}

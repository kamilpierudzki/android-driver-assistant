package com.google.kpierudzki.driverassistant.common.view_components;

import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;

import java.util.List;

/**
 * Created by Kamil on 03.12.2017.
 */

public interface IChartAble {
    void updateValue(float newValue, ObdParamType paramType);

    void updateChart(float newValue, ObdParamType paramType);

    void restoreValuesAndUpdateChart(List<Float> values);

    Float[] getValues();

    int getChartEntriesLimit();
}

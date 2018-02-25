package com.google.kpierudzki.driverassistant.eco_driving.view;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.kpierudzki.driverassistant.GlobalConfig;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.common.view_components.IChartAble;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kamil on 05.09.2017.
 */

public class ChartManagerNew implements IChartAble {

    private LineChart chartWidget;
    private Context context;

    private LinkedList<Float> values = new LinkedList<>();

    private int CHART_ENTRIES_LIMIT;

    public ChartManagerNew(Context context, LineChart chartWidget) {
        this.chartWidget = chartWidget;
        this.context = context;

        CHART_ENTRIES_LIMIT = context.getResources().getInteger(R.integer.EcoDriving_Chart_Entries);

        if (chartWidget != null) {
            styleChart();
        }
    }

    @Override
    public void updateValue(float newValue, ObdParamType paramType) {
        //Ignore
    }

    @MainThread
    @Override
    public void updateChart(float newValue, ObdParamType paramType) {
        values.addLast(newValue);
        if (values.size() > CHART_ENTRIES_LIMIT)
            values.removeFirst();

        ArrayList<Entry> accelerationEntries = new ArrayList<>();
        for (int i = 0; i < values.size(); i++)
            accelerationEntries.add(new Entry(i, values.get(i)));

        LineDataSet scoreDataSet = new LineDataSet(accelerationEntries, context.getString(R.string.EcoDriving_Chart_DataSet));
        styleLineDataSet(scoreDataSet);

        if (chartWidget != null) {
            chartWidget.setData(new LineData(scoreDataSet));
            chartWidget.invalidate();
        }
    }

    @MainThread
    @Override
    public void restoreValuesAndUpdateChart(List<Float> values) {
        if (values.size() > 0) {
            for (int i = 0; i < values.size() - 1; i++)//add all values except the last one
                this.values.add(values.get(i));
            updateChart(values.get(values.size() - 1), null);//add the last one now to trigger update of chart.
        }
    }

    @Override
    public Float[] getValues() {
        return values.toArray(new Float[0]);
    }

    @Override
    public int getChartEntriesLimit() {
        return CHART_ENTRIES_LIMIT;
    }

    private void styleChart() {
        chartWidget.getXAxis().setEnabled(false);

        YAxis leftYAxis = chartWidget.getAxisLeft();
        leftYAxis.setTextColor(ContextCompat.getColor(context, R.color.EcoDriving_Chart_Line));

        LimitLine maxLimit = new LimitLine((float) GlobalConfig.ECO_DRIVING_GPS_OPTIMAL_ACCELERATION_LIMIT, context.getString(R.string.EcoDriving_Chart_Optimal));
        maxLimit.setLineColor(ContextCompat.getColor(context, R.color.EcoDriving_Chart_OptimalLimits));
        leftYAxis.addLimitLine(maxLimit);

        LimitLine minLimit = new LimitLine((float) (GlobalConfig.ECO_DRIVING_GPS_OPTIMAL_ACCELERATION_LIMIT * -1f), context.getString(R.string.EcoDriving_Chart_Optimal));
        minLimit.setLineColor(ContextCompat.getColor(context, R.color.EcoDriving_Chart_OptimalLimits));
        leftYAxis.addLimitLine(minLimit);

        leftYAxis.setAxisMaximum(maxLimit.getLimit() * 1.5f);
        leftYAxis.setAxisMinimum(minLimit.getLimit() * 1.5f);

        chartWidget.setTouchEnabled(false);
        chartWidget.getAxisRight().setEnabled(false);
        chartWidget.setViewPortOffsets(0, 0, 0, 0);

        Description description = new Description();
        description.setEnabled(false);
        chartWidget.setDescription(description);
    }

    private void styleLineDataSet(LineDataSet lineDataSet) {
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.EcoDriving_Chart_Line));
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
    }
}

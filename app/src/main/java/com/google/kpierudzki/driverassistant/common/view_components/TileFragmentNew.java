package com.google.kpierudzki.driverassistant.common.view_components;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 03.12.2017.
 */

public class TileFragmentNew extends Fragment implements IChartAble {

    public static final String KEY_PARAM_TYPE = "KEY_PARAM_TYPE";
    private static final int LIMIT = 30;
    private final static String CHART_DATA_KEY = "CHART_DATA_KEY";

    @BindView(R.id.tile_chart)
    LineChart chartWidget;

    @BindView(R.id.tile_value)
    TextView tileValue;

    @BindView(R.id.tile_unit)
    TextView tileUnit;

    @BindView(R.id.tile_title)
    TextView tileTitle;

    private LinkedList<Float> values;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tile_layout, container, false);
        ButterKnife.bind(this, root);

        values = new LinkedList<>();

        Bundle arguments = getArguments();
        if (arguments != null) {
            ObdParamType paramType = (ObdParamType) arguments.getSerializable(KEY_PARAM_TYPE);
            if (paramType != null) {
                tileTitle.setText(paramType.getLocalizedName());
                tileUnit.setText(paramType.getUnit());
                tileValue.setText("--");
            }
        }

        styleChart();

        if (savedInstanceState != null) {
            float[] restoredValues = savedInstanceState.getFloatArray(CHART_DATA_KEY);
            if (restoredValues != null && restoredValues.length > 0) {
                List<Float> vals = new ArrayList<>();
                for (float restoredValue : restoredValues) vals.add(restoredValue);
                restoreValuesAndUpdateChart(vals);
            }
        }

        return root;
    }

    private void styleChart() {
        Description description = new Description();
        description.setEnabled(false);
        chartWidget.setDescription(description);
        chartWidget.getAxisLeft().setEnabled(false);
        chartWidget.getAxisRight().setEnabled(false);
        chartWidget.getXAxis().setEnabled(false);
        chartWidget.setViewPortOffsets(0, 0, 0, 0);
        chartWidget.getLegend().setEnabled(false);
        chartWidget.setScaleEnabled(false);
        chartWidget.setNoDataText("");
    }

    private void styleLineDataSet(LineDataSet lineDataSet) {
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(ContextCompat.getColor(App.getAppContext(), R.color.Tile_Chart_Background_Bottom));
        lineDataSet.setColors(ContextCompat.getColor(App.getAppContext(), R.color.Tile_Chart_Line));
//        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
    }

    @Override
    public void updateValue(float newValue, ObdParamType paramType) {
        if (tileValue != null)
            tileValue.setText(String.format(Locale.getDefault(), "%.0f", newValue));
    }

    @MainThread
    @Override
    public void updateChart(float newValue, ObdParamType paramType) {
        values.add(newValue);
        if (values.size() > LIMIT) values.removeFirst();

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) entries.add(new Entry(i, values.get(i)));

        LineDataSet lineDataSet = new LineDataSet(entries, "");
        styleLineDataSet(lineDataSet);
        LineData lineData = new LineData(lineDataSet);

        chartWidget.setData(lineData);
        chartWidget.invalidate();
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
        return LIMIT;
    }

    public static TileFragmentNew newInstance(ObdParamType paramType) {
        TileFragmentNew newFragment = new TileFragmentNew();
        Bundle args = new Bundle();
        args.putSerializable(KEY_PARAM_TYPE, paramType);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Float[] data = getValues();
        float[] toStore = new float[data.length];
        for (int i = 0; i < data.length; i++) toStore[i] = data[i];
        outState.putFloatArray(CHART_DATA_KEY, toStore);
    }
}

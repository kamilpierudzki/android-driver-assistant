package com.google.kpierudzki.driverassistant.history.map.view.parameters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.kpierudzki.driverassistant.history.map.HistoryMapContract;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Kamil on 15.11.2017.
 */

public class MapParametersManager {

    private MapParametersAdapter adapter;

    private List<HistoryMapContract.MapData> data;

    public MapParametersManager(@NonNull RecyclerView recyclerView, ICallbacks callbacks, Calendar trackDate) {
        adapter = new MapParametersAdapter(callbacks, trackDate);
        data = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Nullable
    public HistoryMapContract.MapData refresh(int progress) {
        if (data != null && !data.isEmpty() && progress < data.size()) {
            adapter.setData(data.get(progress));
            adapter.notifyDataSetChanged();
            return data.get(progress);
        }
        return null;
    }

    public void setData(@NonNull List<HistoryMapContract.MapData> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    public interface ICallbacks {
        void onParameterClicked(ObdParamType paramType);
    }
}

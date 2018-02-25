package com.google.kpierudzki.driverassistant.debug.obd_recording.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;

import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 22.12.2017.
 */

public class ObdProbesRecordingAdapter extends RecyclerView.Adapter<ObdProbesRecordingViewHolder> {

    private List<ObdProbeRecordingModel> params;

    public ObdProbesRecordingAdapter(List<ObdParamType> params) {
        this.params = StreamSupport.stream(params)
                .map(paramType -> new ObdProbeRecordingModel(paramType, 0))
                .collect(Collectors.toList());
    }

    @Override
    public ObdProbesRecordingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ObdProbesRecordingViewHolder(LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_obd_recording, parent, false));
    }

    @Override
    public void onBindViewHolder(ObdProbesRecordingViewHolder holder, int position) {
        holder.setItem(params.get(position));
    }

    @Override
    public int getItemCount() {
        return params.size();
    }

    void updateData(ObdProbeRecordingModel model) {
        StreamSupport.stream(params)
                .filter(obdProbeRecordingModel -> obdProbeRecordingModel.paramType == model.paramType)
                .forEach(obdProbeRecordingModel -> obdProbeRecordingModel.count = model.count);
    }

    public static class ObdProbeRecordingModel {

        public ObdParamType paramType;
        public int count;

        ObdProbeRecordingModel(ObdParamType paramType, int count) {
            this.paramType = paramType;
            this.count = count;
        }
    }
}

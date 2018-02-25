package com.google.kpierudzki.driverassistant.debug.obd_recording.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.kpierudzki.driverassistant.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 22.12.2017.
 */

public class ObdProbesRecordingViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.obd_recording_name)
    TextView name;

    @BindView(R.id.obd_recording_count)
    TextView count;

    public ObdProbesRecordingViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setItem(ObdProbesRecordingAdapter.ObdProbeRecordingModel model) {
        name.setText(model.paramType.getParameterInfoDialogTitle());
        count.setText(String.format(Locale.getDefault(), "%d", model.count));
    }
}

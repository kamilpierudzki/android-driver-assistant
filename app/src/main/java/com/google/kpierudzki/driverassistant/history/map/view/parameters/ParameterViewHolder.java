package com.google.kpierudzki.driverassistant.history.map.view.parameters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.kpierudzki.driverassistant.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 15.11.2017.
 */

public class ParameterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private MapParametersManager.ICallbacks callbacks;
    private MapParametersAdapter.EntityModel currentModel;

    @BindView(R.id.map_item_param_desc)
    TextView paramDesc;

    @BindView(R.id.map_item_param_value)
    TextView paramValue;

    @BindView(R.id.map_item_param_unit)
    TextView paramUnit;

    ParameterViewHolder(View itemView, MapParametersManager.ICallbacks callbacks) {
        super(itemView);
        this.callbacks = callbacks;

        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(this);
    }

    void setItem(MapParametersAdapter.EntityModel model) {
        this.currentModel = model;
        paramDesc.setText(model.paramType.getLocalizedName());
        paramValue.setText(model.value);
        paramUnit.setText(model.paramType.getUnit());
    }

    @Override
    public void onClick(View view) {
        callbacks.onParameterClicked(currentModel.paramType);
    }
}

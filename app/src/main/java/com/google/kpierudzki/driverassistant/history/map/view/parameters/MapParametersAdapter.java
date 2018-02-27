package com.google.kpierudzki.driverassistant.history.map.view.parameters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.history.map.HistoryMapContract;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.util.TimeUtils;
import com.google.kpierudzki.driverassistant.util.UnitUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kamil on 15.11.2017.
 */

public class MapParametersAdapter extends RecyclerView.Adapter<ParameterViewHolder> {

    private List<EntityModel> models;
    private Calendar _trackDate;

    MapParametersAdapter(Calendar trackDate) {
        models = new ArrayList<>();
        _trackDate = trackDate;
    }

    @Override
    public ParameterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ParameterViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_map_param, parent, false));
    }

    @Override
    public void onBindViewHolder(ParameterViewHolder holder, int position) {
        holder.setItem(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setData(HistoryMapContract.MapData data) {
        if (data.obdParamsEntity != null) {
            models.clear();
            Calendar local = (Calendar) _trackDate.clone();
            local.add(Calendar.SECOND, (int) data.geoSamplesEntity.getOffset());
            models.add(new EntityModel(TimeUtils.formatDateForMapToolbarParameter(local), ObdParamType.DATE));
            models.add(new EntityModel(
                    String.format(
                            Locale.getDefault(),
                            "%d",
                            (int) (data.ecoDrivingEntity.getGeneralScore() * 100f)),
                    ObdParamType.ECO_SCORE));
            models.add(new EntityModel(UnitUtils.mpsToKmh(data.obdParamsEntity.getCurrentSpeed()), ObdParamType.SPEED));//[m/s] -> [km/h]
            models.add(new EntityModel(data.obdParamsEntity.getRpm(), ObdParamType.ENGINE_RPM));
            models.add(new EntityModel(data.obdParamsEntity.getMaf(), ObdParamType.MAF));
            models.add(new EntityModel(data.obdParamsEntity.getCoolantTemp(), ObdParamType.COOLANT_TEMP));
            models.add(new EntityModel(data.obdParamsEntity.getLoad(), ObdParamType.ENGINE_LOAD));
            models.add(new EntityModel(data.obdParamsEntity.getBarometricPress(), ObdParamType.BAROMETRIC_PRESSURE));
            models.add(new EntityModel(data.obdParamsEntity.getOilTemp(), ObdParamType.OIL_TEMP));
            models.add(new EntityModel(data.obdParamsEntity.getAmbientTemp(), ObdParamType.AMBIENT_AIR_TEMP));
            //todo rest of parameters
        }
    }

    static class EntityModel {
        String value;
        ObdParamType paramType;

        EntityModel(float value, ObdParamType paramType) {
            this.value = String.format(Locale.getDefault(), "%.2f", value);
            this.paramType = paramType;
        }

        EntityModel(String value, ObdParamType paramType) {
            this.value = value;
            this.paramType = paramType;
        }
    }
}

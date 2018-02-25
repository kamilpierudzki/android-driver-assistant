package com.google.kpierudzki.driverassistant.dtc.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.dtc.database.DtcEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 25.12.2017.
 */

public class DtcAdapter extends RecyclerView.Adapter<DtcViewHolder> {

    private List<DtcEntity> data;

    DtcAdapter() {
        data = new ArrayList<>();
    }

    @Override
    public DtcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DtcViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dtc, null));
    }

    @Override
    public void onBindViewHolder(DtcViewHolder holder, int position) {
        holder.setItem(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<DtcEntity> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    public boolean areAnyData() {
        return !data.isEmpty();
    }
}

package com.google.kpierudzki.driverassistant.history.calendar.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.history.calendar.HistoryCalendarContract;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 25.07.2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

    private ArrayList<HistoryCalendarContract.CalendarTranslateInfoModel> data;
    private Callbacks callbacks;

    HistoryAdapter(Callbacks callbacks) {
        data = new ArrayList<>();
        this.callbacks = callbacks;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, null), callbacks);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        holder.setItem(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<HistoryCalendarContract.CalendarTranslateInfoModel> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    void removeData(List<HistoryCalendarContract.CalendarTranslateInfoModel> toRemove) {
        if (toRemove.size() == data.size()) {
            data.clear();
            notifyDataSetChanged();
        } else {
            //For UI remove
            int pos = -1;
            for (int i = 0; i < data.size(); i++) {
                if (exists(data.get(i).trackId, toRemove))
                    notifyItemRemoved(i);
            }

            //for data remove
            HistoryCalendarContract.CalendarTranslateInfoModel currentElement;
            Iterator<HistoryCalendarContract.CalendarTranslateInfoModel> iter = data.iterator();
            while (iter.hasNext()) {
                currentElement = iter.next();
                if (exists(currentElement.trackId, toRemove))
                    iter.remove();
            }
        }
    }

    private boolean exists(long trackId, List<HistoryCalendarContract.CalendarTranslateInfoModel> elements) {
        for (HistoryCalendarContract.CalendarTranslateInfoModel element : elements)
            if (element.trackId == trackId)
                return true;
        return false;
    }

    void updateTrackInfo(HistoryCalendarContract.CalendarTranslateInfoModel trackInfo) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).trackId == trackInfo.trackId) {
                data.set(i, trackInfo);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public ArrayList<HistoryCalendarContract.CalendarTranslateInfoModel> getData() {
        return data;
    }

    void showRemoveMode(boolean show) {
        StreamSupport.stream(data).forEach(model -> model.checkboxVisible = show);
        notifyDataSetChanged();
    }

    public interface Callbacks {
        void onTrackSelected(HistoryCalendarContract.CalendarTranslateInfoModel track);

        void onItemChecked(HistoryCalendarContract.CalendarTranslateInfoModel checkedItem, boolean isChecked);

        void onItemLongPressed();
    }
}

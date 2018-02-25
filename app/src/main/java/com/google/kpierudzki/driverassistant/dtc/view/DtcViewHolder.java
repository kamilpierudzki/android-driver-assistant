package com.google.kpierudzki.driverassistant.dtc.view;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.dtc.database.DtcEntity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 25.12.2017.
 */

public class DtcViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_dtc_icon)
    AppCompatTextView iconText;

    @BindView(R.id.item_dtc_code)
    TextView codeText;

    @BindView(R.id.item_dtc_desc)
    TextView descText;

    public DtcViewHolder(View itemView) {
        super(itemView);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ButterKnife.bind(this, itemView);
    }

    public void setItem(DtcEntity item) {
        iconText.setText(String.format(Locale.getDefault(), "%s", item.getCode().charAt(0)));
        codeText.setText(item.getCode());
        descText.setText(item.getDescription());
    }
}

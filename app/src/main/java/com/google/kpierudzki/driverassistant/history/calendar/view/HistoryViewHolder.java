package com.google.kpierudzki.driverassistant.history.calendar.view;

import android.content.res.ColorStateList;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.history.calendar.HistoryCalendarContract;
import com.google.kpierudzki.driverassistant.util.ScreenUtils;
import com.google.kpierudzki.driverassistant.util.TimeUtils;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 24.07.2017.
 */

public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnLongClickListener {

    private final int MARGIN_REMOVE_MODE_IN_DP = 48;
    private final int MARGIN_NORMAL_MODE_IN_DP = 8;

    @BindView(R.id.history_item_title)
    TextView title;

    @BindView(R.id.history_item_desc)
    TextView description;

    @BindView(R.id.history_item_date)
    TextView date;

    @BindView(R.id.history_item_icon)
    AppCompatTextView scoreView;

    @BindView(R.id.history_item_checkbox)
    CheckBox checkBox;

    private HistoryAdapter.Callbacks callbacks;
    private HistoryCalendarContract.CalendarTranslateInfoModel currentTrackInfo;
    private SpringAnimation marginAnimation;
    private SpringForce animationForce;

    public HistoryViewHolder(View itemView, HistoryAdapter.Callbacks callbacks) {
        super(itemView);
        this.callbacks = callbacks;
        itemView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ButterKnife.bind(this, itemView);

        itemView.setOnLongClickListener(this);

        marginAnimation = new SpringAnimation(scoreView, new FloatPropertyCompat<AppCompatTextView>("margin_animation") {
            @Override
            public float getValue(AppCompatTextView object) {
                return ((ConstraintLayout.LayoutParams) object.getLayoutParams()).leftMargin;
            }

            @Override
            public void setValue(AppCompatTextView object, float value) {
                ConstraintLayout.LayoutParams clp = (ConstraintLayout.LayoutParams) object.getLayoutParams();
                clp.leftMargin = (int) value;
                object.setLayoutParams(clp);
            }
        });

        animationForce = new SpringForce();
        animationForce.setStiffness(SpringForce.STIFFNESS_MEDIUM);
        animationForce.setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        marginAnimation.setSpring(animationForce);

    }

    public enum RemoveMode {
        SHOW, HIDE;
    }

    private void prepareFinalAnimationPositionAndStart(RemoveMode removeMode) {
        switch (removeMode) {
            case SHOW:
                animationForce.setFinalPosition(ScreenUtils.dpToPx(MARGIN_REMOVE_MODE_IN_DP));
                break;
            case HIDE:
                animationForce.setFinalPosition(ScreenUtils.dpToPx(MARGIN_NORMAL_MODE_IN_DP));
                break;
        }
        marginAnimation.start();
    }

    private void setMargin(RemoveMode removeMode) {
        ConstraintLayout.LayoutParams clp = (ConstraintLayout.LayoutParams) scoreView.getLayoutParams();
        switch (removeMode) {
            case SHOW:
                clp.leftMargin = (int) ScreenUtils.dpToPx(MARGIN_REMOVE_MODE_IN_DP);
                scoreView.setLayoutParams(clp);
                break;
            case HIDE:
                clp.leftMargin = (int) ScreenUtils.dpToPx(MARGIN_NORMAL_MODE_IN_DP);
                scoreView.setLayoutParams(clp);
                break;
        }
    }

    public void setItem(HistoryCalendarContract.CalendarTranslateInfoModel trackInfo) {
        this.currentTrackInfo = trackInfo;
        title.setText(trackInfo.startPointTranslation);
        description.setText(trackInfo.endPointTranslation);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(trackInfo.startTimeInSec));
        date.setText(TimeUtils.formatDateForHistoryList(calendar));
        scoreView.setText(String.format(Locale.getDefault(), "%d", (int) (trackInfo.score * 100)));
        ViewCompat.setBackgroundTintList(scoreView, ColorStateList.valueOf(
                ContextCompat.getColor(App.getAppContext(), trackInfo.scoreColor.getColor())));

        if (trackInfo.checkboxVisible) {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setOnCheckedChangeListener(this);
            prepareFinalAnimationPositionAndStart(RemoveMode.SHOW);
            itemView.setOnClickListener(v -> checkBox.setChecked(!checkBox.isChecked()));
        } else {
            checkBox.setVisibility(View.INVISIBLE);
            checkBox.setOnCheckedChangeListener(null);
            prepareFinalAnimationPositionAndStart(RemoveMode.HIDE);
            itemView.setOnClickListener(this);
        }

        checkBox.setChecked(trackInfo.isSelected);
    }

    @Override
    public void onClick(View view) {
        callbacks.onTrackSelected(currentTrackInfo);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        callbacks.onItemChecked(currentTrackInfo, isChecked);
    }

    @Override
    public boolean onLongClick(View v) {
        callbacks.onItemLongPressed();
        return true;
    }
}

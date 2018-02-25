package com.google.kpierudzki.driverassistant.history.calendar.view

import android.support.annotation.ColorRes
import com.google.kpierudzki.driverassistant.R

/**
 * Created by Kamil on 22.12.2017.
 */
enum class ScoreColor(private val rangeStart: Float, private val rangeEnd: Float, @ColorRes
private val color: Int) {

    Best(0.9f, 1f, R.color.History_Calendar_Score_Best),
    Good(0.75f, 0.89f, R.color.History_Calendar_Score_Good),
    Normal(0.65f, 0.74f, R.color.History_Calendar_Score_Normal),
    Weak(0.5f, 0.65f, R.color.History_Calendar_Score_Weak),
    Worst(0f, 0.49f, R.color.History_Calendar_Score_Worst),
    Unknown(-1f, -1f, R.color.History_Calendar_Score_Unknown);

    companion object {
        fun matchColor(score: Float): ScoreColor {
            for (value in values()) {
                if (value.rangeStart <= score && score < value.rangeEnd)
                    return value;
            }
            return Unknown;
        }
    }

    @ColorRes
    fun getColor(): Int {
        return color
    }
}
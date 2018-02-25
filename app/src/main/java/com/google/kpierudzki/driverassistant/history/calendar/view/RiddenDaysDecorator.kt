package com.google.kpierudzki.driverassistant.history.calendar.view

import android.os.Build
import com.google.kpierudzki.driverassistant.App
import com.google.kpierudzki.driverassistant.R
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesTracksEntity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by Kamil on 14.01.2018.
 */
class RiddenDaysDecorator : DayViewDecorator {

    var riddenDates = ArrayList<CalendarDay>()

    fun setData(allRiddenDates: List<GeoSamplesTracksEntity>) {
        riddenDates = allRiddenDates.map {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = TimeUnit.SECONDS.toMillis(it.startTime)
            CalendarDay.from(calendar)
        }.toCollection(ArrayList<CalendarDay>())
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return riddenDates.firstOrNull({ riddenDate ->
            riddenDate.month == day.month && riddenDate.day == day.day
        }) != null
    }

    override fun decorate(view: DayViewFacade) {
        view.setBackgroundDrawable(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            App.getAppContext().resources.getDrawable(
                    R.drawable.ridden_day,
                    App.getAppContext().theme)
        else
            App.getAppContext().resources.getDrawable(R.drawable.ridden_day))
    }
}
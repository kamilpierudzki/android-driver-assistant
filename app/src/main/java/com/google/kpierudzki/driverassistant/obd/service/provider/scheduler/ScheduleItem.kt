package com.google.kpierudzki.driverassistant.obd.service.provider.scheduler

import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel

/**
 * Created by Kamil on 21.12.2017.
 */
open class ScheduleItem(var frequency: Int, var command: ObdCommandModel) {

    override fun toString(): String {
        return "ScheduleItem{" +
                "frequency=" + frequency +
                ", command=" + command +
                '}'
    }
}
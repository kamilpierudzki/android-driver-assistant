package com.google.kpierudzki.driverassistant.util

import com.google.kpierudzki.driverassistant.obd.service.commandmodels.SpeedCommand
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.LoadCommand
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.MassAirFlowCommand
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.OilTempCommand
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.RPMCommand
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.pressure.BarometricPressureCommand
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.protocol.TroubleCodesCommand
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.temperature.AmbientAirTemperatureCommand
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.temperature.EngineCoolantTemperatureCommand
import com.google.kpierudzki.driverassistant.obd.service.provider.scheduler.ScheduleItem
import java.util.*

/**
 * Created by Kamil on 21.12.2017.
 */
class CommandsProvider {

    companion object {

        fun getSupportedCommands(): List<ScheduleItem> {
            return object : ArrayList<ScheduleItem>() {
                init {
                    add(ScheduleItem(2, SpeedCommand()))
                    add(ScheduleItem(1, RPMCommand()))
                    add(ScheduleItem(0, MassAirFlowCommand()))
                    add(ScheduleItem(0, EngineCoolantTemperatureCommand()))
                    add(ScheduleItem(0, LoadCommand()))
                    add(ScheduleItem(0, BarometricPressureCommand()))
                    add(ScheduleItem(0, OilTempCommand()))
                    add(ScheduleItem(0, AmbientAirTemperatureCommand()))
                    add(ScheduleItem(0, TroubleCodesCommand()))
                }
            }
        }

    }
}
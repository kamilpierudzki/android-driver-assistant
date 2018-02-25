package com.google.kpierudzki.driverassistant;

import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.SpeedCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.LoadCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.MassAirFlowCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.OilTempCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.engine.RPMCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.pressure.BarometricPressureCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.temperature.AmbientAirTemperatureCommand;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.temperature.EngineCoolantTemperatureCommand;
import com.google.kpierudzki.driverassistant.obd.service.provider.scheduler.ObdCommandScheduler;
import com.google.kpierudzki.driverassistant.obd.service.provider.scheduler.ScheduleItem;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 05.10.2017.
 */

public class ObdSchedulerTest {

    @Test
    public void test1() {
        ObdCommandScheduler scheduler = new ObdCommandScheduler(new ArrayList<ScheduleItem>() {{
            add(new ScheduleItem(2, new SpeedCommand()));
            add(new ScheduleItem(1, new RPMCommand()));
            add(new ScheduleItem(0, new MassAirFlowCommand()));
            add(new ScheduleItem(0, new EngineCoolantTemperatureCommand()));
            add(new ScheduleItem(0, new LoadCommand()));
            add(new ScheduleItem(0, new BarometricPressureCommand()));
            add(new ScheduleItem(0, new OilTempCommand()));
            add(new ScheduleItem(0, new AmbientAirTemperatureCommand()));
        }});


        //Iteration #1
        scheduler.prepareCommands();
        verifyIteration(scheduler.getScheduledCommands(), new ArrayList<ObdCommandModel>() {{
            add(new SpeedCommand());
            add(new RPMCommand());
            add(new MassAirFlowCommand());
            add(new SpeedCommand());
            add(new EngineCoolantTemperatureCommand());
            add(new LoadCommand());
        }});

        //Iteration #2
        scheduler.prepareCommands();
        verifyIteration(scheduler.getScheduledCommands(), new ArrayList<ObdCommandModel>() {{
            add(new SpeedCommand());
            add(new RPMCommand());
            add(new BarometricPressureCommand());
            add(new SpeedCommand());
            add(new OilTempCommand());
            add(new AmbientAirTemperatureCommand());
        }});

        //Iteration #3
        scheduler.prepareCommands();
        verifyIteration(scheduler.getScheduledCommands(), new ArrayList<ObdCommandModel>() {{
            add(new SpeedCommand());
            add(new RPMCommand());
            add(new MassAirFlowCommand());
            add(new SpeedCommand());
            add(new EngineCoolantTemperatureCommand());
            add(new LoadCommand());
        }});

        //Iteration #4
        scheduler.prepareCommands();
        verifyIteration(scheduler.getScheduledCommands(), new ArrayList<ObdCommandModel>() {{
            add(new SpeedCommand());
            add(new RPMCommand());
            add(new BarometricPressureCommand());
            add(new SpeedCommand());
            add(new OilTempCommand());
            add(new AmbientAirTemperatureCommand());
        }});
    }

    private void verifyIteration(List<ObdCommandModel> resultOfScheduler, List<ObdCommandModel> toVerify) {
        Assert.assertTrue(resultOfScheduler.size() == toVerify.size());

        for (int i = 0; i < resultOfScheduler.size(); i++)
            Assert.assertTrue(resultOfScheduler.get(i).paramType == toVerify.get(i).paramType);
    }

    private void printList(List<ObdCommandModel> list) {
        for (ObdCommandModel obdCommandModel : list)
            System.out.println(obdCommandModel.toString());
    }
}

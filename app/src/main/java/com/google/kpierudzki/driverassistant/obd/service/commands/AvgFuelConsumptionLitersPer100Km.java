package com.google.kpierudzki.driverassistant.obd.service.commands;

import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by Kamil on 15.09.2017.
 */
public class AvgFuelConsumptionLitersPer100Km extends ObdCommand {

    public AvgFuelConsumptionLitersPer100Km() {
        super("");
    }

    @Override
    protected void performCalculations() {

    }

    @Override
    public String getFormattedResult() {
        return null;
    }

    @Override
    public String getCalculatedResult() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {

    }

    private boolean checkPossibility(final int speed, final int RPM) {
        final float value = (speed * 100)/RPM;
        if (value >= 1.0f)
            return true;
        return false;
    }
}

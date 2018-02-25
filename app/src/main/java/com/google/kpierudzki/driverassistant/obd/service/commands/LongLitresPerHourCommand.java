package com.google.kpierudzki.driverassistant.obd.service.commands;

import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by Kamil on 15.09.2017.
 */
public class LongLitresPerHourCommand extends ObdCommand {

    private LitresPerHourCommand litresPerHour;
    private double longLitresPerHour;

    public LongLitresPerHourCommand(LitresPerHourCommand litresPerHour) {
        super("");
        this.litresPerHour = litresPerHour;
    }

    @Override
    protected void performCalculations() {
        /** Not implemented*/
    }

    @Override
    public String getFormattedResult() {
        return getCalculatedResult() + " l/h(avg.)";
    }

    @Override
    public String getCalculatedResult() {
        return String.format(new Locale("en"), "%.2f", longLitresPerHour);
    }

    @Override
    public String getName() {
        return "Long litres per hour";
    }

    @Override
    public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {
        longLitresPerHour = (longLitresPerHour + litresPerHour.getLitresPerHour())/2;
    }
}

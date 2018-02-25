package com.google.kpierudzki.driverassistant.obd.service.commands;

import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by Kamil on 15.09.2017.
 */
public class LongLitresPer100KmCommand extends ObdCommand {

    private LitresPer100KmCommand currentFuelConsumption;
    private double longFuelConsumption;

    public LongLitresPer100KmCommand(LitresPer100KmCommand currentFuelConsumption) {
        super("");
        this.currentFuelConsumption = currentFuelConsumption;
    }

    @Override
    protected void performCalculations() {
        /** Not implemented*/
    }

    @Override
    public String getFormattedResult() {
        return getCalculatedResult() + " l/100km(avg.)";
    }

    @Override
    public String getCalculatedResult() {
        return String.format(new Locale("en"), "%.2f", longFuelConsumption);
    }

    @Override
    public String getName() {
        return "Long litres per 100km";
    }

    @Override
    public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {
        longFuelConsumption = (longFuelConsumption + currentFuelConsumption.getLitresPer100Km())/2;
    }

    public double getLongFuelConsumption() {
        return longFuelConsumption;
    }
}

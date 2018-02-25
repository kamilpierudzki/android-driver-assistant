package com.google.kpierudzki.driverassistant.obd.service.commands;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by Kamil on 15.09.2017.
 */
public class LitresPerHourCommand extends ObdCommand {

    private MassAirFlowCommand maf;
    private double litresPerHour;

    public LitresPerHourCommand(MassAirFlowCommand maf) {
        super("");
        this.maf = maf;
    }

    @Override
    protected void performCalculations() {
        /** Not implemented*/
    }

    @Override
    public String getFormattedResult() {
        return getCalculatedResult() + " l/h";
    }

    @Override
    public String getCalculatedResult() {
        return String.format(new Locale("en"), "%.2f", litresPerHour);
    }

    @Override
    public String getName() {
        return "Litres per hour";
    }

    @Override
    public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {
        litresPerHour = (maf.getMAF() * 3.785f)/12.5f;//wartości niewłaściwe
    }

    public double getLitresPerHour() {
        return litresPerHour;
    }
}

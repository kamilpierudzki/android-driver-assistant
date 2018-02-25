package com.google.kpierudzki.driverassistant.obd.service.commands;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.exceptions.NoDataException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by Kamil on 15.09.2017.
 */
public class LitresPer100KmCommand extends ObdCommand {

    private MassAirFlowCommand maf;
    private SpeedCommand speed;
    private LoadCommand load;
    private double litresPer100Km;

    public LitresPer100KmCommand(MassAirFlowCommand maf, SpeedCommand speed, LoadCommand load) {
        super("");
        this.maf = maf;
        this.speed = speed;
        this.load = load;
    }

    @Override
    protected void performCalculations() {
        /** Not implemented*/
    }

    @Override
    public String getFormattedResult() {
        return getCalculatedResult() + " l/100km";
    }

    @Override
    public String getCalculatedResult() {
        return String.format(new Locale("en"), "%.2f", litresPer100Km);
    }

    @Override
    public String getName() {
        return "Litres per 100km";
    }

    @Override
    public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {
        /**
         * diesel - 0.246875,
         * gasoline - 2.673722,
         * */
        if (speed.getMetricSpeed() > 0)
            litresPer100Km = (maf.getMAF() * load.getPercentage() * 2.673722)/speed.getMetricSpeed();
        else {
            litresPer100Km = 0;
            throw new NoDataException();
        }
    }

    public double getLitresPer100Km() {
        return litresPer100Km;
    }
}

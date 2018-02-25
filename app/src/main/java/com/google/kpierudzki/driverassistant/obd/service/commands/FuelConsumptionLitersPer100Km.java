package com.google.kpierudzki.driverassistant.obd.service.commands;

import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Kamil on 15.09.2017.
 */
public class FuelConsumptionLitersPer100Km extends ObdCommand {

    public static final String COMMAND_NAME = "Fuel Consum. l/100Km(inst.)";

    private final float iloscPowietrzaNa1gBenzyny = 14.7f;
    private final float iloscPowietrzaNa1gRopy = 14.6f;
//    private final float iloscPowietrzaNa1gRopy = 20.1f;

    final float wagaGolonuBenzyny_lbs = 6.2f;
    final float wagaGolonuRopy_lbs = 7.1f;

    private float spalanieNa100Km = -1.0f;

    public FuelConsumptionLitersPer100Km() {
        super("09 0A");//fake'owa komenda
    }

    @Override
    protected void performCalculations() {
        //...
    }

    @Override
    public String getFormattedResult() {
        return String.format("%.2f%s", spalanieNa100Km, "l/100km");
    }

    @Override
    public String getCalculatedResult() {
        return String.format("%.2f%s", spalanieNa100Km, "l/100km");
    }

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {
//        String speedResult = ObdDataHolder.speed;
//        speedResult = speedResult.substring(0, speedResult.length() - 4);
//        int predkosc = Integer.valueOf(speedResult);
//
//        String lambdaResult = ObdDataHolder.lambda;
//        float lambda = Float.valueOf(lambdaResult);
//
//        float wspolczynnikPowietrza = AirFuelRatio.getfuelAFR(lambda, ObdDataHolder.fuelType);
//
//        float wspolczynnikWagi = -1.0f;
//        if (ObdDataHolder.fuelType == FuelType.GASOLINE)
//            wspolczynnikWagi = wagaGolonuBenzyny_lbs;
//        if (ObdDataHolder.fuelType == FuelType.DIESEL)
//            wspolczynnikWagi = wagaGolonuRopy_lbs;
//
//        float masaPowietrza = -1.0f;//[g/s]
//        String mafResult = ObdDataHolder.engineMassAirFlow;
//        mafResult = mafResult.substring(0, mafResult.length() - 3);
//        mafResult = mafResult.replace(',', '.');
//        masaPowietrza = Float.valueOf(mafResult);
//
//        final float MPG = (wspolczynnikPowietrza * wspolczynnikWagi * 454 * predkosc * 0.621371f) / (3600.0f * masaPowietrza);//[mile/gallon]
//        if (MPG > 0)
//            spalanieNa100Km = 235.21517f / MPG;//[l/100km]
//        else
//            spalanieNa100Km = 0;
    }
}

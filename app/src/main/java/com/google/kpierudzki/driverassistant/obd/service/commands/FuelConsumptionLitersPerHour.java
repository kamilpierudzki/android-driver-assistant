package com.google.kpierudzki.driverassistant.obd.service.commands;

import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Kamil on 15.09.2017.
 */
public class FuelConsumptionLitersPerHour extends ObdCommand {

    public static final String NAME = "Fuel Consum l/h(inst.)";
//    private final float wspolczynnikPaliwowoPowietrznyDlaBenzyny = 14.7f;
//    private final float wspolczynnikPaliwowoPowietrznyDlaRopy = 14.6f;
//    private final float wspolczynnikPaliwowoPowietrznyDlaPropanu = 15.5f;

    private final float wagaJednegoLitraBenzyny = 0.7f;//[kg]
    private final float wagaJednegoLitraRopy = 0.9f;//[kg]
    private final float wagaJednegoLitraGazu = 0.5f;//[kg]

    private float LPH = -1.0f;

    public FuelConsumptionLitersPerHour() {
        super("");
    }

    @Override
    protected void performCalculations() {
        //...
    }

    @Override
    public String getFormattedResult() {
        return String.format("%.2f%s", LPH, "l/h");
    }

    @Override
    public String getCalculatedResult() {
        return String.format("%.2f%s", LPH, "l/h");
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {
//        float masaPowietrza = -1.0f;
//        String mafResult = ObdDataHolder.engineMassAirFlow;
//        mafResult = mafResult.substring(0, mafResult.length() - 3);
//        mafResult = mafResult.replace(',', '.');
//        masaPowietrza = Float.valueOf(mafResult);
//
//        String lambdaResult = ObdDataHolder.lambda;
//        float lambda = Float.valueOf(lambdaResult);
//
//        float wspolczynnik = AirFuelRatio.getfuelAFR(lambda, ObdDataHolder.fuelType);
//
//        float masaPowietrza_kg_min = masaPowietrza * 0.06f;//[kg/min]
//        final float iloscPaliwaWPowietrzu = (masaPowietrza_kg_min) * (1 / wspolczynnik);//[kg/min]
//
//        if (ObdDataHolder.fuelType == FuelType.GASOLINE)
//            wspolczynnik = wagaJednegoLitraBenzyny;
//        if (ObdDataHolder.fuelType == FuelType.DIESEL)
//            wspolczynnik = wagaJednegoLitraRopy;
//        if (ObdDataHolder.fuelType == FuelType.LPG)
//            wspolczynnik = wagaJednegoLitraGazu;
//
//        final float konsumpcjaPaliwa = iloscPaliwaWPowietrzu / wspolczynnik;//[l/min]
//        LPH = konsumpcjaPaliwa * 60;//[l/h]
    }

    public float getLPH() {
        return LPH;
    }
}

package com.google.kpierudzki.driverassistant.obd.service.commands;

import android.support.annotation.Nullable;

import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Kamil on 15.09.2017.
 */
public class CalculatedMAFObd extends ObdCommand {

    public static final String NAME = "Calculated MAF";
    //stałe
    private float molekularnaMasaPowietrza = 28.97f; //[g/mol]
    private float R = 8.314f;//[J/*K/mol]

    private float MAF_kg_min = -1.0f;
    private float MAF_g_s = -1.0f;

    public CalculatedMAFObd() {
        super("");
    }

    @Override
    protected void performCalculations() {}

    @Override @Nullable
    public String getFormattedResult() {
        return String.format("%.2f%s", MAF_g_s, "g/s");
    }

    @Override
    public String getCalculatedResult() {
        return String.format("%.2f%s", MAF_g_s, "g/s");
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {

//        final float pojemnoscSilnika_l = 1.6f;
//        final float pojemnoscSilnika_m3 = pojemnoscSilnika_l * 0.001f;//[m3]
//
////        final AirIntakeTemperatureObdCommand airIntakeTemperatureObdCommand = new AirIntakeTemperatureObdCommand();
////        airIntakeTemperatureObdCommand.run(in, out);
////        String temperaturaResult = airIntakeTemperatureObdCommand.getFormattedResult();
////
////        int temperaturaWCelcjuszach = Integer.valueOf(temperaturaResult.substring(0, temperaturaResult.length() - 1));//ucięcie ostatniego znaku
//
//        String temperaturaResult = "123K";//todo usunac hardkody
//        int temperaturaWCelcjuszach = Integer.valueOf(temperaturaResult.substring(0, temperaturaResult.length() - 1));//ucięcie ostatniego znaku
//        final float temperaturaWKelvinach = temperaturaWCelcjuszach + 273.15f;//[K]
//
////        final IntakeManifoldPressureObdCommand intakeManifoldPressureObdCommand = new IntakeManifoldPressureObdCommand();
////        float cisnieniePowietrza = -1.0f;
////        try {
////            intakeManifoldPressureObdCommand.run(in, out);
////            String cisnienieResult = intakeManifoldPressureObdCommand.getFormattedResult();
////            cisnieniePowietrza = Float.valueOf(cisnienieResult.substring(0, cisnienieResult.length() - 3));
////        } catch (Exception ex) {
////            //Jeśli nieodczyta ciśnienia
////            Log.d("[E] CalculatedMAFObd - IntakeManifoldPressureObdCommand");
////            cisnieniePowietrza = 101;//[kPa]
////        }
//
//        float cisnieniePowietrza = -1.0f;
//        if (ObdDataHolder.pressureIntakeManifold.length() < 1) {
//            //to znaczy, że rezultat to '#'
//            cisnieniePowietrza = -1.0f;//[kPa]
//        } else {
//            String cisnienieResult = ObdDataHolder.pressureIntakeManifold;
//            cisnieniePowietrza = Float.valueOf(cisnienieResult.substring(0, cisnienieResult.length() - 3));
//        }
//
//        final float gestoscPowietrza = molekularnaMasaPowietrza / (R * temperaturaWKelvinach / cisnieniePowietrza);//[kg/m3]
//
////        final EngineRPMObdCommand engineRPMObdCommand = new EngineRPMObdCommand();
////        engineRPMObdCommand.run(in, out);
////        String rpmResult = engineRPMObdCommand.getFormattedResult();
////        int RPMs = Integer.valueOf(rpmResult.substring(0, rpmResult.length() - 4));//rpm
//
//        String rpmResult = ObdDataHolder.engineRpm;
//        int RPMs = Integer.valueOf(rpmResult.substring(0, rpmResult.length()-4));
//
//        final float iloscCykliSilnika = RPMs * 0.5f;//[cykle/min]
//
////        final EngineLoadObdCommand engineLoadObdCommand = new EngineLoadObdCommand();
////        engineLoadObdCommand.run(in, out);
////        String obciazenieResult = engineLoadObdCommand.getFormattedResult();
//
//        String obciazenieResult = ObdDataHolder.engineLoad;
//        obciazenieResult = obciazenieResult.replace(',', '.');//zamiana przecinka [,] na kropkę [.]
//        final float obciazenieSilnika = Float.valueOf(obciazenieResult.substring(0, obciazenieResult.length()-1));
//
//        final float sprawnoscSilnika = ObdDataHolder.ENGINE_VOLUMETRIC_EFFICIENTY * (obciazenieSilnika+100)/100;
//
//        MAF_kg_min = (pojemnoscSilnika_m3 * iloscCykliSilnika * sprawnoscSilnika / 100 * gestoscPowietrza);//[kg/min]
//        MAF_g_s = MAF_kg_min / 0.06f;//[kg/min] -> [g/s]
    }

    public float getMAF_kg_min() {
        return MAF_kg_min;
    }

    public float getMAF_g_s() {
        return MAF_g_s;
    }
}

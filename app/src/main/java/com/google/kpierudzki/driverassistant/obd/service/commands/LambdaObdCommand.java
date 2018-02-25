package com.google.kpierudzki.driverassistant.obd.service.commands;


import com.github.pires.obd.commands.ObdCommand;

/**
 * Created by Kamil on 15.09.2017.
 */
public class LambdaObdCommand extends ObdCommand {

    public static String NAME = "Lambda";
    private static String[] types = {
            "24", "25", "26", "28", "29", "2A", "2B",
            "34", "35", "36", "37", "38", "39", "3A", "3B"
    };
    private float lambda = -1.0f;
    private float lambdaV = -1.0f;

    public LambdaObdCommand(int type) {
        super("01 " + types[type]);
    }

    public LambdaObdCommand(String paramString) {
        super(paramString);
    }

    public float getLambda() {
        return this.lambda;
    }

    public float getLambdaV() {
        return this.lambdaV;
    }

    @Override
    protected void performCalculations() {
        int A = ((Integer) this.buffer.get(0)).intValue();
        int B = ((Integer) this.buffer.get(1)).intValue();
        if (buffer.size() == 4) {
            int C = ((Integer) this.buffer.get(2)).intValue();
            int D = ((Integer) this.buffer.get(3)).intValue();
            lambdaV = ((C * 256) + D) * 8.0f / 65535.0f;
        }
        lambda = ((A * 256 ) + B) * 2.0f / 65535.0f;
    }

    @Override
    public String getFormattedResult() {
        return String.format("%.2f%s", new Object[]{Float.valueOf(lambda), ""});
    }

    @Override
    public String getCalculatedResult() {
        return String.format("%.2f%s", new Object[]{Float.valueOf(lambda), ""});
    }

    @Override
    public String getName() {
        return NAME;
    }
}

package com.google.kpierudzki.driverassistant.obd.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.google.kpierudzki.driverassistant.R;

/**
 * Created by Kamil on 27.09.2017.
 */

public class DeviceMalfunctionDialog extends DialogFragment {

    public final static String TAG = "DeviceMalfunctionDialog_TAG";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.Obd_Malfunction_Dialog_Message)
                .setNeutralButton(R.string.Obd_Malfunction_Dialog_Button, (dialogInterface, i) ->
                        dialogInterface.dismiss());
        return builder.create();
    }
}

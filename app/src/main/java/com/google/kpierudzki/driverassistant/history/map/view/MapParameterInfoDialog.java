package com.google.kpierudzki.driverassistant.history.map.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;

/**
 * Created by Kamil on 02.12.2017.
 */

public class MapParameterInfoDialog extends DialogFragment {

    public final static String KEY_PARAM_TYPE = "KEY_PARAM_TYPE";
    public final static String TAG = "MapParameterInfoDialog_TAG";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        ObdParamType currentType;
        if (arguments != null) {
            currentType = (ObdParamType) arguments.getSerializable(KEY_PARAM_TYPE);
        } else {
            currentType = ObdParamType.UNKNOWN;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(currentType.name())
                .setNeutralButton(R.string.Obd_Malfunction_Dialog_Button, (dialogInterface, i) ->
                        dialogInterface.dismiss());
        return builder.create();
    }

    public static MapParameterInfoDialog newInstance(ObdParamType paramType) {
        MapParameterInfoDialog dialog = new MapParameterInfoDialog();
        Bundle args = new Bundle();
        args.putSerializable(KEY_PARAM_TYPE, paramType);
        dialog.setArguments(args);
        return dialog;
    }
}

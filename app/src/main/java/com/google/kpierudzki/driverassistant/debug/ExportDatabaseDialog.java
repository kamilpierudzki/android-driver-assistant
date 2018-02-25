package com.google.kpierudzki.driverassistant.debug;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.validatedtextfield.ValidatedEditText;
import com.google.kpierudzki.validatedtextfield.checker.MinLengthChecker;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 29.06.2017.
 */

public class ExportDatabaseDialog extends DialogFragment implements TextWatcher {

    public static final String USED_TAG = "ExportDatabaseDialog_USED_TAG";
    private Callback callback;

    @BindView(R.id.file_name_field_layout)
    ValidatedEditText editText;

    @BindView(R.id.positive_button)
    Button positiveButton;

    @BindView(R.id.negative_button)
    Button negativeButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View root = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_fragment_debig_samples_reading, null);
        ButterKnife.bind(this, root);

        editText.addChecker(new MinLengthChecker(3));
        editText.getEditText().addTextChangedListener(this);

        positiveButton.setOnClickListener(v -> {
            if (editText.check()) {
                callback.onDialogPositiveClicked(editText.getEditText().getText().toString());
                dismiss();
            }
        });

        negativeButton.setOnClickListener(v -> dismiss());

        return new AlertDialog.Builder(getActivity()).setView(root).create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (Callback) getActivity().getSupportFragmentManager().findFragmentByTag(getArguments().getString(USED_TAG));
        } catch (Exception e) {
            //Ignore
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (Callback) getActivity().getSupportFragmentManager().findFragmentByTag(getArguments().getString(USED_TAG));
        } catch (Exception e) {
            //Ignore
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Ignore
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        editText.check();
    }

    @Override
    public void afterTextChanged(Editable s) {
        //Ignore
    }

    public interface Callback {
        void onDialogPositiveClicked(String filename);
    }
}

package com.google.kpierudzki.validatedtextfield.checker;

import android.support.design.widget.TextInputLayout;

import com.google.kpierudzki.validatedtextfield.ICheckable;
import com.google.kpierudzki.validatedtextfield.R;

/**
 * This checker checks that given string contains only numbers.<br /><br />
 * Created by Kamil on 2016-11-04.
 */

public class OnlyNumberChecker implements ICheckable {

    private final String regex = "\\d+";

    @Override
    public boolean check(TextInputLayout inputField) {
        boolean result = inputField.getEditText().getText().toString().matches(regex);
        if (!result)
            inputField.setError(inputField.getResources().getString(R.string.error_only_numbers));
        else
            inputField.setError(null);
        return result;
    }
}

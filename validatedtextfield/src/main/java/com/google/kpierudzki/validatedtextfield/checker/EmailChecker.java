package com.google.kpierudzki.validatedtextfield.checker;

import android.support.design.widget.TextInputLayout;

import com.google.kpierudzki.validatedtextfield.ICheckable;
import com.google.kpierudzki.validatedtextfield.R;

/**
 * This checker checks that given email address is valid.<br /><br />
 * Created by Kamil on 03.01.2017.
 */

public class EmailChecker implements ICheckable {

    private final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public boolean check(TextInputLayout inputField) {
        boolean result = inputField.getEditText().getText().toString().matches(EMAIL_PATTERN);
        if (!result)
            inputField.setError(inputField.getResources().getString(R.string.error_wrong_email));
        else
            inputField.setError(null);
        return result;
    }
}

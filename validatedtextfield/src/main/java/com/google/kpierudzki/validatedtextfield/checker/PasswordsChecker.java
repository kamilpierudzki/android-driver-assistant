package com.google.kpierudzki.validatedtextfield.checker;

import android.support.design.widget.TextInputLayout;

import com.google.kpierudzki.validatedtextfield.ICheckable;
import com.google.kpierudzki.validatedtextfield.R;

/**
 * This checker checks that passwords are identical.<br /><br />
 * Created by Kamil on 2016-11-05.
 */

public class PasswordsChecker implements ICheckable {

    private TextInputLayout fieldToCompare;

    public PasswordsChecker(TextInputLayout fieldToCompare) {
        this.fieldToCompare = fieldToCompare;
    }

    @Override
    public boolean check(TextInputLayout inputField) {
        boolean result = inputField.getEditText().getText().toString().equals(fieldToCompare.getEditText().getText().toString());
        if (!result) {
            inputField.setError(inputField.getResources().getString(R.string.error_passwords_are_different));
            if (fieldToCompare.getError() == null)
                fieldToCompare.setError(inputField.getResources().getString(R.string.error_passwords_are_different));
        } else
            inputField.setError(null);
        return result;
    }
}

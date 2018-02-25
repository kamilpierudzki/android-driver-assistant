package com.google.kpierudzki.validatedtextfield.checker;

import android.support.design.widget.TextInputLayout;

import com.google.kpierudzki.validatedtextfield.ICheckable;
import com.google.kpierudzki.validatedtextfield.R;

/**
 * This checker checks that input field is empty.<br /><br />
 * Created by Kamil on 2016-11-05.
 */

public class NotEmptyChecker implements ICheckable {

    @Override
    public boolean check(TextInputLayout inputField) {
        boolean result = inputField.getEditText().getText().toString().length() > 0;
        if (!result)
            inputField.setError(inputField.getResources().getString(R.string.error_empty));
        else
            inputField.setError(null);
        return result;
    }
}

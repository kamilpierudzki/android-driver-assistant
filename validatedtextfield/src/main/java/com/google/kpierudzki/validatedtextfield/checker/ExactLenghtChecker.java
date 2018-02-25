package com.google.kpierudzki.validatedtextfield.checker;

import android.support.design.widget.TextInputLayout;

import com.google.kpierudzki.validatedtextfield.ICheckable;
import com.google.kpierudzki.validatedtextfield.R;

/**
 * This checker checks that given input has exacts length.<br /><br />
 * Created by Kamil on 2016-11-04.
 */

public class ExactLenghtChecker implements ICheckable {

    private int length;

    public ExactLenghtChecker(int length) {
        this.length = length;
    }

    @Override
    public boolean check(TextInputLayout inputField) {
        boolean result = inputField.getEditText().getText().toString().length() == length;
        if (!result)
            inputField.setError(inputField.getResources().getString(R.string.error_no_exact, length));
        else
            inputField.setError(null);
        return result;
    }
}

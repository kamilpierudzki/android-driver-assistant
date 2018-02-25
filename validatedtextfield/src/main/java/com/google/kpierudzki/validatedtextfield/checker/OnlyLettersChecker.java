package com.google.kpierudzki.validatedtextfield.checker;

import android.support.design.widget.TextInputLayout;

import com.google.kpierudzki.validatedtextfield.ICheckable;
import com.google.kpierudzki.validatedtextfield.R;

/**
 * Created by Kamil on 29.06.2017.
 */

public class OnlyLettersChecker implements ICheckable {

    private final String regex = "\\p{L}+";

    @Override
    public boolean check(TextInputLayout inputField) {
        boolean result = inputField.getEditText().getText().toString().matches(regex);
        if (!result)
            inputField.setError(inputField.getResources()
                    .getString(R.string.error_only_letters));
        else
            inputField.setError(null);
        return result;
    }
}

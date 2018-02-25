package com.google.kpierudzki.validatedtextfield.checker;

import android.support.design.widget.TextInputLayout;

import com.google.kpierudzki.validatedtextfield.ICheckable;
import com.google.kpierudzki.validatedtextfield.R;

/**
 * Created by Kamil on 04.01.2017.
 */

public class MaxLengthChecker implements ICheckable {

    private int maxLength;

    public MaxLengthChecker(int maxLenght) {
        this.maxLength = maxLenght;
    }

    @Override
    public boolean check(TextInputLayout inputField) {
        boolean result = inputField.getEditText().getText().length() <= maxLength;
        if (!result)
            inputField.setError(inputField.getResources().getString(R.string.error_too_long, maxLength));
        else
            inputField.setError(null);
        return result;
    }
}

package com.google.kpierudzki.validatedtextfield.checker;

import android.support.design.widget.TextInputLayout;

import com.google.kpierudzki.validatedtextfield.ICheckable;
import com.google.kpierudzki.validatedtextfield.R;

/**
 * This checker checks minimal length of input.<br /><br />
 * Created by Kamil on 2016-11-04.
 */

public class MinLengthChecker implements ICheckable {

    private int minLenght;

    public MinLengthChecker(int minLenght) {
        this.minLenght = minLenght;
    }

    @Override
    public boolean check(TextInputLayout inputField) {
        boolean result = inputField.getEditText().getText().length() >= minLenght;
        if (!result)
            inputField.setError(inputField.getResources().getString(R.string.error_too_short, minLenght));
        else
            inputField.setError(null);
        return result;
    }
}

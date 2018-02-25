package com.google.kpierudzki.validatedtextfield.checker;

import android.support.design.widget.TextInputLayout;

import com.google.kpierudzki.validatedtextfield.ICheckable;
import com.google.kpierudzki.validatedtextfield.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This checker checks that date is not grater than today.<br /><br />
 * Created by Kamil on 2016-11-05.
 */

public class DateChecker implements ICheckable {

    private SimpleDateFormat simpleDateFormat;

    public DateChecker(SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    @Override
    public boolean check(TextInputLayout inputField) {
        boolean result;
        try {
            result = !(simpleDateFormat.parse(inputField.getEditText().getText().toString()).compareTo(new Date()) > 0);
        } catch (ParseException e) {
            result = false;
        }
        if (!result)
            inputField.setError(inputField.getResources().getString(R.string.error_back_to_the_future));
        else
            inputField.setError(null);
        return result;
    }
}

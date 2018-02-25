package com.google.kpierudzki.validatedtextfield;

import android.support.design.widget.TextInputLayout;

/**
 * Interface for checkable objects.<br /><br />
 * Created by Kamil on 29.06.2017.
 */

public interface ICheckable {
    boolean check(TextInputLayout inputField);
}

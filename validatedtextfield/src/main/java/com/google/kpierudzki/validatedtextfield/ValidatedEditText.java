package com.google.kpierudzki.validatedtextfield;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 29.06.2017.
 */

public class ValidatedEditText extends TextInputLayout implements View.OnFocusChangeListener {

    private List<ICheckable> checkers;

    public ValidatedEditText(Context context) {
        super(context);
        init(context);
    }

    public ValidatedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ValidatedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        checkers = new ArrayList<>();
    }

    /**
     * @param checker One of the defined checkers.
     */
    public void addChecker(ICheckable checker) {
        checkers.add(checker);
    }

    /**
     * Checks content of this widget using given checkers.
     *
     * @return TRUE - Content is ok.
     */
    public boolean check() {
        boolean result = true;
        for (ICheckable checker : checkers) {
            result = checker.check(this);
            if (!result) {
                break;
            }
        }
        return result;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus)
            ((ValidatedEditText) v.getParentForAccessibility()).check();
    }
}

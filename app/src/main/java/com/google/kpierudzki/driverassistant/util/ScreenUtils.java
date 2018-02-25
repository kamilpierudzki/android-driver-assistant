package com.google.kpierudzki.driverassistant.util;

import android.content.res.Configuration;
import android.content.res.Resources;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;

/**
 * Created by Kamil on 06.07.2017.
 */

public class ScreenUtils {

    public static int dpToPx(final int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(final int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static float dpToPxF(final int dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    public static float pxToDpF(final int px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    public static boolean isTablet() {
        return (App.getAppContext().getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isPhone() {
        return !isTablet();
    }

    public static boolean isLandscape() {
        return App.getAppContext().getResources().getBoolean(R.bool.landscape);
    }

    public static boolean isPortrait() {
        return !isLandscape();
    }
}

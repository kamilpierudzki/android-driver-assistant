package com.google.kpierudzki.driverassistant.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Kamil on 26.07.2017.
 */

public class TimeUtils {

    public static String formatDateForHistoryList(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static String formatDateForMapToolbarParameter(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}

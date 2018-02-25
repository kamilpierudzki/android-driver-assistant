package com.google.kpierudzki.driverassistant.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;

import com.google.kpierudzki.driverassistant.R;

/**
 * Created by Kamil on 31.07.2017.
 */

public class AppBarUtil {

    @Nullable
    public static AppBarLayout getAppBarView(@Nullable Activity activity) {
        return activity != null ? activity.findViewById(R.id.appbar) : null;
    }

    @Nullable
    public static Toolbar getAppBarToolbar(@NonNull Activity activity) {
        return getAppBarView(activity).findViewById(R.id.toolbar);
    }
}

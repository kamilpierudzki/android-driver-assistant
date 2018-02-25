package com.google.kpierudzki.driverassistant.util.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.kpierudzki.driverassistant.App;

/**
 * Created by Kamil on 28.06.2017.
 */

public class Pref {

    private static final String DEFAULT_NAME = "some_awesome_name";

    public static void setString(PrefItem prefItem, String value, boolean sync) {
        if (prefItem.getClassOfItem() != String.class)
            throw new RuntimeException("Incorrect PrefItem class");
        SharedPreferences sharedPreferences = App.getAppContext().getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(prefItem.getId(), value);
        if (sync)
            editor.commit();
        else
            editor.apply();
    }

    public static String getString(PrefItem prefItem, String defaultValue) {
        if (prefItem.getClassOfItem() != String.class)
            throw new RuntimeException("Incorrect PrefItem class");
        SharedPreferences sharedPreferences = App.getAppContext().getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(prefItem.getId(), defaultValue);
    }

    public static void setBoolean(PrefItem prefItem, boolean value, boolean sync) {
        if (prefItem.getClassOfItem() != Boolean.class)
            throw new RuntimeException("Incorrect PrefItem class");
        SharedPreferences sharedPreferences = App.getAppContext().getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(prefItem.getId(), value);
        if (sync)
            editor.commit();
        else
            editor.apply();
    }

    public static boolean getBoolean(PrefItem prefItem, boolean defaultValue) {
        if (prefItem.getClassOfItem() != Boolean.class)
            throw new RuntimeException("Incorrect PrefItem class");
        SharedPreferences sharedPreferences = App.getAppContext().getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(prefItem.getId(), defaultValue);
    }

    public static void setInt(PrefItem prefItem, int value, boolean sync) {
        if (prefItem.getClassOfItem() != Integer.class)
            throw new RuntimeException("Incorrect PrefItem class");
        SharedPreferences sharedPreferences = App.getAppContext().getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(prefItem.getId(), value);
        if (sync)
            editor.commit();
        else
            editor.apply();
    }

    public static int getInt(PrefItem prefItem, int defaultValue) {
        if (prefItem.getClassOfItem() != Integer.class)
            throw new RuntimeException("Incorrect PrefItem class");
        SharedPreferences sharedPreferences = App.getAppContext().getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(prefItem.getId(), defaultValue);
    }
}

package com.google.kpierudzki.driverassistant;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Kamil on 18.02.2018.
 */

public interface IMenuCreate {
    boolean onMenuCreated(MenuInflater menuInflater, Menu menu);
    boolean onOptionSelected(MenuItem item);
    void onNavigationBack();
}

package com.google.kpierudzki.driverassistant;

/**
 * Created by Kamil on 02.09.2017.
 */

public interface MainActivityFragmentsCallbacks {
    void onFragmentLoaded(LoadedFragment loadedFragment);
    int getMainFrameId();
    void setToolbarNavigationEnabled(boolean enabled);

    enum LoadedFragment {
        EcoDriving,
        ObdII,
        History,
        Dtc
    }
}

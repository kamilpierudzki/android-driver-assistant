package com.google.kpierudzki.driverassistant.obd.read;

import com.google.kpierudzki.driverassistant.BaseView;
import com.google.kpierudzki.driverassistant.common.BasePresenter;
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadListener;

/**
 * Created by Kamil on 16.09.2017.
 */

public interface ObdReadContract {

    interface View extends BaseView<Presenter>, IObdReadListener {
        //...
    }

    interface Presenter extends BasePresenter, IObdReadListener {
        void fetchDtcInfo();
    }
}

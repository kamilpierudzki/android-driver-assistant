package com.google.kpierudzki.driverassistant;

import com.google.kpierudzki.driverassistant.common.BasePresenter;

/**
 * Created by Kamil on 29.06.2017.
 */

public interface BaseView<Presenter extends BasePresenter> {
    void setPresenter(Presenter presenter);
    void onPresenterReady(Presenter presenter);
}

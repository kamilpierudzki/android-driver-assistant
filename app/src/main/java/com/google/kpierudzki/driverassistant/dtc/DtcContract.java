package com.google.kpierudzki.driverassistant.dtc;

import com.google.kpierudzki.driverassistant.BaseView;
import com.google.kpierudzki.driverassistant.common.BasePresenter;
import com.google.kpierudzki.driverassistant.dtc.database.DtcEntity;

import java.util.List;

/**
 * Created by Kamil on 25.12.2017.
 */

public interface DtcContract {

    interface View extends BaseView<Presenter> {
        void fillList(List<DtcEntity> data);
        void showClearDtcIcon(boolean show);
    }

    interface Presenter extends BasePresenter {
        void fetchData();
        void clearAllDtc();
    }
}

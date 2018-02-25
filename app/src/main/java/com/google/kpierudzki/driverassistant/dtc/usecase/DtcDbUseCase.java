package com.google.kpierudzki.driverassistant.dtc.usecase;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.dtc.DtcContract;
import com.google.kpierudzki.driverassistant.dtc.database.DtcEntity;
import com.google.kpierudzki.driverassistant.service.helper.BackgroundThreadPoolHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 25.12.2017.
 */

public class DtcDbUseCase extends BackgroundThreadPoolHelper implements DtcContract.Presenter {

    private Callbacks callbacks;

    public DtcDbUseCase(Callbacks callbacks) {
        super();
        this.callbacks = callbacks;
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void fetchData() {
        if (threadPool != null && !threadPool.isTerminating()) {
            threadPool.execute(() -> {
                if (callbacks != null)
                    callbacks.onDataFetched(App.getDatabase().getDtcDao().getEntities());
            });
        }
    }

    @Override
    public void clearAllDtc() {
        if (threadPool != null && !threadPool.isTerminating()) {
            threadPool.execute(() -> {
                App.getDatabase().getDtcDao().clearTable();
                if (callbacks != null) callbacks.onDataFetched(new ArrayList<>());
            });
        }
    }

    public interface Callbacks {
        void onDataFetched(List<DtcEntity> data);
    }
}

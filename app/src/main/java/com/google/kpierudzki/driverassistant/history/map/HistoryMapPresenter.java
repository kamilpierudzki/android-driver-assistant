package com.google.kpierudzki.driverassistant.history.map;

import com.google.kpierudzki.driverassistant.history.map.usecase.Callbacks;
import com.google.kpierudzki.driverassistant.history.map.usecase.HistoryMapDbUseCase;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by Kamil on 02.08.2017.
 */

public class HistoryMapPresenter implements HistoryMapContract.Presenter, Callbacks {

    private HistoryMapContract.View view;
    private HistoryMapDbUseCase dbUseCase;

    public HistoryMapPresenter(HistoryMapContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        dbUseCase = new HistoryMapDbUseCase(this);
        if (view != null) view.onPresenterReady(this);
    }

    @Override
    public void stop() {
        if (dbUseCase != null) {
            dbUseCase.onDestroy();
            dbUseCase = null;
        }
    }

    @Override
    public void provideMapData(long trackId) {
        if (dbUseCase != null) dbUseCase.provideMapData(trackId);
    }

    @Override
    public void onMapDataResult(@NotNull ArrayList<HistoryMapContract.MapData> data) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onMapDataResult(data);
        });
    }
}

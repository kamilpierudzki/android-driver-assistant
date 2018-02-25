package com.google.kpierudzki.driverassistant.history.calendar.usecase;

import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.common.BasePresenter;
import com.google.kpierudzki.driverassistant.history.calendar.HistoryCalendarContract;
import com.google.kpierudzki.driverassistant.history.calendar.database.HistoryCalendarDao;
import com.google.kpierudzki.driverassistant.history.calendar.database.HistoryTranslationEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 06.08.2017.
 */

public class HistoryCalendarTranslationLocalUseCase implements BasePresenter {

    private Callbacks callbacks;
    private ExecutorService threadPool;
    private HistoryCalendarDao historyCalendarDao;

    HistoryCalendarTranslationLocalUseCase(Callbacks callbacks) {
        this.callbacks = callbacks;
        historyCalendarDao = App.getDatabase().getHistoryCalendarDao();
    }

    @Override
    public void start() {
        threadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void stop() {
        threadPool.shutdownNow();
    }

    @MainThread
    void translateCoordinates(List<HistoryCalendarContract.CalendarTranslateInfoModel> models) {
        StreamSupport.stream(models).forEach(model -> threadPool.execute(() -> {
            HistoryCalendarContract.CalendarTranslateInfoModel cloned = model.clone();

            HistoryTranslationEntity startPointTranslation =
                    historyCalendarDao.getTranslation(cloned.trackId, cloned.startPoint.toString());
            HistoryTranslationEntity endPointTranslation =
                    historyCalendarDao.getTranslation(cloned.trackId, cloned.endPoint.toString());

            LocalTranslationModel result;
            if (startPointTranslation != null && endPointTranslation != null) {
                cloned.startPointTranslation = startPointTranslation.getTranslation();
                cloned.endPointTranslation = endPointTranslation.getTranslation();
                result = new LocalTranslationModel(true, cloned);
            } else
                result = new LocalTranslationModel(false, cloned);

            callbacks.onLocalTranslationResult(result);
        }));
    }

    @WorkerThread
    void saveTranslation(HistoryCalendarContract.CalendarTranslateInfoModel translationModel) {
        threadPool.execute(() -> {
            historyCalendarDao.addTranslation(new HistoryTranslationEntity(translationModel.startPoint,
                    translationModel.startPointTranslation, translationModel.trackId));
            historyCalendarDao.addTranslation(new HistoryTranslationEntity(translationModel.endPoint,
                    translationModel.endPointTranslation, translationModel.trackId));
        });
    }

    public interface Callbacks {
        void onLocalTranslationResult(LocalTranslationModel translationModel);
    }

    static class LocalTranslationModel extends HistoryCalendarContract.CalendarTranslateInfoModel {

        boolean isTranslated;

        LocalTranslationModel(boolean isTranslated, HistoryCalendarContract.CalendarTranslateInfoModel model) {
            super(model);
            this.isTranslated = isTranslated;
        }
    }
}

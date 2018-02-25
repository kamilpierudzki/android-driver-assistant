package com.google.kpierudzki.driverassistant.history.calendar.usecase;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.kpierudzki.driverassistant.common.BasePresenter;
import com.google.kpierudzki.driverassistant.history.calendar.HistoryCalendarContract;

import java.util.List;

/**
 * Created by Kamil on 06.08.2017.
 */

public class HistoryTranslationRepository implements BasePresenter, HistoryCalendarTranslationRemoteUseCase.Callbacks, HistoryCalendarTranslationLocalUseCase.Callbacks {

    private HistoryCalendarTranslationRemoteUseCase translationRemoteUseCase;
    private HistoryCalendarTranslationLocalUseCase translationLocalUseCase;
    private Callbacks callbacks;

    public HistoryTranslationRepository(@NonNull Callbacks callbacks) {
        this.callbacks = callbacks;
        translationRemoteUseCase = new HistoryCalendarTranslationRemoteUseCase(this);
        translationLocalUseCase = new HistoryCalendarTranslationLocalUseCase(this);
    }

    @Override
    public void start() {
        translationRemoteUseCase.start();
        translationLocalUseCase.start();
    }

    @Override
    public void stop() {
        translationRemoteUseCase.stop();
        translationLocalUseCase.stop();
    }

    @MainThread
    public void translateCoordinates(List<HistoryCalendarContract.CalendarTranslateInfoModel> models) {
        translationLocalUseCase.translateCoordinates(models);
    }

    @WorkerThread
    @Override
    public void onRemoteTranslationResult(HistoryCalendarContract.CalendarTranslateInfoModel model) {
        translationLocalUseCase.saveTranslation(model);
        callbacks.onTranslationResult(model);
    }

    @WorkerThread
    @Override
    public void onLocalTranslationResult(HistoryCalendarTranslationLocalUseCase.LocalTranslationModel translationModel) {
        if (translationModel.isTranslated)
            callbacks.onTranslationResult(translationModel);
        else
            translationRemoteUseCase.translateCoordinates(translationModel);
    }

    public interface Callbacks {
        void onTranslationResult(HistoryCalendarContract.CalendarTranslateInfoModel model);
    }
}

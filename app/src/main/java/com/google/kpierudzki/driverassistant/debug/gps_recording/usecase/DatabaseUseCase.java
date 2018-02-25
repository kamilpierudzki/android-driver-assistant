package com.google.kpierudzki.driverassistant.debug.gps_recording.usecase;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.service.database.AssistantDatabase;
import com.google.kpierudzki.driverassistant.service.helper.BackgroundWorkBasedHelper;
import com.google.kpierudzki.driverassistant.util.DatabaseExtractorUtil;

import java.io.IOException;

/**
 * Created by Kamil on 26.06.2017.
 */

public class DatabaseUseCase extends BackgroundWorkBasedHelper {

    public DatabaseUseCase() {
        super();
    }

    public void provideSamplesCount(@NonNull Callback callback) {
        backgroundHandler.post(() ->
                callback.onSamplesCount(App.getDatabase().getGpsProbeRecordingDao().getProbesCount()));
    }

    @MainThread
    public void exportDatabase(@NonNull Callback callback, String filename) {
        backgroundHandler.post(() -> {
            boolean result = true;
            try {
                DatabaseExtractorUtil.extractDB(AssistantDatabase.DATABASE_FILENAME, filename);
                App.getDatabase().getGpsProbeRecordingDao().clearTable();
            } catch (IOException e) {
                result = false;
            }
            callback.onDatabaseExported(result);
        });
    }

    public interface Callback {
        void onSamplesCount(int count);

        void onDatabaseExported(boolean success);
    }
}

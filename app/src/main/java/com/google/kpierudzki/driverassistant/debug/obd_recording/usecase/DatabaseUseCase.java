package com.google.kpierudzki.driverassistant.debug.obd_recording.usecase;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.service.database.AssistantDatabase;
import com.google.kpierudzki.driverassistant.service.helper.BackgroundWorkBasedHelper;
import com.google.kpierudzki.driverassistant.util.DatabaseExtractorUtil;

import java.io.IOException;

/**
 * Created by Kamil on 17.09.2017.
 */

public class DatabaseUseCase extends BackgroundWorkBasedHelper {

    public void provideSamplesCount(@NonNull Callback callback) {
        backgroundHandler.post(() -> {
            callback.onNewSamplesCount(ObdParamType.SPEED, App.getDatabase().getObdSpeedProbeRecordingDao().getProbesCount());
            callback.onNewSamplesCount(ObdParamType.ENGINE_RPM, App.getDatabase().getObdRpmProbeRecordingDao().getProbesCount());
        });
    }

    @MainThread
    public void exportDatabase(@NonNull Callback callback, String filename) {
        backgroundHandler.post(() -> {
            boolean result = true;
            try {
                DatabaseExtractorUtil.extractDB(AssistantDatabase.DATABASE_FILENAME, filename);
                App.getDatabase().getObdSpeedProbeRecordingDao().clearTable();
            } catch (IOException e) {
                result = false;
            }
            callback.onDatabaseExported(result);
        });
    }

    public interface Callback {
        void onNewSamplesCount(ObdParamType paramType, int count);

        void onDatabaseExported(boolean success);
    }
}

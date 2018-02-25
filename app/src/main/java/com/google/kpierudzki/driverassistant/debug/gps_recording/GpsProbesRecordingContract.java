package com.google.kpierudzki.driverassistant.debug.gps_recording;

import com.google.kpierudzki.driverassistant.BaseView;
import com.google.kpierudzki.driverassistant.common.BasePresenter;
import com.google.kpierudzki.driverassistant.debug.gps_recording.connector.IGpsProbesRecordingListener;

/**
 * Created by Kamil on 26.06.2017.
 */

public interface GpsProbesRecordingContract {

    interface View extends BaseView<Presenter> {
        void onNewSamplesCount(int count);

        void onNewRecordStatus(RecordStatus status);

        void onExportDatabase(boolean success);
    }

    interface Presenter extends BasePresenter, IGpsProbesRecordingListener {
        void startRecord();

        void stopRecord();

        void exportDatabase(String filename);
    }

    enum RecordStatus {
        RECORDING,
        NOT_RECORDING
    }
}

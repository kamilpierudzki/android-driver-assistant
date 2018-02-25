package com.google.kpierudzki.driverassistant.debug.obd_recording;

import com.google.kpierudzki.driverassistant.BaseView;
import com.google.kpierudzki.driverassistant.common.BasePresenter;
import com.google.kpierudzki.driverassistant.debug.obd_recording.connector.IObdProbesRecordingListener;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadListener;

/**
 * Created by Kamil on 17.09.2017.
 */

public interface ObdProbesRecordingContract {

    interface View extends BaseView<Presenter> {
        void onNewSamplesCount(ObdParamType paramType, int count);

        void onNewRecordStatus(RecordStatus status);

        void onExportDatabase(boolean success);
    }

    interface Presenter extends BasePresenter, IObdReadListener, IObdProbesRecordingListener {
        void startRecord();

        void stopRecord();

        void exportDatabase(String filename);
    }

    enum RecordStatus {
        RECORDING,
        NOT_RECORDING
    }
}

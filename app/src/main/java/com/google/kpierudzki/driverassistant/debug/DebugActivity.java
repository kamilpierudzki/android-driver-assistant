package com.google.kpierudzki.driverassistant.debug;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.debug.gps_recording.GpsProbesRecordingPresenter;
import com.google.kpierudzki.driverassistant.debug.gps_recording.view.GpsProbesRecordingFragment;
import com.google.kpierudzki.driverassistant.debug.obd_recording.ObdProbesRecordingPresenter;
import com.google.kpierudzki.driverassistant.debug.obd_recording.view.ObdProbesRecordingFragment;

/**
 * Created by Kamil on 08.12.2017.
 */

public class DebugActivity extends AppCompatActivity {

    public final static String KEY_DEBUG_OPTION = "KEY_DEBUG_OPTION";

    private DebugOption option;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(KEY_DEBUG_OPTION)) {
                option = (DebugOption) intent.getSerializableExtra(KEY_DEBUG_OPTION);
                switch (option) {
                    case GpsRecording:
                        GpsProbesRecordingFragment gpsRecordingFragment = GpsProbesRecordingFragment.newInstance();
                        getSupportFragmentManager().beginTransaction()
                                .replace(android.R.id.content, gpsRecordingFragment, GpsProbesRecordingFragment.TAG)
                                .commit();
                        new GpsProbesRecordingPresenter(gpsRecordingFragment);
                        break;
                    case ObdRecording:
                        ObdProbesRecordingFragment obdRecordingFragment = ObdProbesRecordingFragment.newInstance();
                        getSupportFragmentManager().beginTransaction()
                                .replace(android.R.id.content, obdRecordingFragment, ObdProbesRecordingFragment.TAG)
                                .commit();
                        new ObdProbesRecordingPresenter(obdRecordingFragment);
                        break;
                    case Limits:
                        //...
                        break;
                }
            }
        }
    }
}

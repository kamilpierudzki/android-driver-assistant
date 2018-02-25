package com.google.kpierudzki.driverassistant.debug.gps_recording.view;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.common.IDestroyable;
import com.google.kpierudzki.driverassistant.debug.DebugActivity;
import com.google.kpierudzki.driverassistant.debug.DebugOption;
import com.google.kpierudzki.driverassistant.debug.gps_recording.GpsProbesRecordingContract;
import com.google.kpierudzki.driverassistant.debug.gps_recording.GpsProbesRecordingPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 30.06.2017.
 */

public class DebugDrawerGpsProbesRecordingManager implements IDestroyable, GpsProbesRecordingContract.View, View.OnClickListener {

    @BindView(R.id.active_dot)
    View activeDot;

    @BindView(R.id.label)
    TextView label;

    private GpsProbesRecordingContract.Presenter presenter;
    private DrawerLayout drawer;
    private FrameLayout frame;

    public DebugDrawerGpsProbesRecordingManager(View view, FragmentManager fragmentManager,
                                                DrawerLayout drawer, FrameLayout frame) {
        this.drawer = drawer;
        this.frame = frame;
        View root = view.findViewById(R.id.dbg_nav_record_sample);
        ButterKnife.bind(this, root);
        root.setOnClickListener(this);
        label.setText("Record GPS probes");
        new GpsProbesRecordingPresenter(this);
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.stop();
            presenter = null;
        }
    }

    @Override
    public void setPresenter(GpsProbesRecordingContract.Presenter presenter) {
        this.presenter = presenter;
        this.presenter.start();
    }

    @Override
    public void onPresenterReady(GpsProbesRecordingContract.Presenter presenter) {
        //...
    }

    @Override
    public void onNewSamplesCount(int count) {
        //Ignore
    }

    @Override
    public void onNewRecordStatus(GpsProbesRecordingContract.RecordStatus status) {
        switch (status) {
            case NOT_RECORDING:
                activeDot.setVisibility(View.INVISIBLE);
                break;
            case RECORDING:
                activeDot.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onExportDatabase(boolean success) {
        //Ignore
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(App.getAppContext(), DebugActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DebugActivity.KEY_DEBUG_OPTION, DebugOption.GpsRecording);
        App.getAppContext().startActivity(intent);
        drawer.closeDrawers();
    }
}

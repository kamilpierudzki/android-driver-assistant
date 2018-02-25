package com.google.kpierudzki.driverassistant.debug.obd_recording.view;

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
import com.google.kpierudzki.driverassistant.debug.obd_recording.ObdProbesRecordingContract;
import com.google.kpierudzki.driverassistant.debug.obd_recording.ObdProbesRecordingPresenter;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kamilpierudzki on 20/09/2017.
 */

public class DebugDrawerObdProbesRecordingManager implements IDestroyable, ObdProbesRecordingContract.View, View.OnClickListener {

    @BindView(R.id.active_dot)
    View activeDot;

    @BindView(R.id.label)
    TextView label;

    private FragmentManager fragmentManager;
    private ObdProbesRecordingContract.Presenter presenter;
    private DrawerLayout drawer;
    private FrameLayout frame;

    public DebugDrawerObdProbesRecordingManager(View view, FragmentManager fragmentManager,
                                                DrawerLayout drawer, FrameLayout frame) {
        this.fragmentManager = fragmentManager;
        this.drawer = drawer;
        this.frame = frame;
        View root = view.findViewById(R.id.dbg_obd_probes);
        ButterKnife.bind(this, root);
        root.setOnClickListener(this);
        label.setText("Record OBD probes");
        new ObdProbesRecordingPresenter(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(App.getAppContext(), DebugActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DebugActivity.KEY_DEBUG_OPTION, DebugOption.ObdRecording);
        App.getAppContext().startActivity(intent);
        drawer.closeDrawers();
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.stop();
            presenter = null;
        }
    }

    @Override
    public void setPresenter(ObdProbesRecordingContract.Presenter presenter) {
        this.presenter = presenter;
        this.presenter.start();
    }

    @Override
    public void onPresenterReady(ObdProbesRecordingContract.Presenter presenter) {
        //...
    }

    @Override
    public void onNewSamplesCount(ObdParamType paramType, int count) {
        //Ignore
    }

    @Override
    public void onNewRecordStatus(ObdProbesRecordingContract.RecordStatus status) {
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
}

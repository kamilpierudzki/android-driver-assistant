package com.google.kpierudzki.driverassistant.debug.gps_recording.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.debug.ExportDatabaseDialog;
import com.google.kpierudzki.driverassistant.debug.gps_recording.GpsProbesRecordingContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 26.06.2017.
 */

public class GpsProbesRecordingFragment extends Fragment implements GpsProbesRecordingContract.View,
        View.OnClickListener, ExportDatabaseDialog.Callback {

    public final static String TAG = "GpsProbesRecordingFragment_TAG";

    private GpsProbesRecordingContract.Presenter presenter;
    private GpsProbesRecordingContract.RecordStatus currentStatus;

    @BindView(R.id.start_stop_button)
    Button startStopButton;

    @BindView(R.id.samples_count)
    TextView samplesCount;

    @BindView(R.id.export_database)
    Button exportDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_debug_gps_probes_recording, container, false);
        ButterKnife.bind(this, root);

        startStopButton.setOnClickListener(this);
        exportDatabase.setOnClickListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) presenter.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (presenter != null) presenter.stop();
    }

    public static GpsProbesRecordingFragment newInstance() {
        return new GpsProbesRecordingFragment();
    }

    @Override
    public void onNewSamplesCount(int count) {
        samplesCount.setText(String.valueOf(count));
    }

    @Override
    public void onNewRecordStatus(GpsProbesRecordingContract.RecordStatus status) {
        currentStatus = status;
        switch (status) {
            case RECORDING:
                startStopButton.setText("stop");
                break;
            case NOT_RECORDING:
                startStopButton.setText("start");

                break;
        }
    }

    @Override
    public void onExportDatabase(boolean success) {
        if (success)
            Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == startStopButton.getId()) {
            switch (currentStatus) {
                case NOT_RECORDING:
                    if (presenter != null) presenter.startRecord();
                    break;
                case RECORDING:
                    if (presenter != null) presenter.stopRecord();
                    break;
            }
        } else if (v.getId() == exportDatabase.getId()) {
            showFilenameDialog();
        }
    }

    @Override
    public void setPresenter(GpsProbesRecordingContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onPresenterReady(GpsProbesRecordingContract.Presenter presenter) {
        //...
    }

    private void showFilenameDialog() {
        ExportDatabaseDialog dialog = new ExportDatabaseDialog();
        Bundle args = new Bundle();
        args.putString(ExportDatabaseDialog.USED_TAG, TAG);
        dialog.setArguments(args);
        if (getActivity() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), "ExportDatabaseDialog");
        }
    }

    @Override
    public void onDialogPositiveClicked(String filename) {
        if (presenter != null) presenter.exportDatabase(filename);
    }
}

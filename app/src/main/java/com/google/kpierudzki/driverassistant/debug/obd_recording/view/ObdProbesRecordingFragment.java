package com.google.kpierudzki.driverassistant.debug.obd_recording.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.debug.ExportDatabaseDialog;
import com.google.kpierudzki.driverassistant.debug.obd_recording.ObdProbesRecordingContract;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kamilpierudzki on 20/09/2017.
 */

public class ObdProbesRecordingFragment extends Fragment implements View.OnClickListener,
        ObdProbesRecordingContract.View, ExportDatabaseDialog.Callback {

    public static String TAG = "ObdProbesRecordingFragment_TAG";

    @BindView(R.id.start_stop_button)
    Button startStopButton;

    @BindView(R.id.export_database)
    Button exportDatabase;

    @BindView(R.id.obd_record_recycler_view)
    RecyclerView recyclerView;

    private ObdProbesRecordingContract.Presenter presenter;
    private ObdProbesRecordingContract.RecordStatus currentStatus;
    private ObdProbesRecordingAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_debug_obd_probes_recording, container, false);
        ButterKnife.bind(this, root);

        startStopButton.setOnClickListener(this);
        exportDatabase.setOnClickListener(this);

        adapter = new ObdProbesRecordingAdapter(new ArrayList<ObdParamType>() {{
            add(ObdParamType.SPEED);
            add(ObdParamType.ENGINE_RPM);
            add(ObdParamType.MAF);
            add(ObdParamType.COOLANT_TEMP);
            add(ObdParamType.ENGINE_LOAD);
            add(ObdParamType.BAROMETRIC_PRESSURE);
            add(ObdParamType.OIL_TEMP);
            add(ObdParamType.AMBIENT_AIR_TEMP);
        }});

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

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

    public static ObdProbesRecordingFragment newInstance() {
        return new ObdProbesRecordingFragment();
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

    @Override
    public void setPresenter(ObdProbesRecordingContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onPresenterReady(ObdProbesRecordingContract.Presenter presenter) {
        //...
    }

    @Override
    public void onNewSamplesCount(ObdParamType paramType, int count) {
        adapter.updateData(new ObdProbesRecordingAdapter.ObdProbeRecordingModel(paramType, count));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNewRecordStatus(ObdProbesRecordingContract.RecordStatus status) {
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
}

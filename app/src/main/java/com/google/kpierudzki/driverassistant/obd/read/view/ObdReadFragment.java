package com.google.kpierudzki.driverassistant.obd.read.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.kpierudzki.driverassistant.MainActivityFragmentsCallbacks;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.dtc.view.DtcFragment;
import com.google.kpierudzki.driverassistant.obd.common.DeviceMalfunctionDialog;
import com.google.kpierudzki.driverassistant.obd.connect.view.ObdConnectFragment;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.obd.read.ObdReadContract;
import com.google.kpierudzki.driverassistant.obd.read.ObdReadPresenter;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.obd.start.view.ObdStartFragment;
import com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterState;
import com.google.kpierudzki.driverassistant.util.UnitUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 16.09.2017.
 */

public class ObdReadFragment extends Fragment implements ObdReadContract.View {

    public static final String TAG = "ObdReadFragment_TAG";
    private MainActivityFragmentsCallbacks mainActivityFragmentsCallbacks;
    private ObdReadContract.Presenter presenter;
    private View root;
    private TilesManager tilesManager;

    @BindView(R.id.obd_read_scroll)
    View scrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_obd_read, container, false);
        ButterKnife.bind(this, root);

        if (mainActivityFragmentsCallbacks != null)
            mainActivityFragmentsCallbacks.onFragmentLoaded
                    (MainActivityFragmentsCallbacks.LoadedFragment.ObdII);

        prepareAppBar(true);

        new ObdReadPresenter(this);

        if (getActivity() != null) {
            tilesManager = new TilesManager(scrollView, getActivity().getSupportFragmentManager());
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        prepareAppBar(false);
        presenter = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (presenter != null) presenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (presenter != null) presenter.stop();
    }

    @Override
    public void setPresenter(ObdReadContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onPresenterReady(ObdReadContract.Presenter presenter) {
        presenter.fetchDtcInfo();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mainActivityFragmentsCallbacks = (MainActivityFragmentsCallbacks) getActivity();
        } catch (Exception e) {
            Log.e("ObdReadFragment", e.getLocalizedMessage());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mainActivityFragmentsCallbacks = (MainActivityFragmentsCallbacks) getActivity();
        } catch (Exception e) {
            Log.e("ObdReadFragment", e.getLocalizedMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mainActivityFragmentsCallbacks = null;
    }

    @Override
    public void onBluetoothAdapterStateChanged(@NonNull BluetoothAdapterState state) {
        //Ignore
    }

    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        switch (state) {
            case Connecting:
                getFragmentManager().beginTransaction()
                        .remove(this)
                        .replace(mainActivityFragmentsCallbacks.getMainFrameId(),
                                ObdConnectFragment.newInstance(),
                                ObdConnectFragment.TAG)
                        .commit();
                break;
            case Failed:
                getFragmentManager().beginTransaction()
                        .remove(this)
                        .replace(mainActivityFragmentsCallbacks.getMainFrameId(),
                                ObdStartFragment.newInstance(),
                                ObdStartFragment.TAG)
                        .commit();
                break;
        }
    }

    @Override
    public void onNewObdData(ObdCommandModel data) {
        if (tilesManager != null) {
            float value = data.value;
            if (data.paramType == ObdParamType.SPEED) value = UnitUtils.mpsToKmh(value);
            tilesManager.updateValue(value, data.paramType);
            tilesManager.updateChart(value, data.paramType);
        }
    }

    @Override
    public void onDeviceMalfunction() {
        new DeviceMalfunctionDialog().show(getFragmentManager(), DeviceMalfunctionDialog.TAG);
    }

    public static ObdReadFragment newInstance() {
        return new ObdReadFragment();
    }

    private void prepareAppBar(boolean initialize) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                if (initialize) {
                    supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                    supportActionBar.setTitle(R.string.OBD2_Name);
                } else {
                    supportActionBar.setDisplayOptions(0);
                }
            }
        }
    }

    @Override
    public void onNewDtcDetected() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                //Remove previous icon
                supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

                AppCompatImageView icon = new AppCompatImageView(activity);
                icon.setImageResource(R.drawable.ic_error);
                icon.setOnClickListener(view -> {
                    if (getFragmentManager() != null)
                        getFragmentManager().beginTransaction()
                                .remove(this)
                                .replace(mainActivityFragmentsCallbacks.getMainFrameId(),
                                        DtcFragment.newInstance(),
                                        DtcFragment.TAG)
                                .commit();
                });
                ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.END;
                params.setMargins((int) activity.getResources().getDimension(R.dimen.Obd_Read_Toolbar_DtcIcon_Margin),
                        0,
                        (int) activity.getResources().getDimension(R.dimen.Obd_Read_Toolbar_DtcIcon_Margin),
                        0);

                supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM |
                        ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
                supportActionBar.setCustomView(icon, params);
            }
        }
    }
}

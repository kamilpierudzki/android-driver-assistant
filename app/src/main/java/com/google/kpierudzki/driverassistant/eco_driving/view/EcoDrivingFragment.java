package com.google.kpierudzki.driverassistant.eco_driving.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.google.kpierudzki.driverassistant.MainActivityFragmentsCallbacks;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.common.view_components.TileFragmentNew;
import com.google.kpierudzki.driverassistant.eco_driving.EcoDrivingContract;
import com.google.kpierudzki.driverassistant.eco_driving.EcoDrivingPresenter;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.util.UnitUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 25.06.2017.
 */

public class EcoDrivingFragment extends Fragment implements EcoDrivingContract.View,
        ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {

    public final static int ACCESS_FINE_LOCATION_REQUEST = 125;
    public final static String TAG = "EcoDrivingFragment_TAG";
    public final static String SPEED_CHART_TAG = "SPEED_CHART_TAG";
    public final static String SCORE_CHART_TAG = "SCORE_CHART_TAG";
    private final static String ACCELERATION_CHART_DATA_KEY = "ACCELERATION_CHART_DATA_KEY";

    private EcoDrivingContract.Presenter ecoDrivingPresenter;
    private Runnable _provideLastSamplesTask;
    private ChartManagerNew chartManager;
    private MainActivityFragmentsCallbacks mainActivityFragmentsCallbacks;

    @BindView(R.id.lineChart)
    LineChart chartWidget;

    @BindView(R.id.notification_label)
    TextView notificationLabel;

    @BindView(R.id.notification_container)
    ViewGroup notificationContainer;

    private TileFragmentNew tileFragmentSpeed, tileFragmentScore;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_eco_driving, container, false);
        ButterKnife.bind(this, root);

        chartManager = new ChartManagerNew(getActivity(), chartWidget);

        if (savedInstanceState != null) {
            if (getActivity() != null) tileFragmentSpeed = (TileFragmentNew)
                    getActivity().getSupportFragmentManager().findFragmentByTag(SPEED_CHART_TAG);

            if (getActivity() != null) tileFragmentScore = (TileFragmentNew)
                    getActivity().getSupportFragmentManager().findFragmentByTag(SCORE_CHART_TAG);

            float[] restoredValues = savedInstanceState.getFloatArray(ACCELERATION_CHART_DATA_KEY);
            if (restoredValues != null && restoredValues.length > 0) {
                List<Float> vals = new ArrayList<>();
                for (float restoredValue : restoredValues) vals.add(restoredValue);
                chartManager.restoreValuesAndUpdateChart(vals);
            }
        } else {
            tileFragmentSpeed = TileFragmentNew.newInstance(ObdParamType.SPEED);
            if (getActivity() != null)
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, tileFragmentSpeed, SPEED_CHART_TAG)
                        .commit();

            tileFragmentScore = TileFragmentNew.newInstance(ObdParamType.ECO_SCORE);
            if (getActivity() != null)
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout2, tileFragmentScore, SCORE_CHART_TAG)
                        .commit();

            _provideLastSamplesTask = () -> {
                if (ecoDrivingPresenter != null) {
                    ecoDrivingPresenter.provideLastNSamplesForParam(
                            chartManager.getChartEntriesLimit(),
                            EcoDrivingContract.EcoDrivingParameter.ACCELERATION);

                    ecoDrivingPresenter.provideLastNSamplesForParam(
                            tileFragmentScore.getChartEntriesLimit(),
                            EcoDrivingContract.EcoDrivingParameter.SCORE);

                    ecoDrivingPresenter.provideLastNSamplesForParam(
                            tileFragmentSpeed.getChartEntriesLimit(),
                            EcoDrivingContract.EcoDrivingParameter.SPEED);
                }
            };
        }

        prepareAppBar(true);

        new EcoDrivingPresenter(this);

        if (mainActivityFragmentsCallbacks != null)
            mainActivityFragmentsCallbacks.onFragmentLoaded(MainActivityFragmentsCallbacks.LoadedFragment.EcoDriving);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        prepareAppBar(false);
        ecoDrivingPresenter = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ecoDrivingPresenter != null) ecoDrivingPresenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (ecoDrivingPresenter != null) ecoDrivingPresenter.stop();
    }

    public static EcoDrivingFragment newInstance() {
        return new EcoDrivingFragment();
    }

    @Override
    public void setPresenter(EcoDrivingContract.Presenter presenter) {
        this.ecoDrivingPresenter = presenter;
    }

    @Override
    public void onPresenterReady(EcoDrivingContract.Presenter presenter) {
        if (presenter == ecoDrivingPresenter && _provideLastSamplesTask != null) {
            _provideLastSamplesTask.run();
            _provideLastSamplesTask = null;
        }
    }

    @Override
    public void updateSpeedClock(float speed) {
        if (tileFragmentSpeed != null) {
            float speedInKmh = UnitUtils.mpsToKmh(speed);
            tileFragmentSpeed.updateChart(speedInKmh, null);
            tileFragmentSpeed.updateValue(speedInKmh, null);
        }
    }

    @Override
    public void updateScoreClock(float score) {
        if (tileFragmentScore != null) {
            float correctedScore = score * 100;
            tileFragmentScore.updateChart(correctedScore, null);
            tileFragmentScore.updateValue(correctedScore, null);
        }
    }

    @Override
    public void updateChart(float currentAcceleration) {
        chartManager.updateChart(currentAcceleration, null);
    }

    @Override
    public void updateGpsState(IGeoSampleListener.GpsProviderState state) {
        switch (state) {
            case Disabled:
                notificationContainer.setVisibility(View.VISIBLE);
                notificationContainer.setOnClickListener(null);
                notificationLabel.setText(R.string.GpsStatus_Label_NotEnabled);
                break;
            case Enabled:
                notificationContainer.setVisibility(View.INVISIBLE);
                break;
            case NotSupported:
                notificationContainer.setOnClickListener(this);
                notificationContainer.setVisibility(View.VISIBLE);
                notificationLabel.setText(R.string.GpsStatus_Label_NotSupported);

                Activity activity = getActivity();
                if (activity != null) {
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            ACCESS_FINE_LOCATION_REQUEST);
                }
                break;
        }
    }

    @Override
    public void onDataProviderChanged(EcoDrivingContract.EcoDrivingDataProvider provider) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                AppCompatImageView icon = new AppCompatImageView(activity);
                switch (provider) {
                    case Gps:
                        icon.setImageResource(R.drawable.ic_gps);
                        break;
                    case Obd:
                        icon.setImageResource(R.drawable.ic_bluetooth_w);
                        break;
                }
                ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.END;
                params.setMargins((int) activity.getResources().getDimension(R.dimen.EcoDriving_Toolbar_ProviderIcon_Margin),
                        0,
                        (int) activity.getResources().getDimension(R.dimen.EcoDriving_Toolbar_ProviderIcon_Margin),
                        0);

                supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM |
                        ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
                supportActionBar.setCustomView(icon, params);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Float[] data = chartManager.getValues();
        float[] toStore = new float[data.length];
        for (int i = 0; i < data.length; i++) toStore[i] = data[i];
        outState.putFloatArray(ACCELERATION_CHART_DATA_KEY, toStore);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST && grantResults[0] >= 0) {
            if (ecoDrivingPresenter != null) ecoDrivingPresenter.onPermissionGranted();
        }
    }

    @Override
    public void onLastDataOfParam(List<Float> data, EcoDrivingContract.EcoDrivingParameter parameter) {
        switch (parameter) {
            case ACCELERATION:
                chartManager.restoreValuesAndUpdateChart(data);
                break;
            case SPEED:
                tileFragmentSpeed.restoreValuesAndUpdateChart(data);
                break;
            case SCORE:
                tileFragmentScore.restoreValuesAndUpdateChart(data);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mainActivityFragmentsCallbacks = (MainActivityFragmentsCallbacks) getActivity();
        } catch (Exception e) {
            Log.e("CalendarFragment", e.getLocalizedMessage());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mainActivityFragmentsCallbacks = (MainActivityFragmentsCallbacks) getActivity();
        } catch (Exception e) {
            Log.e("CalendarFragment", e.getLocalizedMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivityFragmentsCallbacks = null;
    }

    @Override
    public void onClick(View view) {
        updateGpsState(IGeoSampleListener.GpsProviderState.NotSupported);
    }

    private void prepareAppBar(boolean initialize) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                if (initialize) {
                    supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                    supportActionBar.setTitle(R.string.Ecodriving_Name);
                } else {
                    supportActionBar.setDisplayOptions(0);
                }
            }
        }
    }
}

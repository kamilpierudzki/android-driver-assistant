package com.google.kpierudzki.driverassistant.history.map.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.MainActivityFragmentsCallbacks;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.history.map.HistoryMapContract;
import com.google.kpierudzki.driverassistant.history.map.HistoryMapPresenter;
import com.google.kpierudzki.driverassistant.history.map.view.parameters.MapParametersManager;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdParamType;
import com.google.kpierudzki.driverassistant.util.AppBarUtil;
import com.google.kpierudzki.driverassistant.util.ScreenUtils;
import com.google.kpierudzki.driverassistant.util.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 30.07.2017.
 */

public class HistoryMapFragment extends Fragment implements OnMapReadyCallback, HistoryMapContract.View,
        SeekBarManager.ICallbacks, MapParametersManager.ICallbacks {

    public final static String KEY_MAP_INFO = "KEY_MAP_INFO";

    @BindView(R.id.map_date_label)
    TextView dateLabel;

    @BindView(R.id.map_view)
    MapView mapView;

    private Toolbar toolbar;

    private HistoryMapContract.Presenter _presenter;
    private HistoryMapContract.MapInfoModel mapInfoModel;

    private MapManager mapManager;
    private MapParametersManager mapParametersManager;
    private MainActivityFragmentsCallbacks mainActivityFragmentsCallbacks;
    private SeekBarManager seekBarManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, root);

        mapInfoModel = getArguments().getParcelable(KEY_MAP_INFO);

        prepareAppBar(true);

        Calendar trackDate = Calendar.getInstance();
        trackDate.setTimeInMillis(TimeUnit.SECONDS.toMillis(mapInfoModel.startTimeInSec));
        if (dateLabel != null)
            dateLabel.setText(TimeUtils.formatDateForHistoryList(trackDate));

        if (getActivity() != null) {
            WindowManager windowManager = getActivity().getWindowManager();
            if (windowManager != null)
                seekBarManager = new SeekBarManager(windowManager.getDefaultDisplay(), root, this);
        }

        RecyclerView recyclerView = toolbar.findViewById(R.id.container_map_recyclerview);
        if (recyclerView != null)
            mapParametersManager = new MapParametersManager(recyclerView, this, trackDate);

        mapManager = new MapManager(root, mapView, savedInstanceState, this);

        new HistoryMapPresenter(this);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        prepareAppBar(false);
        _presenter = null;
    }

    private void prepareAppBarBehaviour(boolean restoreDefault) {
        Activity activity = getActivity();
        if (activity != null) {
            ConstraintLayout appRootView = activity.findViewById(R.id.main_layout_root);
            if (appRootView != null && ScreenUtils.isTablet() && ScreenUtils.isLandscape()) {
                if (restoreDefault)
                    ((CoordinatorLayout.LayoutParams) appRootView.getLayoutParams())
                            .setBehavior(new AppBarLayout.ScrollingViewBehavior());
                else
                    ((CoordinatorLayout.LayoutParams) appRootView.getLayoutParams())
                            .setBehavior(null);
            }
        }
    }

    public static HistoryMapFragment newInstance(HistoryMapContract.MapInfoModel mapInfoModel) {
        HistoryMapFragment fragment = new HistoryMapFragment();
        Bundle params = new Bundle();
        params.putParcelable(KEY_MAP_INFO, mapInfoModel);
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mapManager != null) mapManager.onMapReady(googleMap);
        if (_presenter != null) _presenter.provideMapData(mapInfoModel.trackId);
    }

    @Override
    public void setPresenter(HistoryMapContract.Presenter presenter) {
        this._presenter = presenter;
    }

    @Override
    public void onPresenterReady(HistoryMapContract.Presenter presenter) {
        // Ignore
    }

    @Override
    public void onMapDataResult(@NonNull ArrayList<HistoryMapContract.MapData> data) {
        if (mapManager != null) {
            mapManager.setData(data);
            mapManager.drawPolyline();
        }

        if (mapParametersManager != null) mapParametersManager.setData(data);
        if (seekBarManager != null) seekBarManager.setMax(data.size());
    }

    @Override
    public void onSeekBarChanged(int progress) {
        if (mapParametersManager != null) {
            HistoryMapContract.MapData mapData = mapParametersManager.refresh(progress);
            if (mapData != null && mapManager != null)
                mapManager.updateMarker(mapData.geoSamplesEntity.getOffset());
        }
    }

    @Override
    public void onParameterClicked(ObdParamType paramType) {
        if (getActivity() != null) {
            MapParameterInfoDialog.newInstance(paramType)
                    .show(getActivity().getSupportFragmentManager(), MapParameterInfoDialog.TAG);
        }
    }

    private void prepareAppBar(boolean initialize) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            toolbar = activity.findViewById(R.id.toolbar);
            AppBarLayout appBarView = AppBarUtil.getAppBarView(activity);
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null && toolbar != null) {
                if (initialize) {
                    if (mainActivityFragmentsCallbacks != null)
                        mainActivityFragmentsCallbacks.setToolbarNavigationEnabled(true);

                    prepareAppBarBehaviour(false);
                    supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM |
                            ActionBar.DISPLAY_HOME_AS_UP);

                    if (ScreenUtils.isPhone()) {
                        if (ScreenUtils.isLandscape()) {
                            supportActionBar.setCustomView(LayoutInflater.from(App.getAppContext())
                                            .inflate(R.layout.container_history_map_app_bar_land_phone, null),
                                    new ActionBar.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT));
                        } else {
                            supportActionBar.setCustomView(LayoutInflater.from(App.getAppContext())
                                            .inflate(R.layout.container_history_map_app_bar, null),
                                    new ActionBar.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                    } else {
                        supportActionBar.setCustomView(LayoutInflater.from(App.getAppContext())
                                        .inflate(R.layout.container_history_map_app_bar, null),
                                new ActionBar.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                        if (ScreenUtils.isLandscape()) {
                            if (appBarView != null) {
                                appBarView.getLayoutParams().width =
                                        (int) getResources().getDimension(R.dimen.History_Map_Toolbar_Tablet_Land_Width);
                            }
                        }
                    }

                    TextView startPointLabel = toolbar.findViewById(R.id.start_pos_label);
                    TextView endPointLabel = toolbar.findViewById(R.id.end_pos_label);
                    if (startPointLabel != null) startPointLabel.setText(mapInfoModel.startPoint);
                    if (endPointLabel != null) endPointLabel.setText(mapInfoModel.endPoint);
                } else {
                    prepareAppBarBehaviour(true);
                    supportActionBar.setDisplayOptions(0);

                    if (mainActivityFragmentsCallbacks != null)
                        mainActivityFragmentsCallbacks.setToolbarNavigationEnabled(false);

                    if (ScreenUtils.isTablet() && ScreenUtils.isLandscape() && appBarView != null) {
                        ViewGroup.LayoutParams lp = appBarView.getLayoutParams();
                        if (lp != null) lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mainActivityFragmentsCallbacks = (MainActivityFragmentsCallbacks) getActivity();
        } catch (Exception e) {
            //...
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mainActivityFragmentsCallbacks = (MainActivityFragmentsCallbacks) getActivity();
        } catch (Exception e) {
            //...
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mainActivityFragmentsCallbacks = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapManager != null) mapManager.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mapManager != null) mapManager.onStart();
        if (_presenter != null) _presenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapManager != null) mapManager.onStop();
        if (_presenter != null) _presenter.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapManager != null) mapManager.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapManager != null) mapManager.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapManager != null) mapManager.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapManager != null) mapManager.onSaveInstanceState(outState);
    }
}

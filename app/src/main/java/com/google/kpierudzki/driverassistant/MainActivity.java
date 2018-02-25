package com.google.kpierudzki.driverassistant;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.kpierudzki.driverassistant.common.IDestroyable;
import com.google.kpierudzki.driverassistant.debug.export_main_db.DebugDrawerExportDb;
import com.google.kpierudzki.driverassistant.debug.gps_recording.view.DebugDrawerGpsProbesRecordingManager;
import com.google.kpierudzki.driverassistant.debug.obd_recording.view.DebugDrawerObdProbesRecordingManager;
import com.google.kpierudzki.driverassistant.dtc.view.DtcFragment;
import com.google.kpierudzki.driverassistant.eco_driving.view.EcoDrivingFragment;
import com.google.kpierudzki.driverassistant.history.calendar.view.HistoryCalendarFragment;
import com.google.kpierudzki.driverassistant.menu.demoModeSwitcher.MenuDemoModeSwitcher;
import com.google.kpierudzki.driverassistant.obd.start.view.ObdStartFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import java8.util.stream.StreamSupport;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener, MainActivityFragmentsCallbacks {

    public final static String DEMO_MODE_KEY = "demo_mode_key";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @BindView(R.id.main_frame)
    FrameLayout frame;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.bottomNavigationView)
    BottomNavigationView navigation;

    private List<IDestroyable> menuControls = new ArrayList<>();

    private int navigationSelectedId = -1;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public IMenuCreate menuCreateObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        prepareAppBar();

        drawer.addDrawerListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setToolbarNavigationClickListener(view -> getSupportFragmentManager().popBackStack());

        navigation.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            navigation.setSelectedItemId(R.id.navigation_ecodriving);

            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(DEMO_MODE_KEY)) {
                GlobalConfig.DEMO_MODE = intent.getBooleanExtra(DEMO_MODE_KEY, false);
            }
        }

        View view = findViewById(android.R.id.content);
        if (GlobalConfig.DEBUG_MODE) {
            menuControls.add(new DebugDrawerGpsProbesRecordingManager(view, getFragmentManager(), drawer, frame));
            menuControls.add(new DebugDrawerObdProbesRecordingManager(view, getFragmentManager(), drawer, frame));
            menuControls.add(new DebugDrawerExportDb(view));
        }
        menuControls.add(new MenuDemoModeSwitcher(view));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StreamSupport.stream(menuControls).forEach(IDestroyable::onDestroy);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (navigationSelectedId != R.id.navigation_ecodriving && item.getItemId() == R.id.navigation_ecodriving) {
            removeAllFragments();
            getSupportFragmentManager().beginTransaction()
                    .replace(frame.getId(), EcoDrivingFragment.newInstance(), EcoDrivingFragment.TAG)
                    .commit();
            navigationSelectedId = R.id.navigation_ecodriving;
            return true;
        } else if (navigationSelectedId != R.id.navigation_history && item.getItemId() == R.id.navigation_history) {
            removeAllFragments();
            getSupportFragmentManager().beginTransaction()
                    .replace(frame.getId(), HistoryCalendarFragment.newInstance(), HistoryCalendarFragment.TAG)
                    .commit();
            navigationSelectedId = R.id.navigation_history;
            return true;
        } else if (navigationSelectedId != R.id.navigation_obd2 && item.getItemId() == R.id.navigation_obd2) {
            removeAllFragments();
            getSupportFragmentManager().beginTransaction()
                    .replace(frame.getId(), ObdStartFragment.newInstance(), ObdStartFragment.TAG)
                    .commit();
            navigationSelectedId = R.id.navigation_obd2;
            return true;
        } else if (navigationSelectedId != R.id.navigation_dtc && item.getItemId() == R.id.navigation_dtc) {
            removeAllFragments();
            getSupportFragmentManager().beginTransaction()
                    .replace(frame.getId(), DtcFragment.newInstance(), DtcFragment.TAG)
                    .commit();
            navigationSelectedId = R.id.navigation_dtc;
            return true;
        }
        return false;
    }

    private void removeAllFragments() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        StreamSupport.stream(getSupportFragmentManager().getFragments()).forEach(fragmentTransaction::remove);
        fragmentTransaction.commit();
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        //...
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        //...
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        //...
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        //...
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EcoDrivingFragment.ACCESS_FINE_LOCATION_REQUEST && grantResults.length > 0 && grantResults[0] >= 0) {
            Fragment ecoDrivingFragment = getSupportFragmentManager().findFragmentByTag(EcoDrivingFragment.TAG);
            if (ecoDrivingFragment != null)
                ecoDrivingFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onFragmentLoaded(LoadedFragment loadedFragment) {
        switch (loadedFragment) {
            case EcoDriving:
                navigation.getMenu().getItem(0).setChecked(true);
                navigationSelectedId = R.id.navigation_ecodriving;
                break;
            case ObdII:
                navigation.getMenu().getItem(1).setChecked(true);
                navigationSelectedId = R.id.navigation_obd2;
                break;
            case History:
                navigation.getMenu().getItem(2).setChecked(true);
                navigationSelectedId = R.id.navigation_history;
                break;
            case Dtc:
                navigation.getMenu().getItem(3).setChecked(true);
                navigationSelectedId = R.id.navigation_dtc;
                break;
        }
    }

    @Override
    public int getMainFrameId() {
        return frame.getId();
    }

    @Override
    public void setToolbarNavigationEnabled(boolean enabled) {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(!enabled);
        actionBarDrawerToggle.syncState();

        if (enabled) {
            toolbar.setNavigationOnClickListener(v -> {
                if (menuCreateObservable != null)
                    menuCreateObservable.onNavigationBack();
            });
        } else {
            toolbar.setNavigationOnClickListener(v ->
                    drawer.openDrawer(Gravity.START, true));
        }
    }

    private void prepareAppBar() {
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menuCreateObservable != null)
            return menuCreateObservable.onMenuCreated(getMenuInflater(), menu);
        else
            return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (menuCreateObservable != null)
            return menuCreateObservable.onOptionSelected(item);
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (menuCreateObservable != null)
            menuCreateObservable.onNavigationBack();
        else
            super.onBackPressed();
    }
}

package com.google.kpierudzki.driverassistant.history.calendar.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.kpierudzki.driverassistant.IMenuCreate;
import com.google.kpierudzki.driverassistant.MainActivity;
import com.google.kpierudzki.driverassistant.MainActivityFragmentsCallbacks;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesTracksEntity;
import com.google.kpierudzki.driverassistant.history.calendar.HistoryCalendarContract;
import com.google.kpierudzki.driverassistant.history.calendar.HistoryCalendarPresenter;
import com.google.kpierudzki.driverassistant.history.map.HistoryMapContract;
import com.google.kpierudzki.driverassistant.history.map.view.HistoryMapFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 24.07.2017.
 */

public class HistoryCalendarFragment extends Fragment implements HistoryCalendarContract.View, OnDateSelectedListener,
        HistoryAdapter.Callbacks, IMenuCreate, ConfirmRemoveTracksDialog.ConfirmRemoveCallback {

    public final static String TAG = "HistoryCalendarFragment_TAG";
    private final static String CALENDAR_DATES_KEY = "CALENDAR_DATES_KEY";
    private final static String LIST_DATES_KEY = "LIST_DATES_KEY";
    private final static String TOOLBAR_STATE_KEY = "TOOLBAR_STATE_KEY";
    public final static String ITEMS_TO_REMOVE_KEY = "ITEMS_TO_REMOVE_KEY";

    @BindView(R.id.calendar_view)
    MaterialCalendarView calendarView;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static final int LAST_TRACK_NUMBER = 5;

    private HistoryCalendarContract.Presenter presenter;
    private HistoryAdapter adapter;
    private RiddenDaysDecorator riddenDaysDecorator;
    private MainActivityFragmentsCallbacks mainActivityFragmentsCallbacks;
    private Runnable _provideLastSamplesTask;
    private ArrayList<HistoryCalendarContract.CalendarTranslateInfoModel> itemsToRemove;
    private ToolbarState currentToolbarState = ToolbarState.NORMAL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, root);

        adapter = new HistoryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        riddenDaysDecorator = new RiddenDaysDecorator();

        calendarView.setOnDateChangedListener(this);
        calendarView.removeDecorators();
        calendarView.addDecorator(riddenDaysDecorator);

        itemsToRemove = new ArrayList<>();

        new HistoryCalendarPresenter(this);
        if (mainActivityFragmentsCallbacks != null)
            mainActivityFragmentsCallbacks.onFragmentLoaded(MainActivityFragmentsCallbacks.LoadedFragment.History);

        if (savedInstanceState != null) {
            ArrayList<CalendarDay> riddenDays =
                    savedInstanceState.getParcelableArrayList(CALENDAR_DATES_KEY);
            if (riddenDays != null) {
                riddenDaysDecorator.setRiddenDates(riddenDays);
                calendarView.invalidateDecorators();
            }

            ArrayList<HistoryCalendarContract.CalendarTranslateInfoModel> tracks =
                    savedInstanceState.getParcelableArrayList(LIST_DATES_KEY);
            if (tracks != null) {
                adapter.setData(tracks);
                adapter.notifyDataSetChanged();
            }

            currentToolbarState = (ToolbarState) savedInstanceState.getSerializable(TOOLBAR_STATE_KEY);
        } else {
            _provideLastSamplesTask = () -> {
                if (presenter != null) {
                    presenter.provideLastNTracks(LAST_TRACK_NUMBER);
                    presenter.provideAllTracksForCalendar();
                }
            };
        }
        prepareAppBar(currentToolbarState);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        prepareAppBar(ToolbarState.UNINIT);
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
    public void setPresenter(HistoryCalendarContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onPresenterReady(HistoryCalendarContract.Presenter presenter) {
        if (_provideLastSamplesTask != null) {
            _provideLastSamplesTask.run();
            _provideLastSamplesTask = null;
        }
    }

    @Override
    public void onAllTrackForCalendar(List<GeoSamplesTracksEntity> allTracks) {
        riddenDaysDecorator.setData(allTracks);
        calendarView.invalidateDecorators();
    }

    @Override
    public void onTrackForDate(List<HistoryCalendarContract.CalendarDbInfoModel> tracks) {
        itemsToRemove.clear();
        List<HistoryCalendarContract.CalendarTranslateInfoModel> datas = transformModels(tracks);
        if (presenter != null) presenter.translateCoordinates(datas);
        adapter.setData(datas);
        adapter.notifyDataSetChanged();
    }

    @NonNull
    private List<HistoryCalendarContract.CalendarTranslateInfoModel> transformModels(List<HistoryCalendarContract.CalendarDbInfoModel> models) {
        return StreamSupport.stream(models).map(calendarModel ->
                new HistoryCalendarContract.CalendarTranslateInfoModel(
                        calendarModel.trackEntity.getTrackID(),
                        calendarModel.trackEntity.getCoordinate(),
                        calendarModel.trackEntity.getCoordinate().toString(),
                        calendarModel.sampleEntity.getCoordinate(),
                        calendarModel.sampleEntity.getCoordinate().toString(),
                        calendarModel.trackEntity.getStartTime(),
                        ScoreColor.Companion.matchColor(calendarModel.score),
                        calendarModel.score))
                .collect(Collectors.toList());
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        if (riddenDaysDecorator != null && riddenDaysDecorator.shouldDecorate(date) && presenter != null) {
            prepareAppBar(ToolbarState.NORMAL);
            presenter.provideTracksForDate(date.getCalendar());
        }
    }

    public static HistoryCalendarFragment newInstance() {
        return new HistoryCalendarFragment();
    }

    @Override
    public void onTrackSelected(HistoryCalendarContract.CalendarTranslateInfoModel track) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(mainActivityFragmentsCallbacks.getMainFrameId(), HistoryMapFragment.newInstance(
                            new HistoryMapContract.MapInfoModel(track.trackId, track.startPointTranslation,
                                    track.endPointTranslation, track.startTimeInSec)))
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onItemChecked(HistoryCalendarContract.CalendarTranslateInfoModel checkedItem, boolean isChecked) {
        if (isChecked) {
            itemsToRemove.add(checkedItem);
        } else {
            Iterator<HistoryCalendarContract.CalendarTranslateInfoModel> iter = itemsToRemove.iterator();
            HistoryCalendarContract.CalendarTranslateInfoModel currentItem;
            while (iter.hasNext()) {
                currentItem = iter.next();
                if (currentItem.trackId == checkedItem.trackId) {
                    iter.remove();
                    break;
                }
            }
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                if (itemsToRemove.size() > 0) {
                    supportActionBar.setTitle(String.valueOf(itemsToRemove.size()));
                } else {
                    supportActionBar.setTitle(R.string.History_Calendar_Select);
                }
            }
        }
    }

    @Override
    public void onItemLongPressed() {
        currentToolbarState = ToolbarState.REMOVE;
        prepareAppBar(currentToolbarState);
        adapter.showRemoveMode(true);
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
        this.mainActivityFragmentsCallbacks = null;
    }

    @Override
    public void onReverseGeocode(HistoryCalendarContract.CalendarTranslateInfoModel trackInfo) {
        adapter.updateTrackInfo(trackInfo);
    }

    private void prepareAppBar(ToolbarState state) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                switch (state) {
                    case NORMAL:
                        supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                        supportActionBar.setTitle(R.string.History_Calendar_Name);
                        if (mainActivityFragmentsCallbacks != null)
                            mainActivityFragmentsCallbacks.setToolbarNavigationEnabled(false);
                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).menuCreateObservable = null;
                            activity.invalidateOptionsMenu();
                        }
                        break;
                    case UNINIT:
                        supportActionBar.setDisplayOptions(0);
                        if (mainActivityFragmentsCallbacks != null)
                            mainActivityFragmentsCallbacks.setToolbarNavigationEnabled(false);
                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).menuCreateObservable = null;
                            activity.invalidateOptionsMenu();
                        }
                        break;
                    case REMOVE:
                        if (mainActivityFragmentsCallbacks != null)
                            mainActivityFragmentsCallbacks.setToolbarNavigationEnabled(true);
                        supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE |
                                ActionBar.DISPLAY_HOME_AS_UP);
                        supportActionBar.setTitle(R.string.History_Calendar_Select);
                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).menuCreateObservable = this;
                            activity.invalidateOptionsMenu();
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(CALENDAR_DATES_KEY, riddenDaysDecorator.getRiddenDates());
        outState.putParcelableArrayList(LIST_DATES_KEY, adapter.getData());
        outState.putSerializable(TOOLBAR_STATE_KEY, currentToolbarState);
    }

    @Override
    public boolean onMenuCreated(MenuInflater menuInflater, Menu menu) {
        menuInflater.inflate(R.menu.calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionSelected(MenuItem item) {
        ConfirmRemoveTracksDialog dialog = new ConfirmRemoveTracksDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ITEMS_TO_REMOVE_KEY, itemsToRemove);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), ConfirmRemoveTracksDialog.Companion.getTAG());
        return true;
    }

    @Override
    public void onNavigationBack() {
        onCancel();
    }

    @Override
    public void onTracksRemove(@NotNull ArrayList<HistoryCalendarContract.CalendarTranslateInfoModel> tracksToRemove) {
        adapter.removeData(tracksToRemove);
        onCancel();
        if (presenter != null) {
            presenter.removeTracks(StreamSupport.stream(tracksToRemove)
                    .map(model -> model.trackId)
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public void onCancel() {
        currentToolbarState = ToolbarState.NORMAL;
        prepareAppBar(currentToolbarState);
        adapter.showRemoveMode(false);
        itemsToRemove.clear();
    }

    private enum ToolbarState {
        UNINIT, NORMAL, REMOVE
    }
}

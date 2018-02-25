package com.google.kpierudzki.driverassistant.history.calendar.usecase;

import android.support.annotation.Nullable;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingDao;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingStatistic;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesDao;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesEntity;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesTracksEntity;
import com.google.kpierudzki.driverassistant.history.calendar.HistoryCalendarContract;
import com.google.kpierudzki.driverassistant.service.database.AssistantDatabase;
import com.google.kpierudzki.driverassistant.service.helper.BackgroundThreadPoolHelper;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 26.07.2017.
 */

public class HistoryCalendarDbUseCase extends BackgroundThreadPoolHelper {

    private Callbacks callbacks;

    public HistoryCalendarDbUseCase(Callbacks callbacks) {
        super();
        this.callbacks = callbacks;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callbacks = null;
    }

    public void provideLastNTracks(int n) {
        if (threadPool != null && !threadPool.isTerminating())
            threadPool.execute(() -> {
                GeoSamplesDao geoSamplesDao = App.getDatabase().getGeoSamplesDao();
                List<GeoSamplesTracksEntity> lastTracks = geoSamplesDao.getLastNTracks(n);
                if (lastTracks != null) {
                    List<HistoryCalendarContract.CalendarDbInfoModel> result = StreamSupport.stream(lastTracks)
                            .map(tracksEntry -> {
                                GeoSamplesEntity lastSampleForTrack = geoSamplesDao
                                        .getLastSampleForTrackId(tracksEntry.getTrackID());

                                EcoDrivingDao ecoDrivingDao = App.getDatabase().getEcoDrivingDao();
                                EcoDrivingStatistic statistics = EcoDrivingStatistic.merge(
                                        ecoDrivingDao.getScoreStatisticsForTrackId(tracksEntry.getTrackID()),
                                        ecoDrivingDao.getCountStatisticsForTrackId(tracksEntry.getTrackID()));

                                return new HistoryCalendarContract.CalendarDbInfoModel(tracksEntry,
                                        lastSampleForTrack != null ? lastSampleForTrack : new GeoSamplesEntity(),
                                        calculateScore(statistics));
                            })
                            .collect(Collectors.toList());
                    if (callbacks != null && result != null && !result.isEmpty())
                        callbacks.onTrackForDate(result);
                }
            });
    }

    public void provideAllTracksForCalendar() {
        if (threadPool != null && !threadPool.isTerminating())
            threadPool.execute(() -> {
                if (callbacks != null)
                    callbacks.onAllTrackForCalendar(App.getDatabase().getGeoSamplesDao().getAllTracks());
            });
    }

    public void provideTracksForDate(Calendar date) {
        if (threadPool != null && !threadPool.isTerminating())
            threadPool.execute(() -> {
                if (callbacks != null) {
                    Calendar endDate = (Calendar) date.clone();
                    endDate.add(Calendar.HOUR_OF_DAY, 23);
                    endDate.add(Calendar.MINUTE, 59);
                    endDate.add(Calendar.SECOND, 59);

                    AssistantDatabase database = App.getDatabase();
                    List<GeoSamplesTracksEntity> trackInfos = database.getGeoSamplesDao().getTracksBetween(
                            TimeUnit.MILLISECONDS.toSeconds(date.getTimeInMillis()),
                            TimeUnit.MILLISECONDS.toSeconds(endDate.getTimeInMillis()));

                    List<HistoryCalendarContract.CalendarDbInfoModel> result = StreamSupport.stream(trackInfos)
                            .map(tracksEntry -> {
                                GeoSamplesEntity lastSampleForTrack = database.getGeoSamplesDao()
                                        .getLastSampleForTrackId(tracksEntry.getTrackID());

                                EcoDrivingDao ecoDrivingDao = App.getDatabase().getEcoDrivingDao();
                                EcoDrivingStatistic statistics = EcoDrivingStatistic.merge(
                                        ecoDrivingDao.getScoreStatisticsForTrackId(tracksEntry.getTrackID()),
                                        ecoDrivingDao.getCountStatisticsForTrackId(tracksEntry.getTrackID()));

                                return new HistoryCalendarContract.CalendarDbInfoModel(tracksEntry,
                                        lastSampleForTrack != null ? lastSampleForTrack : new GeoSamplesEntity(),
                                        calculateScore(statistics));
                            }).collect(Collectors.toList());
                    if (callbacks != null) callbacks.onTrackForDate(result);
                }
            });
    }

    private float calculateScore(EcoDrivingStatistic statistics) {
        return (1f * statistics.sum) / statistics.count;
    }

    public void removeTracks(@Nullable IRemoveCallback callback, List<Long> ids) {
        if (threadPool != null && !threadPool.isTerminating()) {
            threadPool.execute(() -> {
                AssistantDatabase database = App.getDatabase();
                database.getGeoSamplesDao().deleteTracks(ids);
                database.getGeoSamplesDao().deleteTracksData(ids);
                database.getEcoDrivingDao().deleteTracksData(ids);
                database.getObdParamsDao().deleteTracksData(ids);

                if (callback != null) callback.onRemoveResult(ids);
            });
        }
    }

    public interface Callbacks {
        void onAllTrackForCalendar(List<GeoSamplesTracksEntity> allTracks);

        void onTrackForDate(List<HistoryCalendarContract.CalendarDbInfoModel> tracks);
    }

    public interface IRemoveCallback {
        void onRemoveResult(List<Long> removedTracks);
    }
}

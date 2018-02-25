package com.google.kpierudzki.driverassistant.history.calendar.usecase;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.common.BasePresenter;
import com.google.kpierudzki.driverassistant.history.calendar.HistoryCalendarContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Kamil on 03.08.2017.
 */

public class HistoryCalendarTranslationRemoteUseCase implements BasePresenter {

    private Callbacks callbacks;
    private ExecutorService threadPool;
    private OkHttpClient client;

    HistoryCalendarTranslationRemoteUseCase(Callbacks callbacks) {
        this.callbacks = callbacks;
        client = new OkHttpClient();
    }

    @Override
    public void start() {
        threadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void stop() {
        threadPool.shutdownNow();
    }

    @WorkerThread
    void translateCoordinates(HistoryCalendarContract.CalendarTranslateInfoModel model) {
        threadPool.execute(() -> {
            HistoryCalendarContract.CalendarTranslateInfoModel cloned = model.clone();

            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s&key=%s";
            String startPointUrl = String.format(Locale.getDefault(), url, cloned.startPoint.toString(),
                    App.getAppContext().getString(R.string.google_reverse_geocode_key));
            String endPointUrl = String.format(Locale.getDefault(), url, cloned.endPoint.toString(),
                    App.getAppContext().getString(R.string.google_reverse_geocode_key));

            try {
                JSONObject startPointData = new JSONObject(performRequest(startPointUrl));
                cloned.startPointTranslation = startPointData.getJSONArray("results").getJSONObject(1)
                        .getString("formatted_address");

                JSONObject endPointData = new JSONObject(performRequest(endPointUrl));
                cloned.endPointTranslation = endPointData.getJSONArray("results").getJSONObject(1)
                        .getString("formatted_address");
                callbacks.onRemoteTranslationResult(cloned);
            } catch (JSONException e) {
                Log.e("reverse_geocode", e.getLocalizedMessage());
            }
        });
    }

    private String performRequest(String url) {
        Request request = new Request.Builder().url(url).build();
        String result = "";
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            //...
        }
        return result;
    }

    public interface Callbacks {
        void onRemoteTranslationResult(HistoryCalendarContract.CalendarTranslateInfoModel model);
    }
}

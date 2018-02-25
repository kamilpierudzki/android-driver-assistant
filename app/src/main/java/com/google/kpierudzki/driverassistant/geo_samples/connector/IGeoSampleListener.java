package com.google.kpierudzki.driverassistant.geo_samples.connector;


import android.location.Location;

import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

/**
 * Created by Kamil on 16.07.2017.
 */

public interface IGeoSampleListener extends IBaseManager.IBaseManagerListener {
    void onNewData(GeoSamplesSwappableData newData);

    void onGpsProviderStateChanged(GpsProviderState state);

    void onRawLocation(Location location);

    enum GpsProviderState {
        Enabled,
        Disabled,
        NotSupported
    }

    class GeoSamplesSwappableData {
        public long trackId;
        public long offset;
        public float speed;

        public GeoSamplesSwappableData(long trackId, long offset, float speed) {
            this.trackId = trackId;
            this.offset = offset;
            this.speed = speed;
        }

        public GeoSamplesSwappableData(GeoSamplesSwappableData data) {
            this.trackId = data.trackId;
            this.offset = data.offset;
            this.speed = data.speed;
        }
    }
}

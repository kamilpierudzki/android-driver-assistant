package com.google.kpierudzki.driverassistant.geo_samples.connector;

import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;

import java.util.List;

/**
 * Created by Kamil on 02.08.2017.
 */

public interface IGeoSampleObservable extends IBaseManager.IBaseManagerObservable {
    void forcePersistBuffer(boolean async);
    void onPermissionGranted();
    void onTrackRemoved(List<Long> removedTracks);
}

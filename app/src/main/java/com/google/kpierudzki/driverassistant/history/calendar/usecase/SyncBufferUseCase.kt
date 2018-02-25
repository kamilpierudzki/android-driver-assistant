package com.google.kpierudzki.driverassistant.history.calendar.usecase

import com.google.kpierudzki.driverassistant.eco_driving.connector.IEcoDrivingObservable
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleObservable
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadObservable
import com.google.kpierudzki.driverassistant.service.helper.BackgroundWorkBasedHelper

/**
 * Created by Kamil on 22.02.2018.
 */
class SyncBufferUseCase(
        private var ecoDrivingGpsObservable: IEcoDrivingObservable?,
        private var ecoDrivingObdObservable: IEcoDrivingObservable?,
        private var obdReadObservable: IObdReadObservable?,
        private var geoSamplesObservable: IGeoSampleObservable?)
    : BackgroundWorkBasedHelper() {

    override fun onDestroy() {
        super.onDestroy()
        ecoDrivingGpsObservable = null
        ecoDrivingObdObservable = null
        obdReadObservable = null
        geoSamplesObservable = null
    }

    fun persistBuffersSync(callback: SyncBufferCallback?) {
        backgroundHandler?.post({
            geoSamplesObservable?.forcePersistBuffer(false)
            ecoDrivingGpsObservable?.forcePersistBuffer(false)
            ecoDrivingObdObservable?.forcePersistBuffer(false)
            obdReadObservable?.forcePersistBuffer(false)

            callback?.onPersistFinished()
        })
    }
}

interface SyncBufferCallback {
    fun onPersistFinished();
}
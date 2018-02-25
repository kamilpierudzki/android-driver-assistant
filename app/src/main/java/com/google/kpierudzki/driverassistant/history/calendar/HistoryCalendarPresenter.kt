package com.google.kpierudzki.driverassistant.history.calendar

import com.google.kpierudzki.driverassistant.eco_driving.connector.IEcoDrivingObservable
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleObservable
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesTracksEntity
import com.google.kpierudzki.driverassistant.history.calendar.usecase.HistoryCalendarDbUseCase
import com.google.kpierudzki.driverassistant.history.calendar.usecase.HistoryTranslationRepository
import com.google.kpierudzki.driverassistant.history.calendar.usecase.SyncBufferCallback
import com.google.kpierudzki.driverassistant.history.calendar.usecase.SyncBufferUseCase
import com.google.kpierudzki.driverassistant.obd.read.connector.IObdReadObservable
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType
import com.google.kpierudzki.driverassistant.service.helper.ServiceBindHelper
import com.google.kpierudzki.driverassistant.util.MainThreadUtil
import java.util.*

/**
 * Created by Kamil on 26.07.2017.
 */

class HistoryCalendarPresenter(private val view: HistoryCalendarContract.View?) : HistoryCalendarContract.Presenter, HistoryCalendarDbUseCase.Callbacks, HistoryTranslationRepository.Callbacks {

    private var dbUseCase: HistoryCalendarDbUseCase? = null
    private var translationUseCase: HistoryTranslationRepository? = null
    private var syncBufferUseCase: SyncBufferUseCase? = null

    private var serviceBindHelper: ServiceBindHelper? = null

    private var ecoDrivingGpsObservable: IEcoDrivingObservable? = null
    private var ecoDrivingObdObservable: IEcoDrivingObservable? = null
    private var obdReadObservable: IObdReadObservable? = null
    private var geoSamplesObservable: IGeoSampleObservable? = null

    init {
        view?.setPresenter(this)
    }

    override fun start() {
        dbUseCase = HistoryCalendarDbUseCase(this)
        translationUseCase = HistoryTranslationRepository(this)
        translationUseCase?.start()

        bindConnector()
    }

    override fun stop() {
        unbindConnector()

        dbUseCase?.onDestroy()
        dbUseCase = null

        translationUseCase?.stop()
        translationUseCase = null
    }

    override fun provideLastNTracks(n: Int) {
        dbUseCase?.provideLastNTracks(n)
    }

    override fun provideAllTracksForCalendar() {
        dbUseCase?.provideAllTracksForCalendar()
    }

    override fun provideTracksForDate(date: Calendar) {
        dbUseCase?.provideTracksForDate(date)
    }

    override fun translateCoordinates(models: List<HistoryCalendarContract.CalendarTranslateInfoModel>) {
        translationUseCase?.translateCoordinates(models)
    }

    override fun removeTracks(ids: List<Long>) {
        syncBufferUseCase?.persistBuffersSync(object : SyncBufferCallback {
            override fun onPersistFinished() {
                dbUseCase?.removeTracks({ removedTracks ->
                    geoSamplesObservable?.onTrackRemoved(removedTracks)
                }, ids)
            }
        })
    }

    override fun onAllTrackForCalendar(allTracks: List<GeoSamplesTracksEntity>) {
        MainThreadUtil.post { view?.onAllTrackForCalendar(allTracks) }
    }

    override fun onTrackForDate(tracks: List<HistoryCalendarContract.CalendarDbInfoModel>) {
        MainThreadUtil.post { view?.onTrackForDate(tracks) }
    }

    override fun onTranslationResult(model: HistoryCalendarContract.CalendarTranslateInfoModel) {
        MainThreadUtil.post { view?.onReverseGeocode(model) }
    }

    private fun bindConnector() {
        serviceBindHelper = ServiceBindHelper { connector ->
            ecoDrivingGpsObservable = connector.provideObservable(
                    ManagerConnectorType.EcoDrivingGps) as? IEcoDrivingObservable
            ecoDrivingObdObservable = connector.provideObservable(
                    ManagerConnectorType.EcoDrivingObd) as? IEcoDrivingObservable
            obdReadObservable = connector.provideObservable(
                    ManagerConnectorType.ObdRead) as? IObdReadObservable
            geoSamplesObservable = connector.provideObservable(
                    ManagerConnectorType.GeoSamples) as? IGeoSampleObservable

            ecoDrivingGpsObservable?.forcePersistBuffer(true)
            ecoDrivingObdObservable?.forcePersistBuffer(true)
            obdReadObservable?.forcePersistBuffer(true)

            syncBufferUseCase = SyncBufferUseCase(
                    ecoDrivingGpsObservable,
                    ecoDrivingObdObservable,
                    obdReadObservable,
                    geoSamplesObservable)

            view?.onPresenterReady(this)
        }
    }

    private fun unbindConnector() {
        serviceBindHelper?.onDestroy()
        serviceBindHelper = null

        syncBufferUseCase?.onDestroy()
        syncBufferUseCase = null
    }
}

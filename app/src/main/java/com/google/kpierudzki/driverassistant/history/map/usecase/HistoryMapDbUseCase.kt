package com.google.kpierudzki.driverassistant.history.map.usecase

import com.google.kpierudzki.driverassistant.App
import com.google.kpierudzki.driverassistant.history.map.HistoryMapContract
import com.google.kpierudzki.driverassistant.service.helper.BackgroundThreadPoolHelper
import com.google.kpierudzki.driverassistant.util.MapUtils

/**
 * Created by Kamil on 21.01.2018.
 */
class HistoryMapDbUseCase(callbacks: Callbacks) : BackgroundThreadPoolHelper(), HistoryMapContract.Presenter {

    private var _callbacks: Callbacks? = callbacks

    override fun onDestroy() {
        super.onDestroy()
        _callbacks = null
    }

    override fun start() {
        throw UnsupportedOperationException("Not supported")
    }

    override fun stop() {
        throw UnsupportedOperationException("Not supported")
    }

    override fun provideMapData(trackId: Long) {
        threadPool?.let {
            if (!it.isTerminating)
                it.execute({
                    // Prepare geo data
                    var result = App.getDatabase().geoSamplesDao.getSamplesForTrack(trackId)
                            .map { HistoryMapContract.MapData(it) }
                            .toCollection(ArrayList<HistoryMapContract.MapData>())

                    // Prepare eco driving data
                    result = MapUtils.prepareMapDataWithEcoDrivingParams(
                            result,
                            App.getDatabase().ecoDrivingDao)

                    // Prepare obd data
                    MapUtils.prepareMapDataWithObdParams(
                            result,
                            App.getDatabase().obdParamsDao)

                    _callbacks?.onMapDataResult(result)
                })
        }
    }
}

interface Callbacks {
    fun onMapDataResult(data: ArrayList<HistoryMapContract.MapData>)
}
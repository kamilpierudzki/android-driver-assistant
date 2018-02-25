package com.google.kpierudzki.driverassistant.service.mock.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.google.kpierudzki.driverassistant.App
import com.google.kpierudzki.driverassistant.geo_samples.service.LocationListener
import com.google.kpierudzki.driverassistant.service.helper.BackgroundWorkBasedHelper
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


/**
 * Created by Kamil on 09.01.2018.
 */
class MockLocationManager : BackgroundWorkBasedHelper() {

    private val _locationManager: LocationManager
    private var _probes: List<LocationMockDataProvider.Probe>
    private var _listeners: ArrayList<LocationListener>
    private var _isWorking: Boolean by Delegates.observable(false, { _, old, new ->
        if (old != new && new) startMock()
    })
    private var _globalIdx: Int
    private var _currentListener: android.location.LocationListener

    init {
        _locationManager = App.getAppContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        _probes = LocationMockDataProvider.prepareData()
        _listeners = ArrayList<LocationListener>()
        _globalIdx = 0
        _currentListener = object : android.location.LocationListener {
            override fun onLocationChanged(location: Location) {
                //Ignore
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                // Ignore
            }

            override fun onProviderEnabled(provider: String) {
                notifyListenersWithProviderState(provider, ProviderState.Enabled)
            }

            override fun onProviderDisabled(provider: String) {
                notifyListenersWithProviderState(provider, ProviderState.Disabled)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _isWorking = false;
    }

    @MainThread
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(listener: LocationListener) {
        synchronized(this) {
            _listeners.add(listener)
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f,
                    _currentListener, backgroundHandler.looper)
            if (_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) _isWorking = true
        }
    }

    @MainThread
    fun removeUpdates(listener: LocationListener) {
        synchronized(this) {
            val listenersToLeft = arrayListOf<LocationListener>()
            _listeners.forEach { if (it !== listener) listenersToLeft.add(it) }
            _listeners.clear()
            _listeners.addAll(listenersToLeft)

            if (_listeners.isEmpty()) {
                _locationManager.removeUpdates(_currentListener)
                _isWorking = false
            }
        }
    }

    @WorkerThread
    private fun notifyListenersWithLocationChanged(location: Location) {
        synchronized(this) {
            _listeners.forEach { it.onLocationChanged(location) }
        }
    }

    private enum class ProviderState {
        Enabled, Disabled, NotSupported
    }

    @WorkerThread
    private fun notifyListenersWithProviderState(provider: String, providerState: ProviderState) {
        synchronized(this) {
            when (providerState) {
                ProviderState.Enabled -> {
                    _isWorking = true
                    _listeners.forEach { it.onProviderEnabled(provider) }
                }

                ProviderState.Disabled -> {
                    _isWorking = false
                    _listeners.forEach { it.onProviderDisabled(provider) }
                }

                ProviderState.NotSupported -> {
                    _isWorking = false
                    _listeners.forEach { it.onProviderNotSupported() }
                }
            }
        }
    }

    private fun buildLocation(probe: LocationMockDataProvider.Probe): Location {
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = probe.position.latitude
        location.longitude = probe.position.longitude
        location.speed = probe.speed
        return location
    }

    @WorkerThread
    private fun startMock() {
        if (_isWorking) {
            if (_globalIdx < _probes.size) {
                backgroundHandler?.postDelayed({
                    notifyListenersWithLocationChanged(buildLocation(_probes[_globalIdx]))
                    if (_isWorking) {
                        _globalIdx++
                        startMock()
                    }
                }, _probes[_globalIdx].delay)
            } else {
                _globalIdx = 0
                backgroundHandler.postDelayed({ startMock() }, TimeUnit.SECONDS.toMillis(1))
            }
        }
    }

    fun isProviderEnabled(): Boolean {
        return _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
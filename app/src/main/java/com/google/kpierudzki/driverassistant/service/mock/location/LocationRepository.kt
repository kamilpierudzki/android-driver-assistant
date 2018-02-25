package com.google.kpierudzki.driverassistant.service.mock.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.support.annotation.MainThread
import com.google.kpierudzki.driverassistant.GlobalConfig
import com.google.kpierudzki.driverassistant.common.IDestroyable
import com.google.kpierudzki.driverassistant.geo_samples.service.LocationListener

/**
 * Created by Kamil on 09.01.2018.
 */
class LocationRepository(context: Context, locationListener: LocationListener) : IDestroyable {

    private val _locationListener: LocationListener = locationListener
    private var _locationManager: LocationManager? = null
    private var _mockLocationManager: MockLocationManager? = null

    init {
        if (GlobalConfig.DEMO_MODE)
            _mockLocationManager = MockLocationManager()
        else
            _locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onDestroy() {
        _mockLocationManager?.let {
            it.removeUpdates(_locationListener)
            it.onDestroy()
        }
        _locationManager?.removeUpdates(_locationListener)
    }

    @SuppressLint("MissingPermission")
    @MainThread
    fun requestLocationUpdates() {
        if (GlobalConfig.DEMO_MODE) {
            try {
                _mockLocationManager?.let {
                    it.requestLocationUpdates(_locationListener)
                    if (it.isProviderEnabled())
                        _locationListener.onProviderEnabled(LocationManager.GPS_PROVIDER)
                    else
                        _locationListener.onProviderDisabled(LocationManager.GPS_PROVIDER)
                }
                if (_mockLocationManager == null) _locationListener.onProviderNotSupported()
            } catch (e: SecurityException) {
                _locationListener.onProviderNotSupported()
            }
        } else {
            try {
                _locationManager?.let {
                    it.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            0, 0f, _locationListener)
                    if (it.isProviderEnabled(LocationManager.GPS_PROVIDER))
                        _locationListener.onProviderEnabled(LocationManager.GPS_PROVIDER)
                    else
                        _locationListener.onProviderDisabled(LocationManager.GPS_PROVIDER)
                }

                if (_locationManager == null) _locationListener.onProviderNotSupported()
            } catch (e: SecurityException) {
                _locationListener.onProviderNotSupported()
            }
        }
    }

    @MainThread
    fun removeUpdates() {
        _mockLocationManager?.removeUpdates(_locationListener)
        _locationManager?.removeUpdates(_locationListener)
    }
}
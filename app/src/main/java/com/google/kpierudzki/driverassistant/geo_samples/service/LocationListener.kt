package com.google.kpierudzki.driverassistant.geo_samples.service

import android.location.LocationListener

/**
 * Created by Kamil on 09.01.2018.
 */
interface LocationListener : LocationListener {
    fun onProviderNotSupported()
}
package com.google.kpierudzki.driverassistant.service.manager

import android.support.annotation.MainThread
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager
import com.google.kpierudzki.driverassistant.service.connector.IConnectorSelectable
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType
import com.google.kpierudzki.driverassistant.service.helper.BackgroundThreadPoolHelper
import java.util.*

/**
 * Created by kamilpierudzki on 12/01/2018.
 */
open class BaseServiceManager(connectorTypes: Array<ManagerConnectorType>) : BackgroundThreadPoolHelper(), IConnectorSelectable {

    private var _connectorTypes: Array<ManagerConnectorType> = connectorTypes
    private var _firstListenerAdded: Boolean = false

    protected val listeners: ArrayList<IBaseManager.IBaseManagerListener> = ArrayList()

    override fun onDestroy() {
        super.onDestroy()
        synchronized(this) {
            listeners.clear()
        }
    }

    fun connectorMatches(connectorType: ManagerConnectorType): Boolean {
        return _connectorTypes.firstOrNull { it == connectorType } != null
    }

    override fun removeListener(connectorType: ManagerConnectorType, listener: IBaseManager.IBaseManagerListener) {
        synchronized(this) {
            val listenersToLeft = arrayListOf<IBaseManager.IBaseManagerListener>()
            listeners.forEach { if (it !== listener) listenersToLeft.add(it) }
            listeners.clear()
            listeners.addAll(listenersToLeft)

            if (listeners.isEmpty()) {
                _firstListenerAdded = false
                onLastListenerRemoved(listener)
            }
        }
    }

    override fun provideObservable(connectorType: ManagerConnectorType): IBaseManager.IBaseManagerObservable? {
        return null
    }

    @MainThread
    override fun addListener(connectorType: ManagerConnectorType, listener: IBaseManager.IBaseManagerListener) {
        synchronized(this) {
            listeners.add(listener)
            if (listeners.size == 1 && !_firstListenerAdded) {
                _firstListenerAdded = true
                onFirstListenerAdded(listener)
            }
        }
    }

    @MainThread
    open fun onFirstListenerAdded(listener: IBaseManager.IBaseManagerListener) {
        //Override
    }

    @MainThread
    open fun onLastListenerRemoved(listener: IBaseManager.IBaseManagerListener) {
        //Override
    }
}
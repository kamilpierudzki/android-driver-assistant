package com.google.kpierudzki.driverassistant.service.helper

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.google.kpierudzki.driverassistant.common.IDestroyable

/**
 * Created by Kamil on 22.01.2018.
 */
class BluetoothAdapterHelper(context: Context?, callbacks: Callbacks) : IDestroyable {

    private val _context: Context? = context
    private val _callbacks: Callbacks = callbacks
    private val _bluetoothReceiver: BroadcastReceiver

    init {
        _bluetoothReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    when (it.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        BluetoothAdapter.STATE_ON -> _callbacks.onBluetoothAdapterStateChanged(
                                BluetoothAdapterState.Enabled)
                        BluetoothAdapter.STATE_OFF -> _callbacks.onBluetoothAdapterStateChanged(
                                BluetoothAdapterState.Disabled)
                    }
                }
            }
        }

        try {
            context?.registerReceiver(_bluetoothReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        } catch (e: Exception) {
            Log.e("bluetooth", e.localizedMessage)
        }
    }

    override fun onDestroy() {
        try {
            _context?.unregisterReceiver(_bluetoothReceiver)
        } catch (e: Exception) {
            Log.e("bluetooth", e.localizedMessage)
        }
    }

    fun checkStateAndReturnAdapter(strictRefresh: Boolean): BluetoothAdapter? {
        synchronized(this) {
            var adapter: BluetoothAdapter? = null
            try {
                adapter = BluetoothAdapter.getDefaultAdapter() ?: throw SecurityException("")
                if (strictRefresh) {
                    if (adapter.isEnabled)
                        _callbacks.onBluetoothAdapterStateChanged(BluetoothAdapterState.Enabled)
                    else
                        _callbacks.onBluetoothAdapterStateChanged(BluetoothAdapterState.Disabled)
                }
            } catch (e: SecurityException) {
                if (strictRefresh) _callbacks.onBluetoothAdapterStateChanged(BluetoothAdapterState.NotSupported)
            }
            return adapter
        }
    }
}

interface Callbacks {
    fun onBluetoothAdapterStateChanged(state: BluetoothAdapterState)
}

enum class BluetoothAdapterState {
    Enabled, Disabled, NotSupported
}
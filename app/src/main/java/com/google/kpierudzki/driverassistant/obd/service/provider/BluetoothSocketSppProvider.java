package com.google.kpierudzki.driverassistant.obd.service.provider;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Kamil on 15.09.2017.
 */

public class BluetoothSocketSppProvider {

    public static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;

    @Deprecated
    public BluetoothSocketSppProvider() {
        mSocket = null;
        mDevice = null;
    }

    public BluetoothSocketSppProvider(BluetoothDevice device) {
        this.mDevice = device;
    }

    public void closeSocket() {
        if (mSocket != null) {
            try {
                mSocket.getInputStream().close();
            } catch (Exception e) {
                //Ignore
            }

            try {
                mSocket.getOutputStream().close();
            } catch (Exception e) {
                //Ignore
            }

            try {
                mSocket.close();
            } catch (Exception e) {
                //Ignore
            } finally {
                mSocket = null;
            }
        }
    }

    public BluetoothSocket getSocketNew() throws IOException {
        if (mSocket == null)
            mSocket = mDevice.createRfcommSocketToServiceRecord(SPP_UUID);
        return mSocket;
    }

    /**
     * @param device Ustawia nowe urządzenie.
     */

    @Deprecated
    public void setDevice(final BluetoothDevice device) {
        this.mDevice = device;
    }

    /**
     * @return Zwraca aktualne urządzenie.
     */
    @Deprecated
    public BluetoothDevice getDevice() {
        return mDevice;
    }
}

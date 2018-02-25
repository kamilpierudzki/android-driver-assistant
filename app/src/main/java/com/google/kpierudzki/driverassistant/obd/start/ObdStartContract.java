package com.google.kpierudzki.driverassistant.obd.start;

import android.bluetooth.BluetoothDevice;

import com.google.kpierudzki.driverassistant.BaseView;
import com.google.kpierudzki.driverassistant.common.BasePresenter;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;
import com.google.kpierudzki.driverassistant.obd.start.connector.IObdStartListener;

import java.util.Locale;

/**
 * Created by Kamil on 09.09.2017.
 */

public interface ObdStartContract {

    interface View extends BaseView<Presenter>, IObdStartListener {
        //...
    }

    interface Presenter extends BasePresenter, IObdStartListener {
        void provideBluetoothDevices();

        void connect(BluetoothDevice device, ObdProtocol protocol);

        void onPermissionGranted();
    }

    class BluetoothDeviceModel {

        public BluetoothDevice bluetoothDevice;

        public BluetoothDeviceModel(BluetoothDevice bluetoothDevice) {
            this.bluetoothDevice = bluetoothDevice;
        }

        @Override
        public String toString() {
            String format = "%s (%s)";
            if (bluetoothDevice != null)
                return String.format(Locale.getDefault(), format,
                        bluetoothDevice.getName(), bluetoothDevice.getAddress());
            else
                return String.format(Locale.getDefault(), format,
                        "Unknown", "00:00:00:00:00:00");
        }
    }
}

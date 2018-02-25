package com.google.kpierudzki.driverassistant.obd.start.view;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.kpierudzki.driverassistant.MainActivityFragmentsCallbacks;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.obd.common.DeviceMalfunctionDialog;
import com.google.kpierudzki.driverassistant.obd.connect.view.ObdConnectFragment;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;
import com.google.kpierudzki.driverassistant.obd.read.view.ObdReadFragment;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.start.ObdStartContract;
import com.google.kpierudzki.driverassistant.obd.start.ObdStartPresenter;
import com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterState;
import com.google.kpierudzki.driverassistant.util.preferences.Pref;
import com.google.kpierudzki.driverassistant.util.preferences.PrefItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import java8.util.J8Arrays;
import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 02.09.2017.
 */

public class ObdStartFragment extends Fragment implements ObdStartContract.View,
        ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {

    public static final String TAG = "ObdStartFragment_TAG";
    public final static int BLUETOOTH_REQUEST = 909;

    @BindView(R.id.device_spinner)
    Spinner deviceSpinner;

    @BindView(R.id.protocol_spinner)
    Spinner protocolSpinner;

    @BindView(R.id.connect_button)
    FloatingActionButton connectButton;

    @BindView(R.id.notification_label)
    TextView notificationLabel;

    @BindView(R.id.notification_container)
    ViewGroup notificationContainer;

    private View root;

    private MainActivityFragmentsCallbacks mainActivityFragmentsCallbacks;
    private ObdStartContract.Presenter presenter;
    private ObdManager.ConnectionState currentState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_obd_start, container, false);
        ButterKnife.bind(this, root);

        if (mainActivityFragmentsCallbacks != null)
            mainActivityFragmentsCallbacks.onFragmentLoaded
                    (MainActivityFragmentsCallbacks.LoadedFragment.ObdII);

        prepareAppBar(true);

        new ObdStartPresenter(this);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        prepareAppBar(false);
        presenter = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (presenter != null) presenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (presenter != null) presenter.stop();
    }

    public static ObdStartFragment newInstance() {
        return new ObdStartFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mainActivityFragmentsCallbacks = (MainActivityFragmentsCallbacks) getActivity();
        } catch (Exception e) {
            Log.e("CalendarFragment", e.getLocalizedMessage());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mainActivityFragmentsCallbacks = (MainActivityFragmentsCallbacks) getActivity();
        } catch (Exception e) {
            Log.e("CalendarFragment", e.getLocalizedMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mainActivityFragmentsCallbacks = null;
    }

    @Override
    public void setPresenter(ObdStartContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onPresenterReady(ObdStartContract.Presenter presenter) {
        if (presenter != null) presenter.provideBluetoothDevices();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == connectButton.getId()) {
            if (mainActivityFragmentsCallbacks != null) {
                BluetoothDevice device = ((ObdStartContract.BluetoothDeviceModel) deviceSpinner
                        .getAdapter().getItem(deviceSpinner.getSelectedItemPosition()))
                        .bluetoothDevice;
                ObdProtocol protocol = (ObdProtocol) protocolSpinner
                        .getAdapter().getItem(protocolSpinner.getSelectedItemPosition());

                if (currentState != ObdManager.ConnectionState.Connecting) {
                    if (presenter != null) presenter.connect(device, protocol);
                    Pref.setString(PrefItem.MAC_OF_LAST_BLUETOOTH_DEVICE, device.getAddress(), false);
                    Pref.setInt(PrefItem.LAST_OBD_PROTOCOL, protocol.protocolNumber, false);
                }
            }
        } else if (view.getId() == notificationContainer.getId()) {
            onBluetoothAdapterStateChanged(BluetoothAdapterState.NotSupported);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == BLUETOOTH_REQUEST && grantResults[0] >= 0) {
            if (presenter != null) presenter.onPermissionGranted();
        }
    }

    @Override
    public void onBluetoothAdapterStateChanged(@NonNull BluetoothAdapterState state) {
        switch (state) {
            case Enabled:
                notificationContainer.setVisibility(View.INVISIBLE);
                connectButton.setOnClickListener(this);
                if (presenter != null) presenter.provideBluetoothDevices();
                break;
            case Disabled:
                notificationContainer.setVisibility(View.VISIBLE);
                notificationContainer.setOnClickListener(null);
                notificationLabel.setText(R.string.BluetoothStatus_Label_NotEnabled);
                connectButton.setOnClickListener(view -> Snackbar.make(
                        root, R.string.Obd_Start_Button_Disabled, Snackbar.LENGTH_SHORT).show());
                break;
            case NotSupported:
                notificationContainer.setOnClickListener(this);
                notificationContainer.setVisibility(View.VISIBLE);
                notificationLabel.setText(R.string.BluetoothStatus_Label_NotSupported);

                connectButton.setOnClickListener(view -> Snackbar.make(
                        root, R.string.Obd_Start_Button_NotSupported, Snackbar.LENGTH_SHORT).show());
                Activity activity = getActivity();
                if (activity != null) {
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{Manifest.permission.BLUETOOTH},
                            BLUETOOTH_REQUEST);
                }
                break;
        }
    }

    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        this.currentState = state;
        if (getActivity() != null) {
            switch (state) {
                case Connecting:
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(this)
                            .replace(mainActivityFragmentsCallbacks.getMainFrameId(),
                                    ObdConnectFragment.newInstance(),
                                    ObdConnectFragment.TAG)
                            .commit();
                    break;
                case Connected:
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(this)
                            .replace(mainActivityFragmentsCallbacks.getMainFrameId(),
                                    ObdReadFragment.newInstance(),
                                    ObdReadFragment.TAG)
                            .commit();
            }
        }
    }

    @Override
    public void onDeviceMalfunction() {
        new DeviceMalfunctionDialog().show(getActivity().getSupportFragmentManager(), DeviceMalfunctionDialog.TAG);
    }

    @Override
    public void onDevicesLoaded(List<ObdStartContract.BluetoothDeviceModel> bluetoothDevices, ObdProtocol[] protocols) {
        if (getContext() != null) {
            ArrayAdapter<ObdStartContract.BluetoothDeviceModel> devicesAdapter = new ArrayAdapter<>(
                    getContext(), android.R.layout.simple_spinner_item, bluetoothDevices);
            devicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            deviceSpinner.setAdapter(devicesAdapter);

            ArrayAdapter<ObdProtocol> protocolsAdapter = new ArrayAdapter<>(
                    getContext(), android.R.layout.simple_spinner_item, protocols);
            protocolsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            protocolSpinner.setAdapter(protocolsAdapter);

            String lastMacAddress = Pref.getString(PrefItem.MAC_OF_LAST_BLUETOOTH_DEVICE, "");
            StreamSupport.stream(bluetoothDevices).filter(deviceModel ->
                    deviceModel.bluetoothDevice.getAddress().equals(lastMacAddress))
                    .findFirst().ifPresent(deviceModel ->
                    deviceSpinner.setSelection(devicesAdapter.getPosition(deviceModel)));

            int lastProtocolNumber = Pref.getInt(PrefItem.LAST_OBD_PROTOCOL, -1);
            J8Arrays.stream(protocols).filter(obdProtocol ->
                    obdProtocol.protocolNumber == lastProtocolNumber)
                    .findFirst().ifPresent(obdProtocol ->
                    protocolSpinner.setSelection(protocolsAdapter.getPosition(obdProtocol)));
        }
    }

    private void prepareAppBar(boolean initialize) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                if (initialize) {
                    supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                    supportActionBar.setTitle(R.string.OBD2_Name);
                } else {
                    supportActionBar.setDisplayOptions(0);
                }
            }
        }
    }
}

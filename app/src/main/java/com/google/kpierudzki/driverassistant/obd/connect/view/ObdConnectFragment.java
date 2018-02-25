package com.google.kpierudzki.driverassistant.obd.connect.view;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.kpierudzki.driverassistant.MainActivityFragmentsCallbacks;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.obd.common.DeviceMalfunctionDialog;
import com.google.kpierudzki.driverassistant.obd.connect.ObdConnectContract;
import com.google.kpierudzki.driverassistant.obd.connect.ObdConnectPresenter;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;
import com.google.kpierudzki.driverassistant.obd.read.view.ObdReadFragment;
import com.google.kpierudzki.driverassistant.obd.service.ObdManager;
import com.google.kpierudzki.driverassistant.obd.start.view.ObdStartFragment;
import com.google.kpierudzki.driverassistant.service.helper.BluetoothAdapterState;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 09.09.2017.
 */

public class ObdConnectFragment extends Fragment implements ObdConnectContract.View, View.OnClickListener {

    public final static String TAG = "ObdConnectFragment_TAG";

    @BindView(R.id.connection_status_label)
    TextView infoLabel;

    @BindView(R.id.device_connecting_to_label)
    TextView deviceConnectingTo;

    @BindView(R.id.obd_cancel_button)
    View cancelButton;

    @BindView(R.id.retry_icon)
    View retryIcon;

    @BindView(R.id.notification_label)
    TextView notificationLabel;

    @BindView(R.id.notification_container)
    ViewGroup notificationContainer;

    @BindView(R.id.progress)
    View progress;

    private View root;

    private MainActivityFragmentsCallbacks mainActivityFragmentsCallbacks;
    private ObdConnectContract.Presenter presenter;
    private BluetoothDevice currentBluetoothDevice;
    private ObdProtocol currentProtocol;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_obd_connect, container, false);
        ButterKnife.bind(this, root);

        if (mainActivityFragmentsCallbacks != null)
            mainActivityFragmentsCallbacks.onFragmentLoaded
                    (MainActivityFragmentsCallbacks.LoadedFragment.ObdII);

        cancelButton.setOnClickListener(this);
        retryIcon.setOnClickListener(this);

        prepareAppBar(true);

        new ObdConnectPresenter(this);
        return root;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        prepareAppBar(false);
        presenter = null;
    }

    public static ObdConnectFragment newInstance() {
        return new ObdConnectFragment();
    }

    @Override
    public void setPresenter(ObdConnectContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onPresenterReady(ObdConnectContract.Presenter presenter) {
        //Ignore
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mainActivityFragmentsCallbacks = (MainActivityFragmentsCallbacks) getActivity();
        } catch (Exception e) {
            Log.e("ObdConnectFragment", e.getLocalizedMessage());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mainActivityFragmentsCallbacks = (MainActivityFragmentsCallbacks) getActivity();
        } catch (Exception e) {
            Log.e("ObdConnectFragment", e.getLocalizedMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mainActivityFragmentsCallbacks = null;
    }

    @Override
    public void onConnectionInfo(String info) {
        infoLabel.setText(info);
    }

    @Override
    public void onDeviceMalfunction() {
        new DeviceMalfunctionDialog().show(getFragmentManager(), DeviceMalfunctionDialog.TAG);
    }

    @Override
    public void onConnectionStateChanged(ObdManager.ConnectionState state) {
        switch (state) {
            case Connected:
                getFragmentManager().beginTransaction()
                        .remove(this)
                        .replace(mainActivityFragmentsCallbacks.getMainFrameId(),
                                ObdReadFragment.newInstance(),
                                ObdReadFragment.TAG)
                        .commit();
                break;
            case Failed:
                Snackbar.make(root, R.string.Obd_Connect_Failed, Snackbar.LENGTH_SHORT).show();
                infoLabel.setText(R.string.Obd_Connect_Failed);
                retryIcon.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.INVISIBLE);

                getFragmentManager().beginTransaction()
                        .remove(this)
                        .replace(mainActivityFragmentsCallbacks.getMainFrameId(),
                                ObdStartFragment.newInstance(),
                                ObdStartFragment.TAG)
                        .commit();
                break;
            case Connecting:
                retryIcon.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                progress.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onConnectingDevice(@Nullable BluetoothDevice bluetoothDevice, @Nullable ObdProtocol protocol) {
        this.currentBluetoothDevice = bluetoothDevice;
        this.currentProtocol = protocol;

        if (bluetoothDevice != null && isAdded())
            deviceConnectingTo.setText(String.format(Locale.getDefault(),
                    getString(R.string.Obd_Connect_Device),
                    bluetoothDevice.getName(), bluetoothDevice.getAddress()));
    }

    @Override
    public void onClick(View view) {
        if (presenter != null) {
            if (view.getId() == cancelButton.getId())
                presenter.cancel();
            else if (view.getId() == retryIcon.getId() && currentBluetoothDevice != null &&
                    currentProtocol != null)
                presenter.connect(currentBluetoothDevice, currentProtocol);
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

    @Override
    public void onBluetoothAdapterStateChanged(@NotNull BluetoothAdapterState state) {
        switch (state) {
            case Disabled:
                notificationContainer.setVisibility(View.VISIBLE);
                notificationContainer.setOnClickListener(null);
                notificationLabel.setText(R.string.BluetoothStatus_Label_NotEnabled);
                break;
            case Enabled:
                notificationContainer.setVisibility(View.INVISIBLE);
                break;
            case NotSupported:
                notificationContainer.setOnClickListener(this);
                notificationContainer.setVisibility(View.VISIBLE);
                notificationLabel.setText(R.string.BluetoothStatus_Label_NotSupported);
                break;
        }
    }
}

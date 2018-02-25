package com.google.kpierudzki.driverassistant.service.helper;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.service.AssistantService;
import com.google.kpierudzki.driverassistant.common.IDestroyable;
import com.google.kpierudzki.driverassistant.service.connector.IConnectorSelectable;

/**
 * Created by Kamil on 28.06.2017.
 */

public class ServiceBindHelper implements ServiceConnection, IDestroyable {

    private Callback callback;
    private IConnectorSelectable serviceConnector;

    public ServiceBindHelper(@NonNull Callback callback) {
        super();
        this.callback = callback;
        App.getAppContext().bindService(new Intent(App.getAppContext(), AssistantService.class), this, 0);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        serviceConnector = ((AssistantService.ServiceConnector) service).getConnector();
        callback.onNewConnector(serviceConnector);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        serviceConnector = null;
    }

    @Nullable
    public IConnectorSelectable getServiceConnector() {
        return serviceConnector;
    }

    @Override
    public void onDestroy() {
        App.getAppContext().unbindService(this);
    }

    public interface Callback {
        void onNewConnector(IConnectorSelectable connector);
    }
}

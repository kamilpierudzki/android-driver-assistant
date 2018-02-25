package com.google.kpierudzki.driverassistant.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.background_work.service.AppInBackgroundReceiver;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;
import com.google.kpierudzki.driverassistant.service.connector.IConnectorSelectable;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.helper.AssistantServiceManagerHelper;

/**
 * Created by Kamil on 26.06.2017.
 */

public class AssistantService extends Service implements IConnectorSelectable {

    private AssistantServiceManagerHelper managerHelper;
    private AppInBackgroundReceiver backgroundWorkReceiver;
    private final ServiceConnector serviceConnector = new ServiceConnector();

    @Override
    public void onCreate() {
        super.onCreate();
        managerHelper = new AssistantServiceManagerHelper(this);
        backgroundWorkReceiver = new AppInBackgroundReceiver(App.getAppContext(), managerHelper);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        managerHelper.onDestroy();
        backgroundWorkReceiver.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceConnector;
    }

    @Override
    public void addListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManager.IBaseManagerListener listener) {
        managerHelper.addListener(connectorType, listener);
    }

    @Override
    public void removeListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManager.IBaseManagerListener listener) {
        managerHelper.removeListener(connectorType, listener);
    }

    @Nullable
    @Override
    public IBaseManager.IBaseManagerObservable provideObservable(@NonNull ManagerConnectorType connectorType) {
        return managerHelper.provideObservable(connectorType);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    public class ServiceConnector extends Binder {

        public IConnectorSelectable getConnector() {
            return AssistantService.this;
        }
    }
}

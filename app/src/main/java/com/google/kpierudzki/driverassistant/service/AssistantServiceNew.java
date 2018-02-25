package com.google.kpierudzki.driverassistant.service;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.background_work.service.AppInBackgroundReceiver;
import com.google.kpierudzki.driverassistant.common.IDestroyable;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;
import com.google.kpierudzki.driverassistant.service.connector.IConnectorSelectable;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.helper.AssistantServiceManagerHelper;

/**
 * Created by Kamil on 26.12.2017.
 */

public class AssistantServiceNew implements IDestroyable, IConnectorSelectable {

    private AssistantServiceManagerHelper managerHelper;
    private AppInBackgroundReceiver backgroundWorkReceiver;

    public AssistantServiceNew() {
        managerHelper = new AssistantServiceManagerHelper(App.getAppContext());
        backgroundWorkReceiver = new AppInBackgroundReceiver(App.getAppContext(), managerHelper);
    }

    @Override
    public void onDestroy() {
        managerHelper.onDestroy();
        backgroundWorkReceiver.onDestroy();
    }

    @MainThread
    @Override
    public void addListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManager.IBaseManagerListener listener) {
        managerHelper.addListener(connectorType, listener);
    }

    @MainThread
    @Override
    public void removeListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManager.IBaseManagerListener listener) {
        managerHelper.removeListener(connectorType, listener);
    }

    @MainThread
    @Nullable
    @Override
    public IBaseManager.IBaseManagerObservable provideObservable(@NonNull ManagerConnectorType connectorType) {
        return managerHelper.provideObservable(connectorType);
    }
}

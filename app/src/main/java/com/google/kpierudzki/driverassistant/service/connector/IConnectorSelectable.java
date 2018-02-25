package com.google.kpierudzki.driverassistant.service.connector;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Kamil on 27.06.2017.
 */

public interface IConnectorSelectable {

    @MainThread
    void addListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManager.IBaseManagerListener listener);

    @MainThread
    void removeListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManager.IBaseManagerListener listener);

    @Nullable
    IBaseManager.IBaseManagerObservable provideObservable(@NonNull ManagerConnectorType connectorType);
}

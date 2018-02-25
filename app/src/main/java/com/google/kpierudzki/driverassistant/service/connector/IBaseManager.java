package com.google.kpierudzki.driverassistant.service.connector;

/**
 * Created by Kamil on 16.07.2017.
 */

public interface IBaseManager {

    interface IBaseManagerListener extends IBaseManager {
        //...
    }

    interface IBaseManagerObservable extends IBaseManager {

    }
}

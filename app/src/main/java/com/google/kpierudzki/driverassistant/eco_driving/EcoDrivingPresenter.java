package com.google.kpierudzki.driverassistant.eco_driving;

import com.google.kpierudzki.driverassistant.eco_driving.connector.IEcoDrivingObservable;
import com.google.kpierudzki.driverassistant.eco_driving.usecase.EcoDrivingDbUseCase;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.helper.ServiceBindHelper;
import com.google.kpierudzki.driverassistant.util.MainThreadUtil;

import java.util.List;

/**
 * Created by Kamil on 25.06.2017.
 */

public class EcoDrivingPresenter implements EcoDrivingContract.Presenter, EcoDrivingDbUseCase.Callback {

    private EcoDrivingContract.View view;
    private ServiceBindHelper serviceBindHelper;
    private IEcoDrivingObservable ecoDrivingGpsObservable;
    private EcoDrivingDbUseCase dbUseCase;

    public EcoDrivingPresenter(EcoDrivingContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        dbUseCase = new EcoDrivingDbUseCase(this);
        bindConnector();
    }

    @Override
    public void stop() {
        unbindConnector();

        if (dbUseCase != null) {
            dbUseCase.onDestroy();
            dbUseCase = null;
        }
    }

    @Override
    public void provideLastNSamplesForParam(int N, EcoDrivingContract.EcoDrivingParameter parameter) {
        if (dbUseCase != null) dbUseCase.provideLastNSamplesForParam(N, parameter);
    }

    @Override
    public void onPermissionGranted() {
        if (ecoDrivingGpsObservable != null) ecoDrivingGpsObservable.onPermissionGranted();
    }

    @Override
    public void onAccelerationChanged(float acceleration) {
        MainThreadUtil.post(() -> {
            if (view != null) view.updateChart(acceleration);
        });
    }

    @Override
    public void onAvgScoreChanged(float score) {
        MainThreadUtil.post(() -> {
            if (view != null) view.updateScoreClock(score);
        });
    }

    @Override
    public void onSpeedChanged(float speed) {
        MainThreadUtil.post(() -> {
            if (view != null) view.updateSpeedClock(speed);
        });
    }

    @Override
    public void onGpsProviderStateChanged(IGeoSampleListener.GpsProviderState state) {
        MainThreadUtil.post(() -> {
            if (view != null) view.updateGpsState(state);
        });
    }

    @Override
    public void onDataProviderChanged(EcoDrivingContract.EcoDrivingDataProvider provider) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onDataProviderChanged(provider);
        });
    }

    @Override
    public void onLastDataOfParam(List<Float> data, EcoDrivingContract.EcoDrivingParameter parameter) {
        MainThreadUtil.post(() -> {
            if (view != null) view.onLastDataOfParam(data, parameter);
        });
    }

    private void bindConnector() {
        serviceBindHelper = new ServiceBindHelper(connector -> {
            connector.addListener(ManagerConnectorType.EcoDrivingGps, EcoDrivingPresenter.this);
            connector.addListener(ManagerConnectorType.EcoDrivingObd, EcoDrivingPresenter.this);
            ecoDrivingGpsObservable = (IEcoDrivingObservable) connector.provideObservable(ManagerConnectorType.EcoDrivingGps);
            if (view != null) view.onPresenterReady(this);
        });
    }

    private void unbindConnector() {
        if (serviceBindHelper != null) {
            if (serviceBindHelper.getServiceConnector() != null) {
                serviceBindHelper.getServiceConnector().removeListener(
                        ManagerConnectorType.EcoDrivingGps, EcoDrivingPresenter.this);
                serviceBindHelper.getServiceConnector().removeListener(
                        ManagerConnectorType.EcoDrivingObd, EcoDrivingPresenter.this);
            }
            serviceBindHelper.onDestroy();
            serviceBindHelper = null;
        }
    }
}

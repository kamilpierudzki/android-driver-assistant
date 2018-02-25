package com.google.kpierudzki.driverassistant.eco_driving;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.kpierudzki.driverassistant.BaseView;
import com.google.kpierudzki.driverassistant.common.BasePresenter;
import com.google.kpierudzki.driverassistant.eco_driving.connector.IEcoDrivingListener;
import com.google.kpierudzki.driverassistant.eco_driving.usecase.EcoDrivingDbUseCase;
import com.google.kpierudzki.driverassistant.geo_samples.connector.IGeoSampleListener;

/**
 * Created by Kamil on 25.06.2017.
 */

public interface EcoDrivingContract {

    interface View extends BaseView<Presenter>, EcoDrivingDbUseCase.Callback {
        void updateSpeedClock(float speed);

        void updateScoreClock(float score);

        void updateChart(float currentAcceleration);

        void updateGpsState(IGeoSampleListener.GpsProviderState state);

        void onDataProviderChanged(EcoDrivingDataProvider provider);
    }

    interface Presenter extends BasePresenter, IEcoDrivingListener {
        void provideLastNSamplesForParam(int N, EcoDrivingParameter parameter);

        void onPermissionGranted();
    }

    enum EcoDrivingDataProvider {
        Gps, Obd
    }

    enum EcoDrivingParameter {
        SCORE, SPEED, ACCELERATION;
    }

    class EcoDrivingInfo implements Parcelable {
        public float speed;
        public float currentAcceleration;
        public float avgScore;

        public EcoDrivingInfo(float speed, float currentAcceleration, float avgScore) {
            this.speed = speed;
            this.currentAcceleration = currentAcceleration;
            this.avgScore = avgScore;
        }

        public EcoDrivingInfo clone() {
            return new EcoDrivingInfo(speed, currentAcceleration, avgScore);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EcoDrivingInfo that = (EcoDrivingInfo) o;

            if (Float.compare(that.speed, speed) != 0) return false;
            if (Float.compare(that.currentAcceleration, currentAcceleration) != 0) return false;
            return Float.compare(that.avgScore, avgScore) == 0;

        }

        @Override
        public int hashCode() {
            int result = (speed != +0.0f ? Float.floatToIntBits(speed) : 0);
            result = 31 * result + (currentAcceleration != +0.0f ? Float.floatToIntBits(currentAcceleration) : 0);
            result = 31 * result + (avgScore != +0.0f ? Float.floatToIntBits(avgScore) : 0);
            return result;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(this.speed);
            dest.writeFloat(this.currentAcceleration);
            dest.writeFloat(this.avgScore);
        }

        protected EcoDrivingInfo(Parcel in) {
            this.speed = in.readFloat();
            this.currentAcceleration = in.readFloat();
            this.avgScore = in.readFloat();
        }

        public static final Parcelable.Creator<EcoDrivingInfo> CREATOR = new Parcelable.Creator<EcoDrivingInfo>() {
            @Override
            public EcoDrivingInfo createFromParcel(Parcel source) {
                return new EcoDrivingInfo(source);
            }

            @Override
            public EcoDrivingInfo[] newArray(int size) {
                return new EcoDrivingInfo[size];
            }
        };
    }
}

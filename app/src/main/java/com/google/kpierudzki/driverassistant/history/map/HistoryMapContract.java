package com.google.kpierudzki.driverassistant.history.map;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.kpierudzki.driverassistant.BaseView;
import com.google.kpierudzki.driverassistant.common.BasePresenter;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingEntity;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesEntity;
import com.google.kpierudzki.driverassistant.history.map.usecase.Callbacks;
import com.google.kpierudzki.driverassistant.obd.database.ObdParamsEntity;

/**
 * Created by Kamil on 02.08.2017.
 */

public interface HistoryMapContract {

    interface View extends BaseView<Presenter>, Callbacks {

    }

    interface Presenter extends BasePresenter {
        void provideMapData(long trackId);
    }

    class MapInfoModel implements Parcelable {
        public long trackId;
        public String startPoint;
        public String endPoint;
        public long startTimeInSec;

        public MapInfoModel(long trackId, String startPoint, String endPoint, long startTimeInSec) {
            this.trackId = trackId;
            this.startPoint = startPoint;
            this.endPoint = endPoint;
            this.startTimeInSec = startTimeInSec;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.trackId);
            dest.writeString(this.startPoint);
            dest.writeString(this.endPoint);
            dest.writeLong(this.startTimeInSec);
        }

        protected MapInfoModel(Parcel in) {
            this.trackId = in.readLong();
            this.startPoint = in.readString();
            this.endPoint = in.readString();
            this.startTimeInSec = in.readLong();
        }

        public static final Parcelable.Creator<MapInfoModel> CREATOR = new Parcelable.Creator<MapInfoModel>() {
            @Override
            public MapInfoModel createFromParcel(Parcel source) {
                return new MapInfoModel(source);
            }

            @Override
            public MapInfoModel[] newArray(int size) {
                return new MapInfoModel[size];
            }
        };
    }

    class MapData {
        public GeoSamplesEntity geoSamplesEntity;
        public EcoDrivingEntity ecoDrivingEntity;
        public ObdParamsEntity obdParamsEntity;

        public MapData(GeoSamplesEntity geoSamplesEntity) {
            this.geoSamplesEntity = geoSamplesEntity;
        }
    }
}

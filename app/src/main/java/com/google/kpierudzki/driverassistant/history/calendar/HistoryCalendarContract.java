package com.google.kpierudzki.driverassistant.history.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.kpierudzki.driverassistant.BaseView;
import com.google.kpierudzki.driverassistant.common.BasePresenter;
import com.google.kpierudzki.driverassistant.common.model.Coordinate;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesEntity;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesTracksEntity;
import com.google.kpierudzki.driverassistant.history.calendar.usecase.HistoryCalendarDbUseCase;
import com.google.kpierudzki.driverassistant.history.calendar.view.ScoreColor;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Kamil on 26.07.2017.
 */

public interface HistoryCalendarContract {

    interface View extends BaseView<Presenter>, HistoryCalendarDbUseCase.Callbacks {
        void onReverseGeocode(CalendarTranslateInfoModel trackInfo);
    }

    interface Presenter extends BasePresenter {
        void provideLastNTracks(int n);

        void provideAllTracksForCalendar();

        void provideTracksForDate(Calendar date);

        void translateCoordinates(List<CalendarTranslateInfoModel> models);

        void removeTracks(List<Long> ids);
    }

    class CalendarDbInfoModel {
        public GeoSamplesTracksEntity trackEntity;
        public GeoSamplesEntity sampleEntity;
        public float score;

        public CalendarDbInfoModel(GeoSamplesTracksEntity trackEntity, GeoSamplesEntity sampleEntity, float score) {
            this.trackEntity = trackEntity;
            this.sampleEntity = sampleEntity;
            this.score = score;
        }
    }

    class CalendarTranslateInfoModel implements Parcelable {
        public long trackId;
        public Coordinate startPoint;
        public String startPointTranslation;
        public Coordinate endPoint;
        public String endPointTranslation;
        public long startTimeInSec;
        public ScoreColor scoreColor;
        public float score;
        public boolean checkboxVisible;
        public boolean isSelected;

        public CalendarTranslateInfoModel(long trackId, Coordinate startPoint, String startPointTranslation, Coordinate endPoint, String endPointTranslation, long startTimeInSec, ScoreColor scoreColor, float score) {
            this.trackId = trackId;
            this.startPoint = startPoint;
            this.startPointTranslation = startPointTranslation;
            this.endPoint = endPoint;
            this.endPointTranslation = endPointTranslation;
            this.startTimeInSec = startTimeInSec;
            this.scoreColor = scoreColor;
            this.score = score;
        }

        public CalendarTranslateInfoModel(CalendarTranslateInfoModel model) {
            this.trackId = model.trackId;
            this.startPoint = model.startPoint;
            this.startPointTranslation = model.startPointTranslation;
            this.endPoint = model.endPoint;
            this.endPointTranslation = model.endPointTranslation;
            this.startTimeInSec = model.startTimeInSec;
            this.scoreColor = model.scoreColor;
            this.score = model.score;
            this.checkboxVisible = model.checkboxVisible;
            this.isSelected = model.isSelected;
        }

        @Override
        public CalendarTranslateInfoModel clone() {
            return new CalendarTranslateInfoModel(trackId, startPoint, startPointTranslation,
                    endPoint, endPointTranslation, startTimeInSec, scoreColor, score);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CalendarTranslateInfoModel that = (CalendarTranslateInfoModel) o;

            if (trackId != that.trackId) return false;
            if (startTimeInSec != that.startTimeInSec) return false;
            if (!startPoint.equals(that.startPoint)) return false;
            if (!startPointTranslation.equals(that.startPointTranslation)) return false;
            if (!endPoint.equals(that.endPoint)) return false;
            if (score != that.score) return false;
            if (scoreColor != that.scoreColor) return false;
            return endPointTranslation.equals(that.endPointTranslation);

        }

        @Override
        public int hashCode() {
            int result = (int) (trackId ^ (trackId >>> 32));
            result = 31 * result + startPoint.hashCode();
            result = 31 * result + startPointTranslation.hashCode();
            result = 31 * result + endPoint.hashCode();
            result = 31 * result + endPointTranslation.hashCode();
            result = 31 * result + (int) (startTimeInSec ^ (startTimeInSec >>> 32));
            result = 31 * result + scoreColor.hashCode();
            result = 31 * result + (score != +0.0f ? Float.floatToIntBits(score) : 0);
            return result;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.trackId);
            dest.writeParcelable(this.startPoint, flags);
            dest.writeString(this.startPointTranslation);
            dest.writeParcelable(this.endPoint, flags);
            dest.writeString(this.endPointTranslation);
            dest.writeLong(this.startTimeInSec);
            dest.writeInt(this.scoreColor == null ? -1 : this.scoreColor.ordinal());
            dest.writeFloat(this.score);
        }

        protected CalendarTranslateInfoModel(Parcel in) {
            this.trackId = in.readLong();
            this.startPoint = in.readParcelable(Coordinate.class.getClassLoader());
            this.startPointTranslation = in.readString();
            this.endPoint = in.readParcelable(Coordinate.class.getClassLoader());
            this.endPointTranslation = in.readString();
            this.startTimeInSec = in.readLong();
            int tmpScoreColor = in.readInt();
            this.scoreColor = tmpScoreColor == -1 ? null : ScoreColor.values()[tmpScoreColor];
            this.score = in.readFloat();
        }

        public static final Parcelable.Creator<CalendarTranslateInfoModel> CREATOR = new Parcelable.Creator<CalendarTranslateInfoModel>() {
            @Override
            public CalendarTranslateInfoModel createFromParcel(Parcel source) {
                return new CalendarTranslateInfoModel(source);
            }

            @Override
            public CalendarTranslateInfoModel[] newArray(int size) {
                return new CalendarTranslateInfoModel[size];
            }
        };
    }

}

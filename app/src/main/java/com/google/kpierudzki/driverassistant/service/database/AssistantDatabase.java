package com.google.kpierudzki.driverassistant.service.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.google.kpierudzki.driverassistant.debug.gps_recording.database.GpsProbeRecordingDao;
import com.google.kpierudzki.driverassistant.debug.gps_recording.database.GpsProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdAmbientAirTempProbeRecordingDao;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdAmbientAirTempProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdBarometricPressProbeRecordingDao;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdBarometricPressProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdCoolantTempProbeRecordingDao;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdCoolantTempProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdLoadProbeRecordingDao;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdLoadProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdMafProbeRecordingDao;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdMafProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdOilTempProbeRecordingDao;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdOilTempProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdRpmProbeRecordingDao;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdRpmProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdSpeedProbeRecordingDao;
import com.google.kpierudzki.driverassistant.debug.obd_recording.database.ObdSpeedProbeRecordingEntity;
import com.google.kpierudzki.driverassistant.dtc.database.DtcDao;
import com.google.kpierudzki.driverassistant.dtc.database.DtcEntity;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingDao;
import com.google.kpierudzki.driverassistant.eco_driving.database.EcoDrivingEntity;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesDao;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesEntity;
import com.google.kpierudzki.driverassistant.geo_samples.database.GeoSamplesTracksEntity;
import com.google.kpierudzki.driverassistant.history.calendar.database.HistoryCalendarDao;
import com.google.kpierudzki.driverassistant.history.calendar.database.HistoryTranslationEntity;
import com.google.kpierudzki.driverassistant.obd.database.ObdParamsDao;
import com.google.kpierudzki.driverassistant.obd.database.MruDao;
import com.google.kpierudzki.driverassistant.obd.database.MruEntity;
import com.google.kpierudzki.driverassistant.obd.database.ObdParamsEntity;

/**
 * Created by Kamil on 15.07.2017.
 */

@Database(entities =
        {
                EcoDrivingEntity.class,
                GeoSamplesTracksEntity.class,
                GeoSamplesEntity.class,
                HistoryTranslationEntity.class,
                MruEntity.class,
                ObdParamsEntity.class,
                GpsProbeRecordingEntity.class,
                ObdSpeedProbeRecordingEntity.class,
                ObdRpmProbeRecordingEntity.class,
                ObdMafProbeRecordingEntity.class,
                ObdCoolantTempProbeRecordingEntity.class,
                ObdLoadProbeRecordingEntity.class,
                ObdBarometricPressProbeRecordingEntity.class,
                ObdOilTempProbeRecordingEntity.class,
                ObdAmbientAirTempProbeRecordingEntity.class,
                DtcEntity.class
        },
        version = 1)

public abstract class AssistantDatabase extends RoomDatabase {

    public final static String DATABASE_FILENAME = "assistant_database";
    public final static String DEMO_DATABASE_FILENAME = "demo_assistant_database";

    public abstract EcoDrivingDao getEcoDrivingDao();

    public abstract GeoSamplesDao getGeoSamplesDao();

    public abstract HistoryCalendarDao getHistoryCalendarDao();

    public abstract MruDao getMruDao();

    public abstract ObdParamsDao getObdParamsDao();

    public abstract GpsProbeRecordingDao getGpsProbeRecordingDao();

    public abstract ObdSpeedProbeRecordingDao getObdSpeedProbeRecordingDao();

    public abstract ObdRpmProbeRecordingDao getObdRpmProbeRecordingDao();

    public abstract ObdMafProbeRecordingDao getObdMafProbeRecordingDao();

    public abstract ObdCoolantTempProbeRecordingDao getObdCoolantTempProbeRecordingDao();

    public abstract ObdLoadProbeRecordingDao getObdLoadProbeRecordingDao();

    public abstract ObdBarometricPressProbeRecordingDao getObdBarometricPressProbeRecordingDao();

    public abstract ObdOilTempProbeRecordingDao getObdOilTempProbeRecordingDao();

    public abstract ObdAmbientAirTempProbeRecordingDao getObdAmbientAirTempProbeRecordingDao();

    public abstract DtcDao getDtcDao();
}

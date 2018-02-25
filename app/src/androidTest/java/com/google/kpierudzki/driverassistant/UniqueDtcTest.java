package com.google.kpierudzki.driverassistant;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.kpierudzki.driverassistant.dtc.database.DtcDao;
import com.google.kpierudzki.driverassistant.dtc.database.DtcEntity;
import com.google.kpierudzki.driverassistant.dtc.datamodel.DtcModel;
import com.google.kpierudzki.driverassistant.service.database.AssistantDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import static junit.framework.Assert.assertTrue;

/**
 * Created by Kamil on 27.12.2017.
 */
@RunWith(AndroidJUnit4.class)
public class UniqueDtcTest {

    private AssistantDatabase mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AssistantDatabase.class).build();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void uniqueTest() {
        DtcDao dtcDao = mDb.getDtcDao();

        Set<DtcModel> set1 = new HashSet<>();
        set1.add(new DtcModel("P0011", "test123"));
        set1.add(new DtcModel("P0011", "test123"));
        dtcDao.addAll(transform(set1));

        Set<DtcModel> set2 = new HashSet<>();
        set2.add(new DtcModel("P0011", "test123"));
        set2.add(new DtcModel("P0011", "test123"));
        dtcDao.addAll(transform(set2));

        assertTrue(dtcDao.getEntities().size() == 1);
    }

    private Set<DtcEntity> transform(Set<DtcModel> set) {
        return StreamSupport.stream(set)
                .map(dtcModel -> new DtcEntity(dtcModel.code, dtcModel.desc))
                .collect(Collectors.toSet());
    }
}

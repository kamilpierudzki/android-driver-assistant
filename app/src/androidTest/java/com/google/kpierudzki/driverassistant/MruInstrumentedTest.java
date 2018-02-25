package com.google.kpierudzki.driverassistant;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.kpierudzki.driverassistant.obd.database.MruDao;
import com.google.kpierudzki.driverassistant.obd.database.MruEntity;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;
import com.google.kpierudzki.driverassistant.obd.service.provider.MruProtocolProvider;
import com.google.kpierudzki.driverassistant.service.database.AssistantDatabase;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

/**
 * Created by kamilpierudzki on 20/09/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MruInstrumentedTest {

    private AssistantDatabase mDb;
    private MruDao mMruDao;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AssistantDatabase.class).build();
        mMruDao = mDb.getMruDao();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void test0() {
        MruProtocolProvider mruProvider = new MruProtocolProvider(ObdProtocol.Auto, mMruDao);
        int tries = 60;
        for (int i = 0; i < tries; i++) {
            if (i > 0 && (tries % i == 0 || tries % i == 9 || tries % i == 13))
                mruProvider.notifySuccess();
            mruProvider.incProtocol();
        }

        int[] orders = new int[]{0, 1, 2, 5, 7, 3, 4, 6, 8, 9};
        mruProvider = new MruProtocolProvider(ObdProtocol.Auto, mMruDao);

        verifyOrder(orders, mruProvider.protocols);
        verifyIncrementation(orders, mruProvider);
    }

    private void verifyOrder(int[] order, List<MruEntity> protocols) {
        Assert.assertTrue(order.length == protocols.size());
        for (int i = 0; i < protocols.size(); i++)
            Assert.assertTrue(order[i] == protocols.get(i).getProtocolNumber());
    }

    private void verifyIncrementation(int[] orders, MruProtocolProvider mruProvider) {
        int ordersCounter = 0;
        for (int i = 0; i < 33; i++) {//provider uses MRU strategy
            Assert.assertTrue(mruProvider.getProtocol().protocolNumber == orders[ordersCounter]);
            if (++ordersCounter >= orders.length) ordersCounter = 0;
            mruProvider.incProtocol();
        }

        for (int i = 0; i < 33; i++) {//provider uses FIFO strategy
            Assert.assertTrue(mruProvider.getProtocol().protocolNumber == ordersCounter);
            if (++ordersCounter >= orders.length) ordersCounter = 0;
            mruProvider.incProtocol();
        }
    }
}

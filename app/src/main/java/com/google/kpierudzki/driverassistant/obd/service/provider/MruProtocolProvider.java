package com.google.kpierudzki.driverassistant.obd.service.provider;

import android.support.annotation.WorkerThread;

import com.google.common.collect.Iterators;
import com.google.kpierudzki.driverassistant.obd.database.MruDao;
import com.google.kpierudzki.driverassistant.obd.database.MruEntity;
import com.google.kpierudzki.driverassistant.obd.datamodel.ObdProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import java8.util.J8Arrays;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 27.09.2017.
 */

public class MruProtocolProvider {

    private static final int PROTOCOL_USAGE_LIMIT = 3;
    private int cyclesCount = 0;
    private int protocolsIterations = 0;

    private StandardProtocolNumberProvider standardProtocolProvider;
    public List<MruEntity> protocols;
    private ObdProtocol startProtocol;
    private MruDao mMruDao;
    private Iterator<MruEntity> protocolIter;
    private MruEntity protocol;
    private boolean useStandardProvider;

    @WorkerThread
    public MruProtocolProvider(ObdProtocol startProtocol, MruDao mruDao) {
        this.startProtocol = startProtocol;
        this.mMruDao = mruDao;

        protocols = new ArrayList<>(mMruDao.getMruProtocols());
        if (protocols.isEmpty()) {
            protocols.addAll(J8Arrays.stream(ObdProtocol.values()).map(obdProtocol ->
                    new MruEntity(obdProtocol.protocolNumber, 0)).collect(Collectors.toList()));
            Collections.sort(protocols, (t1, t2) -> t1.successCount - t2.successCount);
            syncDatabase();
        }

        standardProtocolProvider = new StandardProtocolNumberProvider(startProtocol.protocolNumber);
        useStandardProvider = !isAnyProtocolWithSuccess();
        protocolIter = Iterators.cycle(protocols);
        protocol = protocolIter.next();
    }

    private void syncDatabase() {
        mMruDao.syncMruProtocolsInDb(protocols);
    }

    private boolean isAnyProtocolWithSuccess() {
        return StreamSupport.stream(protocols).filter(pair -> pair.successCount > 0).findFirst().isPresent();
    }

    @WorkerThread
    public void incProtocol() {
        protocol = protocolIter.next();
        if (useStandardProvider)
            standardProtocolProvider.incrementProtocol();
        else {
            if (++protocolsIterations >= protocols.size()) {
                protocolsIterations = 0;
                if (++cyclesCount >= PROTOCOL_USAGE_LIMIT)
                    useStandardProvider = true;
            }
        }
    }

    @WorkerThread
    public ObdProtocol getProtocol() {
        if (useStandardProvider)
            return ObdProtocol.values()[standardProtocolProvider.getProtocol()];
        else {
            if (startProtocol == ObdProtocol.Auto)
                return ObdProtocol.values()[protocol.protocolNumber];
            else
                return startProtocol;
        }
    }

    @WorkerThread
    public void notifySuccess() {
        protocol.successCount++;
        syncDatabase();
    }
}

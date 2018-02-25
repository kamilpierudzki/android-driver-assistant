package com.google.kpierudzki.driverassistant.obd.service.provider.scheduler;

import android.support.annotation.NonNull;

import com.google.common.collect.Iterators;
import com.google.kpierudzki.driverassistant.obd.service.commandmodels.ObdCommandModel;
import com.google.kpierudzki.driverassistant.util.CommandsProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by Kamil on 05.10.2017.
 */

public class ObdCommandScheduler {

    private final static int COMMAND_COUNT_PER_SECOND = 6;

    private List<ObdCommandModel> scheduledCommands;
    private List<Integer> workerPlaces;
    private Iterator<ObdCommandModel> lowestPriorityCommandsIter;
    private List<ScheduleItem> commandsWithPriority;

    public ObdCommandScheduler() {
        commandsWithPriority = CommandsProvider.Companion.getSupportedCommands();
        commonConstructor();
    }

    public ObdCommandScheduler(List<ScheduleItem> commandsWithPriority) {
        this.commandsWithPriority = commandsWithPriority;
        commonConstructor();
    }

    private void commonConstructor() {
        Collections.sort(commandsWithPriority, (it1, it2) -> it2.getFrequency() - it1.getFrequency());

        scheduledCommands = new ArrayList<>(Collections.nCopies(COMMAND_COUNT_PER_SECOND, null));
        StreamSupport.stream(commandsWithPriority)
                .filter(item -> item.getFrequency() > 0)
                .forEach(this::applyStaticSchedule);

        workerPlaces = getIndicesOfEmptyPlaces(scheduledCommands);
        lowestPriorityCommandsIter = Iterators.cycle(StreamSupport.stream(commandsWithPriority)
                .filter(item -> item.getFrequency() == 0)
                .map(ScheduleItem::getCommand)
                .collect(Collectors.toList()));
    }

    private void applyStaticSchedule(ScheduleItem priorityCommand) {
        int interval = COMMAND_COUNT_PER_SECOND / priorityCommand.getFrequency();
        int firstEmpty = COMMAND_COUNT_PER_SECOND - 1;
        for (int i = 0; i < COMMAND_COUNT_PER_SECOND; i++)
            if (scheduledCommands.get(i) == null) {
                firstEmpty = i;
                break;
            }

        for (int i = firstEmpty; i < COMMAND_COUNT_PER_SECOND; i += interval)
            scheduledCommands.set(i, priorityCommand.getCommand());
    }

    private List<Integer> getIndicesOfEmptyPlaces(List<ObdCommandModel> scheduledCommands) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < scheduledCommands.size(); i++)
            if (scheduledCommands.get(i) == null) result.add(i);
        return result;
    }

    public void prepareCommands() {
        StreamSupport.stream(workerPlaces)
                .filter(integer -> integer < scheduledCommands.size())
                .forEach(integer -> scheduledCommands.set(integer, lowestPriorityCommandsIter.next()));
    }

    @NonNull
    public List<ObdCommandModel> getScheduledCommands() {
        return scheduledCommands;
    }

    @NonNull
    public List<ObdCommandModel> getAllCommands() {
        return StreamSupport.stream(commandsWithPriority)
                .map(ScheduleItem::getCommand)
                .collect(Collectors.toList());
    }
}

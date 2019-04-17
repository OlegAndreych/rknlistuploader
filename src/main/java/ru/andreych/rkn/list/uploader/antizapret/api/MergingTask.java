package ru.andreych.rkn.list.uploader.antizapret.api;

import inet.ipaddr.IPAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class MergingTask extends RecursiveTask<IPAddress[]> {

    private static final int THRESHOLD = 128;
    private final List<IPAddress> initialBlocks;

    public MergingTask(final IPAddress[] initialBlocks) {
        this.initialBlocks = Arrays.asList(initialBlocks);
    }

    public MergingTask(final List<IPAddress> initialBlocks) {
        this.initialBlocks = initialBlocks;
    }

    private Collection<MergingTask> createSubtasks() {
        final ArrayList<MergingTask> subtasks = new ArrayList<>(2);
        final int size = this.initialBlocks.size();
        final int halfSize = size / 2;

        subtasks.add(new MergingTask(this.initialBlocks.subList(0, halfSize)));
        subtasks.add(new MergingTask(this.initialBlocks.subList(halfSize, size)));

        return subtasks;
    }

    @Override
    protected IPAddress[] compute() {
        if (this.initialBlocks.size() > THRESHOLD) {

            final IPAddress[] premerged = ForkJoinTask.invokeAll(this.createSubtasks())
                    .stream()
                    .map(ForkJoinTask::join)
                    .flatMap(Arrays::stream).toArray(IPAddress[]::new);

            return this.process(premerged);
        } else {
            return this.process(this.initialBlocks.toArray(new IPAddress[0]));
        }
    }

    private IPAddress[] process(final IPAddress[] premerged) {
        if (premerged.length > 0) {
            return premerged[0].mergeToSequentialBlocks(premerged);
        }
        return new IPAddress[0];
    }
}

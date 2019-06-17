package ru.andreych.rkn.list.uploader.antizapret.api;

import inet.ipaddr.IPAddress;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.partition;

public class MergingTask extends RecursiveTask<IPAddress[]> {

    private static final int THRESHOLD = 10_000;
    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private final List<IPAddress> initialBlocks;

    public MergingTask(final IPAddress[] initialBlocks) {
        this.initialBlocks = Arrays.asList(initialBlocks);
    }

    private MergingTask(final List<IPAddress> initialBlocks) {
        this.initialBlocks = initialBlocks;
    }

    private Collection<MergingTask> createSubtasks() {
        final int size = this.initialBlocks.size();
        final int batchSize = size / THREADS;

        return partition(this.initialBlocks, batchSize)
                .stream()
                .map(MergingTask::new)
                .collect(Collectors.toList());
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

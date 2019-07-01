package ru.andreych.rkn.list.uploader.range;

import com.github.maltalex.ineter.base.IPv4Address;
import com.github.maltalex.ineter.range.IPv4Range;
import com.github.maltalex.ineter.range.IPv4Subnet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.ComparatorUtils.max;
import static org.apache.commons.collections4.ComparatorUtils.min;

public class IPRangesMerger {
    private static final Logger LOG = LogManager.getLogger(IPRangesMerger.class);

    public static List<IPv4Range> merge(@Nullable final Collection<IPv4Range> blockList) {

        if (blockList == null || blockList.isEmpty()) {
            return List.of();
        }

        LOG.info("Entries before merging: {}.", blockList.size());
        LOG.info("Merging addresses.");

        final long start = System.nanoTime();

        final ArrayList<IPv4Range> sortedRanges = blockList.stream()
                .sorted(Comparator.comparing(IPv4Range::getFirst).reversed())
                .collect(toCollection(ArrayList::new));

        int idx = 0;
        for (int i = 0; i < sortedRanges.size(); i++) {
            if (idx != 0
                    && sortedRanges.get(idx - 1).getFirst().compareTo(sortedRanges.get(i).getLast().next()) <= 0) {
                while (idx != 0
                        && sortedRanges.get(idx - 1).getFirst().compareTo(sortedRanges.get(i).getLast().next()) <= 0) {

                    final IPv4Address end = max(sortedRanges.get(idx - 1).getLast(),
                            sortedRanges.get(i).getLast(),
                            IPv4Address::compareTo);

                    final IPv4Address beginning = min(sortedRanges.get(idx - 1).getFirst(),
                            sortedRanges.get(i).getFirst(),
                            IPv4Address::compareTo);

                    sortedRanges.set(idx - 1, new IPv4Range(beginning, end));
                    idx--;
                }
            } else {
                sortedRanges.set(idx, sortedRanges.get(i));
            }
            idx++;
        }

        final List<IPv4Range> result = sortedRanges.subList(0, idx).stream()
                .flatMap(r -> {
                    final List<IPv4Subnet> iPv4Subnets = r.toSubnets();
                    if (iPv4Subnets.size() > 1) {
                        return Stream.of(r);
                    }
                    return iPv4Subnets.stream();
                })
                .collect(toList());

        final long stop = System.nanoTime();

        LOG.info("Merged addresses to ranges.");

        LOG.info("Entries after merging: {}.", result.size());
        LOG.info("Merged in {} seconds.", () -> ((double) (stop - start)) / 1_000_000_000);

        return result;
    }
}

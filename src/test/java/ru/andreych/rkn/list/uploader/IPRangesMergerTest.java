package ru.andreych.rkn.list.uploader;

import com.github.maltalex.ineter.range.IPv4Range;
import org.junit.jupiter.api.Test;
import ru.andreych.rkn.list.uploader.range.IPRangesMerger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IPRangesMergerTest {

    @Test
    void mergeAdjacent() {
        final List<IPv4Range> merge = IPRangesMerger.merge(List.of(IPv4Range.of("127.0.0.1"), IPv4Range.of("127.0.0.2")));
        assertEquals(1, merge.size());
        assertEquals(IPv4Range.of("127.0.0.1", "127.0.0.2"), merge.get(0));
    }
}
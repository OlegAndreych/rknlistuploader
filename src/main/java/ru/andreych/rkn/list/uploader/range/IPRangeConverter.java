package ru.andreych.rkn.list.uploader.range;

import com.github.maltalex.ineter.base.IPv4Address;
import com.github.maltalex.ineter.range.IPv4Range;
import com.github.maltalex.ineter.range.IPv4Subnet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class IPRangeConverter {
    private static final Logger LOG = LogManager.getLogger(IPRangeConverter.class);

    public static List<IPv4Range> parseRanges(final List<String> addressStrings) {

        LOG.info("Converting address strings to IP ranges.");

        final List<IPv4Range> ipAddresses = addressStrings.stream()
                .map(IPRangeConverter::tryRange)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        LOG.info("Address strings has been converted to IP ranges.");

        return ipAddresses;
    }

    public static Set<String> toRangesStrings(final Collection<IPv4Range> ranges) {
        return ranges.stream()
                .map(address -> {
                    if (address instanceof IPv4Subnet) {
                        return address.toString();
                    }
                    final IPv4Address first = address.getFirst();
                    final IPv4Address last = address.getLast();
                    return first.equals(last)
                            ? first.toString()
                            : first.toString() + '-' + last.toString();
                })
                .collect(toSet());
    }

    private static Optional<IPv4Range> tryRange(final String addressString) {
        try {
            return Optional.of(IPv4Range.parse(addressString));
        } catch (final Exception e) {
            LOG.debug("Well, {} is not an IPv4 range.", addressString);
            return Optional.empty();
        }
    }
}

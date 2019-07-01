package ru.andreych.rkn.list.uploader.main;

import com.beust.jcommander.JCommander;
import com.github.maltalex.ineter.range.IPv4Range;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.andreych.rkn.list.uploader.antizapret.api.AntizapretClient;
import ru.andreych.rkn.list.uploader.config.Args;
import ru.andreych.rkn.list.uploader.hash.HashCalculator;
import ru.andreych.rkn.list.uploader.hash.HashStorage;
import ru.andreych.rkn.list.uploader.mikrotik.MikrotikConnector;
import ru.andreych.rkn.list.uploader.mikrotik.MikrotikResultToSet;
import ru.andreych.rkn.list.uploader.range.IPRangeConverter;
import ru.andreych.rkn.list.uploader.range.IPRangesMerger;

import java.util.*;

public class Main {
    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(final String[] args) throws Exception {

        final Args parsedArgs = new Args();

        final JCommander jCommander = JCommander.newBuilder()
                .addObject(parsedArgs)
                .programName("rknlistuploader")
                .build();

        jCommander
                .parse(args);

        if (parsedArgs.isHelp()) {
            jCommander.usage();
            return;
        }

        LOG.debug(parsedArgs);

        final String apiUrl = parsedArgs.getApiUrl();
        final Long connectionTimeout = parsedArgs.getAntizapretConnectionTimeout();
        final Long readTimeout = parsedArgs.getAntizapretConnectionSocketReadTimeout();
        final Long writeTimeout = parsedArgs.getAntizapretConnectionSocketWriteTimeout();
        final AntizapretClient client = new AntizapretClient(apiUrl, connectionTimeout, readTimeout, writeTimeout);
        List<String> addressStrings = client.getAddressStrings();
        client.dispose();

        final byte[] hash = HashCalculator.calculate(addressStrings);
        final HashStorage hashStorage = new HashStorage(parsedArgs.getHashFilePath());

        if (Arrays.equals(hash, hashStorage.getHash())) {
            LOG.info("Addresses hashes are equal. I guess there's nothing to do.");
            return;
        }

        final List<IPv4Range> unmergedRanges = IPRangeConverter.parseRanges(addressStrings);
        //noinspection UnusedAssignment
        addressStrings = null;
        final List<IPv4Range> ranges = IPRangesMerger.merge(unmergedRanges);
        final Set<String> blockList = IPRangeConverter.toRangesStrings(ranges);

        LOG.debug(blockList);

        try (final MikrotikConnector mikrotikConnector = new MikrotikConnector(
                parsedArgs.getRouterAddress(),
                parsedArgs.getPort(),
                parsedArgs.getLogin(),
                parsedArgs.getPassword(),
                parsedArgs.getMikrotikTimeout())) {

            List<Map<String, String>> listContent = mikrotikConnector.getListContent(parsedArgs.getListName());

            LOG.debug(listContent);

            final Map<String, String> addressMap = MikrotikResultToSet.convert(listContent);
            //noinspection UnusedAssignment
            listContent = null;

            LOG.debug(addressMap);

            Map<String, String> removed = new HashMap<>(addressMap);
            removed.keySet().removeAll(blockList);

            LOG.info("{} items to remove has been calculated", removed.size());
            LOG.debug("To remove:\n{}", removed);

            mikrotikConnector.removeAddresses(removed.values());
            //noinspection UnusedAssignment
            removed = null;

            HashSet<String> added = new HashSet<>(blockList);
            added.removeAll(addressMap.keySet());

            LOG.info("{} items to add has been calculated", added.size());
            LOG.debug("To add:\n{}", added);

            LOG.info("Addresses has been deleted");

            mikrotikConnector.addAddresses(added, parsedArgs.getListName());
            //noinspection UnusedAssignment
            added = null;

            LOG.info("Addresses has been added");
        }

        hashStorage.storeHash(hash);
    }
}

package ru.andreych.rkn.list.uploader.main;

import com.beust.jcommander.JCommander;
import inet.ipaddr.IPAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.andreych.rkn.list.uploader.antizapret.api.AntizapretClient;
import ru.andreych.rkn.list.uploader.config.Args;
import ru.andreych.rkn.list.uploader.mikrotik.MikrotikConnector;
import ru.andreych.rkn.list.uploader.mikrotik.MikrotikResultToSet;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.IteratorUtils.toArray;

public class Main {

    private static final Logger LOG = LogManager.getLogger();

    public static void main(final String[] args) throws Exception {

        final long start = System.nanoTime();

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

        try (final MikrotikConnector mikrotikConnector = new MikrotikConnector(
                parsedArgs.getRouterAddress(),
                parsedArgs.getPort(),
                parsedArgs.getLogin(),
                parsedArgs.getPassword(),
                parsedArgs.getMikrotikTimeout())) {

            final List<Map<String, String>> listContent = mikrotikConnector.getListContent(parsedArgs.getListName());

            LOG.debug(listContent);

            final Map<String, String> addressMap = MikrotikResultToSet.convert(listContent);

            LOG.debug(addressMap);

            final Set<IPAddress> blockList = new AntizapretClient(parsedArgs.getApiUrl()).getBlockList();

            LOG.debug(blockList);

            final IPAddress[] blocks;
            if (!blockList.isEmpty()) {
                final Iterator<IPAddress> iterator = blockList.iterator();
                final IPAddress head = iterator.next();
                final IPAddress[] tail = toArray(iterator, IPAddress.class);
                blocks = head.mergeToSequentialBlocks(tail);
            } else {
                blocks = new IPAddress[0];
            }

            final long stop = System.nanoTime();
            LOG.info("Entries before merging: {}", blockList.size());
            LOG.info("Entries after merging: {}", blocks.length);
            LOG.info("Merged in {} seconds.", () -> ((double) (stop - start)) / 1_000_000_000);

            final Set<String> mergedList = Stream.of(blocks)
                    .map(address -> {
                        if (address.isMultiple()) {
                            return address.getLower().toString() + '-' + address.getUpper().toString();
                        } else {
                            return address.toString();
                        }
                    })
                    .collect(toSet());

            final Map<String, String> removed = new HashMap<>(addressMap);
            removed.keySet().removeAll(mergedList);

            LOG.info("{} items to remove has been calculated", removed.size());
            LOG.debug("To remove:\n{}", removed);

            final HashSet<String> added = new HashSet<>(mergedList);
            added.removeAll(addressMap.keySet());

            LOG.info("{} items to add has been calculated", added.size());
            LOG.debug("To add:\n{}", added);

            mikrotikConnector.removeAddresses(removed.values());

            LOG.info("Addresses has been deleted");

            mikrotikConnector.addAddresses(added, parsedArgs.getListName());

            LOG.info("Addresses has been added");
        }
    }
}

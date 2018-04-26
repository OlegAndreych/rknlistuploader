package ru.andreych.rkn.list.uploader.main;

import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.andreych.rkn.list.uploader.antizapret.api.AntizapretClient;
import ru.andreych.rkn.list.uploader.config.Args;
import ru.andreych.rkn.list.uploader.mikrotik.MikrotikConnector;
import ru.andreych.rkn.list.uploader.mikrotik.MikrotikResultToSet;

import java.util.*;

public class Main {

    private static final Logger LOG = LogManager.getLogger();

    public static void main(String[] args) throws Exception {

        final var parsedArgs = new Args();

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
                parsedArgs.getPassword())) {

            final List<Map<String, String>> listContent = mikrotikConnector.getListContent(parsedArgs.getListName());

            LOG.debug(listContent);

            final Map<String, String> addressMap = MikrotikResultToSet.convert(listContent);

            LOG.debug(addressMap);

            final Set<String> blockList = new AntizapretClient(parsedArgs.getApiUrl()).getBlockList();

            LOG.debug(blockList);

            final Map<String, String> removed = new HashMap<>(addressMap);
            removed.keySet().removeAll(blockList);

            LOG.info("{} items to remove has been calculated", removed.size());
            LOG.debug("To remove:\n{}", removed);

            final HashSet<String> added = new HashSet<>(blockList);
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

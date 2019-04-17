package ru.andreych.rkn.list.uploader.antizapret.api;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class AntizapretClient {

    private static final Logger LOG = LogManager.getLogger();

    private final OkHttpClient client = new OkHttpClient();
    private final Request request;

    public AntizapretClient(final String listAddress) {
        this.request = new Request.Builder()
                .url(listAddress)
                .get()
                .build();
    }

    public Set<String> getBlockList() throws IOException {

        final IPAddress[] blocks;

        LOG.info("Retrieving raw blocked addresses from Antizapret.");
        try (final Response response = this.client.newCall(this.request).execute()) {

            blocks = this.getBlocks(response);
        }
        return Stream.of(blocks)
                .map(address -> {
                    if (address.isSinglePrefixBlock()) {
                        return address.toPrefixLengthString();
                    } else if (address.isMultiple()) {
                        return address.getLower().toString() + '-' + address.getUpper().toString();
                    } else {
                        return address.toString();
                    }
                })
                .collect(toSet());

    }


    private IPAddress[] getBlocks(final Response response) throws IOException {

        final long start = System.nanoTime();

        final ResponseBody responseBody = response.body();
        final IPAddress[] blockList = this.getBlockList(responseBody);
        final int blockListLength = blockList.length;

        LOG.info("Entries before merging: {}.", blockListLength);
        LOG.info("Merging addresses.");

        final IPAddress[] blocks;
        if (blockListLength > 0) {
            /*final IPAddress head = blockList[0];
            blocks = head.mergeToSequentialBlocks(blockList);*/

            final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
            blocks = forkJoinPool.invoke(new MergingTask(blockList));

        } else {
            blocks = new IPAddress[0];
        }

        LOG.info("Merged addresses to ranges.");
        LOG.info("Merging to prefix blocks.");

        final IPAddress[] prefixesAndBlocks = Arrays.stream(blocks)
                .map(b -> b.mergeToPrefixBlocks(b))
                .flatMap(Arrays::stream)
                .toArray(IPAddress[]::new);

        LOG.info("Merged to prefix blocks.");

        final long stop = System.nanoTime();
        LOG.info("Entries after merging: {}.", prefixesAndBlocks.length);
        LOG.info("Merged in {} seconds.", () -> ((double) (stop - start)) / 1_000_000_000);
        return prefixesAndBlocks;
    }

    private IPAddress[] getBlockList(final ResponseBody responseBody) throws IOException {

        if (responseBody == null) {
            return new IPAddress[0];
        }

        LOG.info("Parsing raw addresses data from Antizapret.");

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseBody.byteStream()))) {

            final IPAddress[] ipAddresses = bufferedReader.lines()
                    .flatMap(l -> Arrays.stream(l.split(",")))
                    .filter(Objects::nonNull)
                    .filter(i -> !i.isEmpty())
                    .map(IPAddressString::new)
                    .filter(IPAddressString::isIPv4)
                    .map(IPAddressString::getAddress)
                    .toArray(IPAddress[]::new);

            LOG.info("Raw data has been parsed.");

            return ipAddresses;
        }
    }
}

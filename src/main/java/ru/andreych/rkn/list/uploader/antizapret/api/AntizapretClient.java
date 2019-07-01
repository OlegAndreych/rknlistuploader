package ru.andreych.rkn.list.uploader.antizapret.api;

import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.stream;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;

public class AntizapretClient {

    private static final Logger LOG = LogManager.getLogger(AntizapretClient.class);

    private final OkHttpClient client;
    private final Request request;

    public AntizapretClient(
            final String listAddress,
            final long connectionTimeout,
            final long readTimeout,
            final long writeTimeout) {

        this.client = new OkHttpClient.Builder()
                .connectTimeout(connectionTimeout, MILLISECONDS)
                .readTimeout(readTimeout, MILLISECONDS)
                .writeTimeout(writeTimeout, MILLISECONDS)
                .build();

        this.request = new Request.Builder()
                .url(listAddress)
                .get()
                .build();
    }

    public List<String> getAddressStrings() throws IOException {

        LOG.info("Retrieving raw blocked addresses from Antizapret.");
        try (final Response response = this.client.newCall(this.request).execute()) {
            final List<String> addressStrings = this.getAddressStrings(response.body());
            LOG.info("Retrieved raw blocked addresses from Antizapret.");
            return addressStrings;
        }
    }

    public void dispose() {
        final Dispatcher dispatcher = this.client.dispatcher();
        dispatcher.cancelAll();
        dispatcher.executorService().shutdownNow();
        this.client.connectionPool().evictAll();
    }

    private List<String> getAddressStrings(final ResponseBody responseBody) throws IOException {

        if (responseBody == null) {
            return List.of();
        }

        LOG.info("Parsing raw addresses data from Antizapret.");

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseBody.byteStream()))) {
            final List<String> addressStrings = bufferedReader.lines()
                    .flatMap(l -> stream(l.split(",")))
                    .filter(Objects::nonNull)
                    .filter(i -> !i.isEmpty())
                    .collect(toList());

            LOG.info("Raw data has been parsed.");

            return addressStrings;
        }
    }
}

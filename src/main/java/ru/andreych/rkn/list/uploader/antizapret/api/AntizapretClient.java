package ru.andreych.rkn.list.uploader.antizapret.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

public class AntizapretClient {

    private static final Logger LOG = LogManager.getLogger();
    private static final Predicate<String> SUBNET_PREDICATE = Pattern
            .compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(/\\d{1,2})?")
            .asPredicate();

    private final OkHttpClient client = new OkHttpClient();
    private final Request request;

    public AntizapretClient(final String listAddress) {
        this.request = new Request.Builder()
                .url(listAddress)
                .get()
                .build();
    }

    public Set<String> getBlockList() throws IOException {
        final Response response = this.client.newCall(this.request).execute();

        if (response.body() != null) {
            return new BufferedReader(new InputStreamReader(response.body().byteStream())).lines()
                    .flatMap(l -> Arrays.stream(l.split(",")))
                    .filter(Objects::nonNull)
                    .filter(i -> !i.isEmpty())
                    .filter(SUBNET_PREDICATE)
                    .collect(toSet());
        } else {
            LOG.warn("We have an empty response body here.");
            return emptySet();
        }
    }
}

package ru.andreych.rkn.list.uploader.antizapret.api;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toSet;

public class AntizapretClient {

    private static final Pattern subnetRegex = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(\\/\\d{1,2})?");

    private final HttpRequest.Builder template;
    private final HttpClient client = HttpClient
            .newBuilder()
            .build();

    public AntizapretClient(String listAddress) throws URISyntaxException {
        this.template = HttpRequest
                .newBuilder(new URI(listAddress))
                .GET();
    }

    public Set<String> getBlockList() throws IOException, InterruptedException {
        final HttpResponse<InputStream> response = client.send(template.build(), HttpResponse.BodyHandler.asInputStream());
        final Predicate<String> subnetValidator = subnetRegex.asPredicate();

        return new BufferedReader(new InputStreamReader(response.body())).lines()
                .flatMap(l -> Arrays.stream(l.split(",")))
                .filter(Objects::nonNull)
                .filter(i -> !i.isEmpty())
                .filter(subnetValidator)
                .collect(toSet());
    }
}

package ru.andreych.rkn.list.uploader.mikrotik;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MikrotikResultToSet {

    public static Map<String, String> convert(List<Map<String, String>> maps) {
        return maps.stream()
                .map(m -> new String[]{m.get("address"), m.get(".id")})
                .collect(Collectors.toMap(i -> i[0], i -> i[1]));
    }
}

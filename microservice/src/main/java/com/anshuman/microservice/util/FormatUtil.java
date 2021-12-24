package com.anshuman.microservice.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@UtilityClass
public class FormatUtil {

    public static String humanReadableTime(Duration d) {
        return d.toMinutesPart() + "m:" + d.toSecondsPart() + "s:" + d.toMillisPart() + "ms" + d.toNanosPart() + "ns";
    }

    public static <K, V> String stringifyMap(Map<K, V> map) {
        return Optional.ofNullable(map)
                .map(Map::entrySet)
                .filter(Predicate.not(Set::isEmpty))
                .map(entrySet -> entrySet.stream().map(entry -> entry.getKey() + ":" + entry.getValue())
                        .collect(Collectors.joining(", ")))
                .orElse("n/a");
    }
}

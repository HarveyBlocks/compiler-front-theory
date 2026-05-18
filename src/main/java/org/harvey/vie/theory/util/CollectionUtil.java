package org.harvey.vie.theory.util;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 23:14
 */
public class CollectionUtil {
    private CollectionUtil() {}

    public static <T> Map<T, Integer> dict(T[] array) {
        return IntStream.range(0, array.length).boxed().collect(Collectors.toMap(i -> array[i], i -> i));
    }

    public static <T> int validIndex(Map<T, Integer> dict, T element) {
        Integer index = dict.get(element);
        if (index == null) {
            throw new IllegalArgumentException("element is not exist in dict");
        }
        return index;
    }

    public static <K, V extends Enum<V>> Map<K, V> dictOnEnum(V[] enums, Function<V, K> mapping) {
        return Arrays.stream(enums).collect(Collectors.toMap(mapping, v -> v));
    }
}

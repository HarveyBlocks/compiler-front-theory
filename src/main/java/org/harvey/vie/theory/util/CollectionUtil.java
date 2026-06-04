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
    /**
     * 函数功能：阻止创建集合工具类实例。
     * 输入：
     * - 无。
     * 输出：无。
     */
    private CollectionUtil() {}

    /**
     * 函数功能：根据数组元素创建元素到索引的映射。
     * 输入：
     * - array：待转换的数组。
     * 输出：元素到索引的 Map。
     */
    public static <T> Map<T, Integer> dict(T[] array) {
        return IntStream.range(0, array.length).boxed().collect(Collectors.toMap(i -> array[i], i -> i));
    }

    /**
     * 函数功能：从映射中获取元素对应的有效索引。
     * 输入：
     * - dict：元素到索引的映射。
     * - element：待查找索引的元素。
     * 输出：元素对应的索引整数。
     */
    public static <T> int validIndex(Map<T, Integer> dict, T element) {
        Integer index = dict.get(element);
        if (index == null) {
            throw new IllegalArgumentException("element is not exist in dict");
        }
        return index;
    }

    /**
     * 函数功能：根据枚举数组和键映射函数创建键到枚举值的映射。
     * 输入：
     * - enums：待转换的枚举值数组。
     * - mapping：从枚举值生成键的函数。
     * 输出：键到枚举值的 Map。
     */
    public static <K, V extends Enum<V>> Map<K, V> dictOnEnum(V[] enums, Function<V, K> mapping) {
        return Arrays.stream(enums).collect(Collectors.toMap(mapping, v -> v));
    }
}

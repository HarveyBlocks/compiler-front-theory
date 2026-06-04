package org.harvey.vie.theory.lexical;

import org.harvey.vie.theory.util.SimpleCollection;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-13 12:06
 */
public class RegexCharSet implements SimpleCollection<String> {
    private final Set<String> set;


    /**
     * 函数功能：创建正则字符集合。
     * 输入：
     * - set：用于初始化的字符串集合。
     * 输出：无。
     */
    public RegexCharSet(Set<String> set) {
        this.set = set;
    }

    /**
     * 函数功能：合并多个正则字符集合。
     * 输入：
     * - set：待合并的正则字符集合数组。
     * 输出：合并后的 RegexCharSet。
     */
    public static RegexCharSet unionAll(RegexCharSet... set) {
        return new RegexCharSet(Stream.of(set)
                .flatMap(SimpleCollection::stream)
                .collect(Collectors.toSet()));
    }

    /**
     * 函数功能：获取正则字符集合的正则字符串表示。
     * 输入：
     * - 无。
     * 输出：正则字符集合字符串。
     */
    @Override
    public String toString() {
        return set.stream().collect(Collectors.joining("|", "(", ")"));
    }

    /**
     * 函数功能：按给定元素创建正则字符集合。
     * 输入：
     * - ele：用于创建集合的字符串元素数组。
     * 输出：创建得到的 RegexCharSet。
     */
    public static RegexCharSet of(String... ele) {
        return new RegexCharSet(Set.of(ele));
    }

    /**
     * 函数功能：获取正则字符集合的元素数量。
     * 输入：
     * - 无。
     * 输出：元素数量整数。
     */
    @Override
    public int size() {
        return set.size();
    }

    /**
     * 函数功能：获取正则字符集合的元素迭代器。
     * 输入：
     * - 无。
     * 输出：字符串元素 Iterator。
     */
    @Override
    public Iterator<String> iterator() {
        return set.iterator();
    }

    /**
     * 函数功能：创建排除指定元素后的正则字符集合。
     * 输入：
     * - s：需要排除的字符串元素数组。
     * 输出：排除指定元素后的 RegexCharSet。
     */
    public RegexCharSet exclude(String... s) {
        Set<String> exclusive = Set.of(s);
        return new RegexCharSet(this.set.stream().filter(Predicate.not(exclusive::contains)).collect(Collectors.toSet()));
    }
}

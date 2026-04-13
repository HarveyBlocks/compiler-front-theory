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


    public RegexCharSet(Set<String> set) {
        this.set = set;
    }

    public static RegexCharSet unionAll(RegexCharSet... set) {
        return new RegexCharSet(Stream.of(set)
                .flatMap(SimpleCollection::stream)
                .collect(Collectors.toSet()));
    }

    @Override
    public String toString() {
        return set.stream().collect(Collectors.joining("|", "(", ")"));
    }

    public static RegexCharSet of(String... ele) {
        return new RegexCharSet(Set.of(ele));
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public Iterator<String> iterator() {
        return set.iterator();
    }

    public RegexCharSet exclude(String... s) {
        Set<String> exclusive = Set.of(s);
        return new RegexCharSet(this.set.stream().filter(Predicate.not(exclusive::contains)).collect(Collectors.toSet()));
    }
}

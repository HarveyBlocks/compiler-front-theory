package org.harvey.vie.theory.lexical.regex.node;

/**
 * A {@link RegexNode} representing an empty string (epsilon). It matches
 * without consuming any input.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:24
 */
public class EpsilonRegexNode implements RegexNode {
    @Override
    public String toString() {
        return "ε";
    }
}

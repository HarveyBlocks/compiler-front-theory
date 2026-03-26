package org.harvey.vie.theory.lexical.regex.node;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A {@link RegexNode} representing the Kleene closure of a regular expression
 * pattern. It matches the sub-pattern zero or more times.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:24
 */
@Getter
@AllArgsConstructor
public class ClosureRegexNode implements RegexNode {
    private final RegexNode child;

    @Override
    public String toString() {
        return "(" + child + ")*";
    }
}

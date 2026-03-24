package org.harvey.vie.theory.lexical.regex.node;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO
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

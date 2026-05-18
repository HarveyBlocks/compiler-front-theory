package org.harvey.vie.theory.lexical.regex.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A {@link RegexNode} representing the union (alternation) of two regular
 * expression patterns. It matches if either of the sub-patterns matches.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:24
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CupRegexNode implements RegexNode {
    private RegexNode left;
    private RegexNode right;

    @Override
    public String toString() {
        return "(" + left + ")|(" + right + ")";
    }
}

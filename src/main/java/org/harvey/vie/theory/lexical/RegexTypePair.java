package org.harvey.vie.theory.lexical;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.regex.node.RegexNode;

/**
 * A simple data structure that pairs a regular expression tree with a
 * corresponding token type. This is used during the conversion process
 * to associate patterns with their results.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 17:54
 */
@AllArgsConstructor
@Getter
public class RegexTypePair {
    private final RegexNode node;
    private final TokenType type;
}

package org.harvey.vie.theory.lexical;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 17:37
 */
@Getter
@AllArgsConstructor
public class LexicalPattern {
    private final String regex;
    private final TokenType type;
}

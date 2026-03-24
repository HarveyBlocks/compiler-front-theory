package org.harvey.vie.theory.lexical.analysis.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:17
 */
@Getter
@AllArgsConstructor
public class SourceTokenImpl implements SourceToken {
    private final TokenType type;
    private final String lexeme;
    private final int offset;

    @Override
    public String hintString() {
        return String.format("%d:%s('%s')", offset, type.hint(), lexeme);
    }
}

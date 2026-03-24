package org.harvey.vie.theory.lexical.analysis.token;

import lombok.Getter;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:17
 */
@Getter
public class SourceTokenImpl implements SourceToken {
    private final TokenType type;
    private final String lexeme;
    private final int line;
    private final int column;

    public SourceTokenImpl(TokenType type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    @Override
    public String hintString() {
        return String.format("%s('%s') at %d:%d", type.name(), lexeme, line, column);
    }
}

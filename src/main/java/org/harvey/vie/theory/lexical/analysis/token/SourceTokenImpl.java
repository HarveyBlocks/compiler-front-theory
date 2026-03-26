package org.harvey.vie.theory.lexical.analysis.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.io.ByteOutStream;

/**
 * Implementation of {@link SourceToken} representing a recognized token in the
 * source code. It stores the token's type, the exact character sequence (lexeme),
 * and its position in the source.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:17
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SourceTokenImpl implements SourceToken {
    private final TokenType type;
    private final byte[] lexeme;
    private final int offset;

    public static SourceToken create(TokenType type, byte[][] lexeme, int offset) {
        return new SourceTokenImpl(type, ByteOutStream.flap(lexeme), offset);
    }

    @Override
    public String hintString() {
        return String.format("%d:%s('%s')", offset, type.hint(), new String(lexeme));
    }


    @Override
    public String toString() {
        return hintString();
    }
}

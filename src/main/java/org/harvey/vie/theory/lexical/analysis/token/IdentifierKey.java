package org.harvey.vie.theory.lexical.analysis.token;

import java.util.Arrays;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 21:44
 */
public class IdentifierKey {
    private final byte[] lexeme;

    private IdentifierKey(byte[] lexeme) {this.lexeme = lexeme;}
    public static IdentifierKey generate(SourceToken token){
        return new IdentifierKey(token.getLexeme());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdentifierKey)) {
            return false;
        }
        IdentifierKey that = (IdentifierKey) o;
        return Arrays.equals(lexeme, that.lexeme);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(lexeme);
    }
}

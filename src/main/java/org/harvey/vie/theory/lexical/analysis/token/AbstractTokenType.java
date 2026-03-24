package org.harvey.vie.theory.lexical.analysis.token;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 17:25
 */
public abstract class AbstractTokenType implements TokenType {
    @Override
    public int hashCode() {
        return hint().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this==obj||obj instanceof TokenType && hint().equals(((TokenType) obj).hint());
    }

    @Override
    public String toString() {
        return hint();
    }
}

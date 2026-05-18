package org.harvey.vie.theory.lexical.analysis.token;

import org.harvey.vie.theory.exception.CompileException;

/**
 * Interface for an iterator that produces {@link SourceToken} instances
 * from a segment of source text.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:14
 */
public interface SourceTokenIterator extends AutoCloseable {
    SourceToken NO_MORE_TOKEN = new DoneToken();

    boolean hasNext();

    /**
     * @throws CompileException unfinished token
     */
    SourceToken next() throws CompileException;

    int getOffset();

    SourceToken current() throws CompileException;
}

class DoneToken implements SourceToken {
    @Override
    public String hintString() {
        return "DONE";
    }

    @Override
    public byte[] getLexeme() {
        return new byte[0];
    }

    @Override
    public int getOffset() {
        return -1;
    }

    @Override
    public String toString() {
        return hintString();
    }

    @Override
    public TokenType getType() {
        return null;
    }

}
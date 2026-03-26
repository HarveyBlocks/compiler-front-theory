package org.harvey.vie.theory.lexical.analysis.token;

import org.harvey.vie.theory.exception.CompileException;

import java.io.Closeable;
import java.util.Iterator;

/**
 * Interface for an iterator that produces {@link SourceToken} instances
 * from a segment of source text.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:14
 */
public interface SourceTokenIterator extends AutoCloseable {
    boolean hasNext();
    SourceToken next() throws CompileException;

}

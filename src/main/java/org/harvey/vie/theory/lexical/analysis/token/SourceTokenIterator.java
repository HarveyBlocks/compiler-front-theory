package org.harvey.vie.theory.lexical.analysis.token;

import org.harvey.vie.theory.exception.CompileException;

import java.util.Iterator;

/**
 * TODO 一段文本对应SourceTokenIterator
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:14
 */
public interface SourceTokenIterator {
    boolean hasNext();
    SourceToken next() throws CompileException;
}

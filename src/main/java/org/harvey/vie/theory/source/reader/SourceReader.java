package org.harvey.vie.theory.source.reader;

import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.source.character.SourceCharacter;

import java.io.Closeable;
import java.io.IOException;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:44
 */
public interface SourceReader extends Closeable {

    /**
     * read and move 1 char
     */
    SourceCharacter read() throws IOException, CompileException;

    /**
     * read 1 char, but not move
     */
    SourceCharacter peek() throws IOException, CompileException;

    void close();

    int getOffset();
}

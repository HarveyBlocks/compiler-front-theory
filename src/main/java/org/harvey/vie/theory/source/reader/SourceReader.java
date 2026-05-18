package org.harvey.vie.theory.source.reader;

import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.source.character.SourceCharacter;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface for reading characters from a source input stream.
 * It extends {@link Closeable} and provides methods to read characters
 * sequentially or peek at the next character without advancing the pointer.
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

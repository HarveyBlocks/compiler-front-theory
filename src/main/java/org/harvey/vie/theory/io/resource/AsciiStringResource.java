package org.harvey.vie.theory.io.resource;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.source.reader.AsciiStringSourceReader;
import org.harvey.vie.theory.source.reader.SourceReader;

import java.io.StringReader;

/**
 * A implementation of {@link Resource} that uses an ASCII string as its source.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 20:59
 */
@AllArgsConstructor
public class AsciiStringResource implements Resource {
    private final String s;

    @Override
    public SourceReader toReader(ErrorContext errorContext) {
        return new AsciiStringSourceReader(new StringReader(s), errorContext);
    }
}

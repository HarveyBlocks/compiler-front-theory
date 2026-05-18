package org.harvey.vie.theory.io.resource;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.source.reader.SourceReader;

/**
 * Interface representing an input resource for the compiler. A resource
 * provides a way to obtain a {@link SourceReader} for processing its content.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 20:32
 */
public interface Resource {
    SourceReader toReader(ErrorContext errorContext);
}

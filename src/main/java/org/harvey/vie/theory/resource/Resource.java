package org.harvey.vie.theory.resource;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.source.reader.SourceReader;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 20:32
 */
public interface Resource {
    SourceReader toReader(ErrorContext errorContext);
}

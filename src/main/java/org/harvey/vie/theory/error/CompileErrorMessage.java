package org.harvey.vie.theory.error;

/**
 * Interface for messages describing errors detected during compilation.
 * Each message includes the offset where the error occurred and a descriptive string.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:29
 */
public interface CompileErrorMessage {

    int getOffset();

    String getMessage();
}

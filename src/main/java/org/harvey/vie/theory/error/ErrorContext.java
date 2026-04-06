package org.harvey.vie.theory.error;

import java.util.Collection;

/**
 * Interface defining the context for managing compilation errors.
 * It provides methods for adding error messages, retrieving all collected errors,
 * and checking if any errors have been encountered.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:27
 */
public interface ErrorContext {
    void addError(CompileErrorMessage message);

    Collection<? extends CompileErrorMessage> getErrors();

    boolean isEmpty();
}

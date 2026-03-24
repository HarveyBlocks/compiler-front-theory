package org.harvey.vie.theory.error;

import java.util.Collection;
import java.util.Collections;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:27
 */
public interface ErrorContext {
    void addError(CompileErrorMessage message);
    Collection<? extends CompileErrorMessage> getErrors();
}

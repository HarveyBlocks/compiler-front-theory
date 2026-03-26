package org.harvey.vie.theory.error;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A standard implementation of the {@link ErrorContext} interface that stores
 * compilation error messages in JVM memory. This class is suitable for general
 * error tracking during the compilation process.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:34
 */
@Getter
public class DefaultErrorContext implements ErrorContext {
    private final List<CompileErrorMessage> errors;

    public DefaultErrorContext() {
        errors = new ArrayList<>();
    }

    @Override
    public void addError(CompileErrorMessage message) {
        errors.add(message);
    }

    @Override
    public boolean isEmpty() {
        return errors.isEmpty();
    }

    @Override
    public String toString() {
        return errors.stream().map(CompileErrorMessage::toString).collect(Collectors.joining("\n"));
    }
}

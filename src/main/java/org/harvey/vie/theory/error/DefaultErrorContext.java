package org.harvey.vie.theory.error;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO JVM 内存级别的 ErrorContext
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
    public String toString() {
        return errors.stream().map(CompileErrorMessage::toString).collect(Collectors.joining("\n"));
    }
}

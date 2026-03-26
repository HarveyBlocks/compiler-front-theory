package org.harvey.vie.theory.error;

import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@link ErrorContext} specifically designed to handle
 * and store errors associated with a specific source file. This allows for
 * localized error reporting and management.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:28
 */
@Getter
public class FileErrorContext implements ErrorContext {
    private final List<CompileErrorMessage> errors;
    private final File file;

    public FileErrorContext(File file) {
        errors = new ArrayList<>();
        this.file = file;
    }

    @Override
    public void addError(CompileErrorMessage message) {
        errors.add(message);
    }

    @Override
    public boolean isEmpty() {
        return errors.isEmpty();
    }

}

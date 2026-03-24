package org.harvey.vie.theory.error;

import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO 当一个文件内出现了错误, 错误会放入FileErrorContext
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

}

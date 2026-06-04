package org.harvey.vie.theory.error;

import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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

    /**
     * 函数功能：创建关联指定文件的错误上下文。
     * 输入：
     * - file：错误上下文关联的源文件。
     * 输出：无。
     */
    public FileErrorContext(File file) {
        errors = new ArrayList<>();
        this.file = file;
    }

    /**
     * 函数功能：向文件错误上下文中添加编译错误信息。
     * 输入：
     * - message：待添加的编译错误信息。
     * 输出：无。
     */
    @Override
    public void addError(CompileErrorMessage message) {
        errors.add(message);
    }

    /**
     * 函数功能：获取当前文件错误上下文中的错误数量。
     * 输入：
     * - 无。
     * 输出：错误数量整数。
     */
    @Override
    public int size() {
        return errors.size();
    }


    /**
     * 函数功能：获取文件错误信息迭代器。
     * 输入：
     * - 无。
     * 输出：CompileErrorMessage 迭代器。
     */
    @Override
    public Iterator<CompileErrorMessage> iterator() {
        return errors.iterator();
    }
}

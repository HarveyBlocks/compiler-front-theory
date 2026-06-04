package org.harvey.vie.theory.error;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
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

    /**
     * 函数功能：创建默认错误上下文。
     * 输入：
     * - 无。
     * 输出：无。
     */
    public DefaultErrorContext() {
        errors = new ArrayList<>();
    }

    /**
     * 函数功能：向错误上下文中添加编译错误信息。
     * 输入：
     * - message：待添加的编译错误信息。
     * 输出：无。
     */
    @Override
    public void addError(CompileErrorMessage message) {
        errors.add(message);
    }

    /**
     * 函数功能：获取当前错误上下文中的错误数量。
     * 输入：
     * - 无。
     * 输出：错误数量整数。
     */
    @Override
    public int size() {
        return errors.size();
    }

    /**
     * 函数功能：返回所有错误信息拼接后的字符串。
     * 输入：
     * - 无。
     * 输出：错误信息的字符串表示。
     */
    @Override
    public String toString() {
        return errors.stream().map(CompileErrorMessage::toString).collect(Collectors.joining("\n"));
    }

    /**
     * 函数功能：获取错误信息迭代器。
     * 输入：
     * - 无。
     * 输出：CompileErrorMessage 迭代器。
     */
    @Override
    public Iterator<CompileErrorMessage> iterator() {
        return errors.iterator();
    }
}

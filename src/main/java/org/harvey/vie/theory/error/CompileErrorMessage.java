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

    /**
     * 函数功能：获取错误在源内容中的偏移量。
     * 输入：
     * - 无。
     * 输出：错误偏移量整数。
     */
    int getOffset();

    /**
     * 函数功能：获取错误描述信息。
     * 输入：
     * - 无。
     * 输出：错误描述字符串。
     */
    String getMessage();
}

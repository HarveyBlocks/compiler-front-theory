package org.harvey.vie.theory.error;

/**
 * Represents an error message encountered during input/output operations
 * within the compilation process.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 01:00
 */
public class IoErrorMessage extends AbstractErrorMessage {

    /**
     * 函数功能：创建输入输出阶段错误信息。
     * 输入：
     * - offset：错误在源内容中的偏移量。
     * - message：错误描述信息。
     * 输出：无。
     */
    public IoErrorMessage(int offset, String message) {
        super(offset, message);
    }
}

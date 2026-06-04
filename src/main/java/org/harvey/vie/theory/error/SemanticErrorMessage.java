package org.harvey.vie.theory.error;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 23:24
 */
public class SemanticErrorMessage extends AbstractErrorMessage {
    /**
     * 函数功能：创建语义分析阶段错误信息。
     * 输入：
     * - offset：错误在源内容中的偏移量。
     * - message：错误描述信息。
     * 输出：无。
     */
    public SemanticErrorMessage(int offset, String message) {
        super(offset, message);
    }

}

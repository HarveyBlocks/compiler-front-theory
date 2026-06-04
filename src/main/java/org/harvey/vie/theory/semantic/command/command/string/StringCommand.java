package org.harvey.vie.theory.semantic.command.command.string;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;

/**
 * TODO 仅用作测试和demo
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:26
 */
@AllArgsConstructor
public class StringCommand implements SemanticCommand {
    private final String value;
/**
 * 函数功能：返回当前对象的字符串表示。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    @Override
    public String toString() {
        return value;
    }
}

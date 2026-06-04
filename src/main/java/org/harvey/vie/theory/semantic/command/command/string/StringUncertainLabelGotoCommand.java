package org.harvey.vie.theory.semantic.command.command.string;

import lombok.Data;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;

/**
 * TODO 仅用作测试和demo
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:26
 */
@Data
public class StringUncertainLabelGotoCommand implements UncertainLabelGotoCommand {
    private final SourceToken token;
    private SemanticLabel label;

    /**
     * 函数功能：创建 StringUncertainLabelGotoCommand 对象。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：无。
     */

    public StringUncertainLabelGotoCommand(SourceToken token) {
        this.token = token;
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

    @Override
    public String toString() {
        return "goto " + (label == null ? "unknown" : label.getIndex() + "");
    }
}

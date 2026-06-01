package org.harvey.vie.theory.semantic.command.command.string;

import lombok.Data;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;

/**
 * {@code break}/{@code continue} 使用的延迟绑定 goto 文本命令。
 * <p>
 * 创建时只保存源 token，标签为空时打印为 {@code goto unknown}；循环翻译器绑定
 * {@link SemanticLabel} 后，{@link #toString()} 会输出 {@code goto <index>}。
 * 如果程序顶层仍然看到未知标签，
 * {@link org.harvey.vie.theory.semantic.command.translator.command.ProgramCommandTranslator} 会据此报错。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:26
 */
@Data
public class StringUncertainLabelGotoCommand implements UncertainLabelGotoCommand {
    private SemanticLabel label;
    private final SourceToken token;

    public StringUncertainLabelGotoCommand(SourceToken token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "goto " + (label==null?"unknown":label.getIndex()+"");
    }
}

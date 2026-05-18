package org.harvey.vie.theory.semantic.command.command;

import lombok.Data;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * TODO 仅用作测试和demo
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

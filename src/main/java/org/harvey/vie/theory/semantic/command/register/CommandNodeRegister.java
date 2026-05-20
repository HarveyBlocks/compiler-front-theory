package org.harvey.vie.theory.semantic.command.register;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;

import java.util.List;

/**
 * TODO 现在看来似乎不需要这个机制了. <br>
 *  Register 本来是用来处理label 与 outer 的关系的. <br>
 *  label如何注册, 可能要看 outer 的具体情况 <br>
 */
public interface CommandNodeRegister {
    void register(CommandNodeBuilder outer);

    default SemanticType getType() {
        return SemanticType.unknown();
    }

    default SemanticType getInstructionType() {
        return getType();
    }
    // TODO 到底是不是有必要增加这个功能的, 有待评估,
    //      我看是有错误设计的嫌疑, 在不该出现的地方出现了不该出现的代码,
    //      不是在这里出现代码最合适, 写在这里只不过是强词夺理, 望文生义了
    //      其实本质的错误是没有增加并使用一个StackCallback, 而是复用了CommandNodeRegister,
    //      而这就造成了代码的耦合
    default SourceToken getAnchorToken() {
        return null;
    }

    default List<UncertainLabelGotoCommand> getUncertainBreaks() {
        return List.of();
    }

    default List<UncertainLabelGotoCommand> getUncertainContinues() {
        return List.of();
    }
}

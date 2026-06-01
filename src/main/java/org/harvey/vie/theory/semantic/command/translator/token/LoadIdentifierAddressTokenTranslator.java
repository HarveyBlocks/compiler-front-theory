package org.harvey.vie.theory.semantic.command.translator.token;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

/**
 * 标识符 token 的占位翻译器。
 * <p>
 * 早期版本可能在移进标识符时直接加载局部地址，但现在标识符的语义要等规约后才能确定：
 * 它可能是声明名、函数名、结构体类型名，也可能才是真正的变量使用。
 * 因此这里返回 {@link PlaceholderNodeRegister}，真正的变量地址加载由
 * {@link org.harvey.vie.theory.semantic.command.translator.command.IdentifierUseTranslator} 完成。
 * <p>
 * 讲解时可以强调：这避免了把“声明里的 id”误翻译成运行时命令。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-25 16:30
 */
public class LoadIdentifierAddressTokenTranslator implements TokenTranslator {
    @Override
    public CommandNodeRegister translate(ShiftReduceSemanticContext context, SourceToken token) {
        return new PlaceholderNodeRegister();
    }
}

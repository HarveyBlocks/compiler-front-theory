package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 表达式支路：处理括号表达式 {@code (expr)}。
 * <p>
 * 括号本身不产生命令，只改变语法结合顺序。若当前括号表达式已折叠为常量，
 * 交给 {@link ConstantCommandSupport} 生成常量装载；否则只透传括号中的子表达式命令。
 * 讲完本支路回到 {@link InSuffixExpressionTranslator} 或
 * {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose}。
 *
 * @author Temper
 */
public class ParenthesizedExpressionTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        CommandNodeRegister constant = ConstantCommandSupport.constantOrNull(context, production, children);
        if (constant != null) {
            return constant;
        }
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        children[1].register(builder);
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }
}

package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.value.ConstantAttributes;
import org.harvey.vie.theory.semantic.value.ConstantValue;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 函数支路：翻译 {@code return} 语句。
 * <p>
 * 有返回值时，返回表达式先把值放到栈顶；如果表达式已被常量传播为编译期常量，
 * 直接生成 {@code load_st_*_static}，避免额外求值代码。最后统一追加
 * {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory#returnCommand()}，
 * 对应文本命令 {@code return}。
 * <p>
 * 讲完本支路可回到 {@link FunctionDefinitionTranslator} 看函数段如何保存。
 *
 * @author Temper
 */
public class FunctionReturnTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length != 2 && children.length != 3) {
            throw new org.harvey.vie.theory.exception.CompilerException(
                    "illegal statement on return statement production: " + production + ", children=" + children.length
            );
        }
        boolean hasValue = production.containsTag(ProgramSemanticTag.VALUE);
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        if (hasValue && ConstantAttributes.childIsConstant(context, 1)) {
            // 常量返回值可以直接装载，避免额外求值代码。
            ConstantValue value = ConstantAttributes.child(context, 1);
            if (value != null) {
                builder.add(new TerminalNode(context.getCommandFactory().loadConstant(value)));
            }
        } else if (hasValue) {
            children[1].register(builder);
        }
        builder.add(new TerminalNode(context.getCommandFactory().returnCommand()));
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }
}

package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 语句列表支路：把一串语句或实参按源程序顺序拼成线性命令。
 * <p>
 * 它和 {@link SimpleShrinkTranslator} 很像，但对列表场景更明确：空列表返回
 * {@link PlaceholderNodeRegister}，单元素列表直接透传，多元素列表才新建
 * {@link NormalCommandNodeRegister}。这保证块内语句、函数实参等命令顺序和源码顺序一致。
 * <p>
 * 讲完本支路回到 {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose}；最终继续看
 * {@link CommandNodeRegister} 如何注册到命令节点树。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:22
 */
public class StatementListTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length == 0) {
            return new PlaceholderNodeRegister();
        }
        if (children.length == 1) {
            return children[0];
        }
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        for (CommandNodeRegister child : children) {
            child.register(thisBuilder);
        }
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }
}

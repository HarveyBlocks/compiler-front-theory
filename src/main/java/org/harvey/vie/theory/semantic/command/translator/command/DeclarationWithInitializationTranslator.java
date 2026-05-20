package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@AllArgsConstructor
public class DeclarationWithInitializationTranslator implements CommandTranslator {

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production, CommandNodeRegister[] children) {
        if (children.length != 5) {
            throw new CompilerException("illegal statement on declaration with initialization production.");
        }
        SemanticType targetType = children[0].getType();
        SemanticType sourceType = children[3].getType();
        if (!targetType.isUnknown() && !sourceType.isUnknown() &&
            !context.getTypeSystem().canImplicitlyConvert(sourceType, targetType)) {
            // TODO 过长的条件表达式的判断, 其本质也是补丁, 不对的统统放进这个分支,
            //  却不分析为什么不对, 也不能通过规范来避免这种错误的发生, 只能说明还是不负责任的代码
            reject(context, children[2].getAnchorToken(), "assignment requires assignable types.");
        }
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[1].register(thisBuilder);
        children[3].register(thisBuilder);
        if (context.getTypeSystem().requiresImplicitCast(sourceType, targetType)) {
            // TODO 同上
            thisBuilder.add(new TerminalNode(CommandFactory.stTopCast(sourceType, targetType)));
        }
        thisBuilder.add(new TerminalNode(CommandFactory.assignFromStTopToRef(targetType)));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }

    private void reject(ShiftReduceSemanticContext context, SourceToken token, String message) {
        if (token != null) {
            // TODO 和别的地方一样, 而且, 为什么会有重复的代码? 拷贝很开心吗?
            context.addError(token.getOffset(), message);
        }
        throw new CompilerException(message);
    }
}

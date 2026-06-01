package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 标识符使用支路：把源码里的变量名翻译成“加载该变量槽位地址”的命令。
 * <p>
 * 注意移进 {@code IDENTIFIER} token 时不会立即生成地址命令，因为那时还不知道它是声明、函数名还是变量使用。
 * 等产生式规约到“标识符使用”语义标签后，本类从
 * {@link ShiftReduceSemanticContext#getIdentifier(org.harvey.vie.theory.lexical.analysis.token.SourceToken)}
 * 取得已解析的 {@link IdentifierRecord}，再调用
 * {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory#loadIdentifierAddress(IdentifierRecord)}。
 * <p>
 * 讲完本支路可看 {@link PrimaryProduceLeftValueTranslator}，它会在表达式位置把这个地址继续读成值。
 *
 * @author Temper
 */
public class IdentifierUseTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        HeadNode head = currentReducedHead(context);
        ShiftReduceSyntaxTreeNode child = head.get(0);
        if (!child.isToken()) {
            throw new CompilerException("identifier use requires a token child.");
        }
        SourceToken token = child.toToken().getSource();
        IdentifierRecord record = context.getIdentifier(token);
        if (record == null) {
            throw new CompilerException("identifier is not declared in current visible scopes.");
        }
        CommandNode node = new TerminalNode(context.getCommandFactory().loadIdentifierAddress(record));
        return new NormalCommandNodeRegister(new CommandNode[]{node}, production, children);
    }

    private static HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new CompilerException("current reduced head is absent for identifier use.");
        }
        return context.getTreeContext().peek().toHead();
    }
}

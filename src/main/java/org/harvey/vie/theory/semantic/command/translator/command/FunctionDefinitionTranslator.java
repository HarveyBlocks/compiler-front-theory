package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.CommandSegmentSupport;
import org.harvey.vie.theory.semantic.command.FunctionCommandSegment;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.List;

/**
 * 函数支路：收集函数体翻译后的命令序列，并把它登记成独立函数段。
 * <p>
 * 函数定义本身不会直接向当前入口命令流输出指令，否则全局入口执行时会顺序落进函数体。
 * 因此这里用 {@link CommandSegmentSupport#flatten(CommandNodeRegister)} 先把函数体命令展开成线性列表，
 * 再通过 {@link ShiftReduceSemanticContext#registerFunctionCommandSegment(FunctionCommandSegment)}
 * 登记到函数段上下文中，最后返回 {@link PlaceholderNodeRegister}。
 * <p>
 * 讲完本支路可看函数调用 {@link FunctionCallTranslator}，或继续沿主线去
 * {@link org.harvey.vie.theory.semantic.command.SemanticResultCallback} 查看入口段和函数段如何汇总。
 *
 * @author Temper
 */
public class FunctionDefinitionTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length != 2) {
            throw new CompilerException("function definition requires function head and block.");
        }
        FunctionRecord function = function(context);
        List<org.harvey.vie.theory.semantic.command.command.SemanticCommand> commands =
                CommandSegmentSupport.flatten(children[1]);
        // 函数体命令单独挂到函数记录上，供后续统一输出或执行。
        context.registerFunctionCommandSegment(new FunctionCommandSegment(function, commands));
        return new PlaceholderNodeRegister();
    }

    /**
     * 从当前归约出的函数定义节点中反查对应的函数符号记录。
     */
    private static FunctionRecord function(ShiftReduceSemanticContext context) {
        HeadNode head = currentReducedHead(context);
        ShiftReduceSyntaxTreeNode functionHead = head.get(0);
        if (!functionHead.isHead()) {
            throw new CompilerException("function definition head is absent.");
        }
        ShiftReduceSyntaxTreeNode tokenNode = functionHead.toHead().get(1);
        if (!tokenNode.isToken()) {
            throw new CompilerException("function name token is absent.");
        }
        SourceToken token = tokenNode.toToken().getSource();
        FunctionRecord record = context.getFunction(token);
        if (record == null) {
            throw new CompilerException("function definition is not registered.");
        }
        return record;
    }

    /**
     * 取得当前正在归约的头节点，用于恢复函数定义的语法上下文。
     */
    private static HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new CompilerException("current reduced head is absent for function definition.");
        }
        return context.getTreeContext().peek().toHead();
    }
}

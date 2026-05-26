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
        context.registerFunctionCommandSegment(new FunctionCommandSegment(function, commands));
        return new PlaceholderNodeRegister();
    }

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

    private static HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new CompilerException("current reduced head is absent for function definition.");
        }
        return context.getTreeContext().peek().toHead();
    }
}

package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.analysis.SemanticTypeDiagnostics;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO lvalue->lvalue [ expr ]
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@AllArgsConstructor
public class ArrayAtExpressionTranslator implements CommandTranslator {

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context, SimpleGrammarProduction production, CommandNodeRegister[] children) {
        if (children.length != 4) {
            throw new CompilerException("illegal statement on array at expression production.");
        }
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[0].register(thisBuilder);
        children[2].register(thisBuilder);
        SemanticType baseType = children[0].getType();
        SemanticType indexType = children[2].getType();
        if (!baseType.isUnknown() && !baseType.isArray()) {
            SemanticTypeDiagnostics.reject(context, children[1].getAnchorToken(), "subscript operator requires an array operand.");
        }
        if (!indexType.isUnknown() && !SemanticType.scalar(SemanticType.Kind.INT32).equals(indexType)) {
            SemanticTypeDiagnostics.reject(context, children[1].getAnchorToken(), "array index must be int32.");
        }
        SemanticType resultType = baseType.arrayElementType();
        thisBuilder.add(new TerminalNode(CommandFactory.biasFromStTopToRef(resultType)));
        return new NormalCommandNodeRegister(thisBuilder.build(),
                production,
                children,
                resultType,
                resultType,
                children[0].getAnchorToken()
        );
    }
}

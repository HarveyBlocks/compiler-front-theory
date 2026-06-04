package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
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
    /**
     * 函数功能：翻译语法节点并返回命令节点注册器。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * - children：CommandNodeRegister[] 类型参数。
     * 输出：CommandNodeRegister 类型返回值。
     */

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context, SimpleGrammarProduction production, CommandNodeRegister[] children) {
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[0].register(thisBuilder);
        children[2].register(thisBuilder);
        SemanticType baseType = TypeAttributes.childType(context, 0);
        SemanticType indexType = TypeAttributes.childType(context, 2);
        if (baseType == null) {
            SemanticDiagnostics.reject(
                    context,
                    TypeAttributes.childAnchor(context, 0),
                    "array access requires a typed left operand."
            );
        }
        if (indexType == null) {
            SemanticDiagnostics.reject(
                    context,
                    TypeAttributes.childAnchor(context, 2),
                    "array index expression requires a type."
            );
        }
        if (!baseType.isArray()) {
            SemanticDiagnostics.reject(
                    context,
                    TypeAttributes.childAnchor(context, 1),
                    "subscript operator requires an array operand."
            );
        }
        if (!SemanticType.scalar(SemanticType.Kind.INT32).equals(indexType)) {
            SemanticDiagnostics.reject(context, TypeAttributes.childAnchor(context, 1), "array index must be int32.");
        }
        SemanticType resultType = baseType.arrayElementType();
        thisBuilder.add(new TerminalNode(context.getCommandFactory().biasFromStTopToRef(
                CommandDataType.forStorage(resultType)
        )));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }
}

package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 左值定位支路：翻译数组下标访问 {@code lvalue[expr]}。
 * <p>
 * 左侧子命令先把数组对象的位置压到栈上，右侧子命令再把下标值压到栈上；随后本类追加
 * {@code bias_from_st_top_to_ref_*}，表示“用栈顶下标在数组引用上做偏移”，得到数组元素的引用位置。
 * 这个位置之后可以被 {@link PrimaryProduceLeftValueTranslator} 读取成值，也可以被
 * {@link AssignStatementTranslator} 写回。
 * <p>
 * 讲完本支路可继续看结构体字段偏移 {@link MemberAccessTranslator}，或回到
 * {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose}。
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
            SemanticDiagnostics.reject(context, TypeAttributes.childAnchor(context, 1), "subscript operator requires an array operand.");
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

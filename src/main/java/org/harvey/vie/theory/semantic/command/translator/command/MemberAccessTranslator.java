package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.structure.StructField;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 左值定位支路：翻译结构体成员访问 {@code object.field}。
 * <p>
 * 左操作数命令执行后，栈顶会保存结构体对象的引用或地址；本类通过
 * {@link ShiftReduceSemanticContext#getStruct(SemanticType)} 找到结构体布局，再用
 * {@link StructRecord#field(org.harvey.vie.theory.lexical.analysis.token.SourceToken)} 找到字段和固定偏移。
 * 最后追加 {@code bias_from_st_top_to_ref_* offset}，把“结构体对象位置”偏移成“字段位置”。
 * <p>
 * 讲完本支路可回到数组偏移 {@link ArrayAtExpressionTranslator}，或继续看左值读写：
 * {@link PrimaryProduceLeftValueTranslator} 与 {@link AssignStatementTranslator}。
 *
 * @author Temper
 */
public class MemberAccessTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        children[0].register(builder);
        SemanticType baseType = TypeAttributes.childType(context, 0);
        if (baseType == null) {
            SemanticDiagnostics.reject(
                    context,
                    TypeAttributes.childAnchor(context, 0),
                    "member access requires a typed left operand."
            );
        }
        StructRecord struct = context.getStruct(baseType);
        if (struct == null) {
            SemanticDiagnostics.reject(
                    context,
                    TypeAttributes.childAnchor(context, 0),
                    "member access requires a declared struct operand."
            );
        }
        StructField field = struct.field(TypeAttributes.childAnchor(context, 2));
        // 字段访问本质上是“基址 + 固定偏移”的引用偏移。
        builder.add(new TerminalNode(context.getCommandFactory().biasFromStTopToRef(
                CommandDataType.forStorage(field.getType()),
                field.getOffset()
        )));
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }
}

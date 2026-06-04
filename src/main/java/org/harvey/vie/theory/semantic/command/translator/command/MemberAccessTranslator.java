package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
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
 * 翻译成员访问表达式。
 * <p>
 * 左操作数求值后，栈顶会保存结构体对象的引用或地址；
 * 再结合字段在结构体布局中的固定偏移，生成字段定位指令。
 *
 * @author Temper
 */
public class MemberAccessTranslator implements CommandTranslator {
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

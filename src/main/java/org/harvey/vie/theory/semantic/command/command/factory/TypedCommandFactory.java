package org.harvey.vie.theory.semantic.command.command.factory;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.value.ConstantValue;

/**
 * 带数据类型后缀的命令工厂。
 * <p>
 * 大部分中间命令都要知道操作数或存储单元类型，例如 {@code int32}/{@code float64}/{@code ref}。
 * 翻译器先用 {@link CommandDataType} 把语义类型压缩成命令层面的数据类别，再调用本接口生成
 * {@link SemanticCommand}。
 * <p>
 * 当前 demo 的文本实现见
 * {@link org.harvey.vie.theory.semantic.command.command.string.TypedStringCommandFactory}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:04
 */
public interface TypedCommandFactory {
    SemanticCommand loadLiteral(SourceToken token);

    SemanticCommand loadIdentifierAddress(IdentifierRecord record);

    SemanticCommand loadConstant(ConstantValue constantValue);

    SemanticCommand newStruct(StructRecord record);

    SemanticCommand newArray(CommandDataType elementType, int totalDimensions, int specifiedDimensions);

    SemanticCommand stOperator(OperatorFactor operatorFactor, CommandDataType operandType);

    SemanticCommand stTopAddrToVal(CommandDataType type);

    SemanticCommand stTopRefToVal(CommandDataType type);

    SemanticCommand assignFromStTopToAddr(CommandDataType type);

    SemanticCommand assignFromStTopToRef(CommandDataType type);

    SemanticCommand biasFromStTopToAddr(CommandDataType elementType);

    SemanticCommand biasFromStTopToRef(CommandDataType elementType);

    SemanticCommand biasFromStTopToRef(CommandDataType fieldType, int offset);

    SemanticCommand stTopCast(CommandDataType from, CommandDataType to);

    SemanticCommand ifGoto(SemanticLabel label);

    SemanticCommand ifnGoto(SemanticLabel label);

    SemanticCommand gotoCommand(SemanticLabel label);

    UncertainLabelGotoCommand gotoCommandUncertainLabel(SourceToken token);
}

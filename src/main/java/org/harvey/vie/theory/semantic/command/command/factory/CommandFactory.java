package org.harvey.vie.theory.semantic.command.command.factory;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.value.ConstantValue;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:16
 */
public interface CommandFactory {
    SemanticCommand loadStatic(org.harvey.vie.theory.lexical.analysis.token.SourceToken token);

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

    UncertainLabelGotoCommand gotoCommandUncertainLabel(org.harvey.vie.theory.lexical.analysis.token.SourceToken token);

    SemanticCommand callFunction(FunctionRecord name);

    SemanticCommand returnCommand();
}

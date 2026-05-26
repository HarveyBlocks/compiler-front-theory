package org.harvey.vie.theory.semantic.command.command.factory;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.value.ConstantValue;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:16
 */
public interface CommandFactory {
    SemanticCommand loadStatic(SourceToken token);

    SemanticCommand loadIdentifierAddress(SourceToken token);

    SemanticCommand loadIdentifierAddress(IdentifierRecord record);

    SemanticCommand loadConstant(ConstantValue constantValue);

    SemanticCommand newStruct(SourceToken token);

    SemanticCommand newArray(SourceToken token, SemanticType type, int dimensions);

    SemanticCommand stOperator(OperatorFactor operatorFactor, SemanticType operandType);

    SemanticCommand stTopAddrToVal(SemanticType type);

    SemanticCommand stTopRefToVal(SemanticType type);

    SemanticCommand assignFromStTopToAddr(SemanticType type);

    SemanticCommand assignFromStTopToRef(SemanticType type);

    SemanticCommand biasFromStTopToAddr(SemanticType elementType);

    SemanticCommand biasFromStTopToRef(SemanticType elementType);

    SemanticCommand biasFromStTopToRef(SemanticType fieldType, int offset);

    SemanticCommand stTopCast(SemanticType from, SemanticType to);

    SemanticCommand ifGoto(SemanticLabel label);

    SemanticCommand ifnGoto(SemanticLabel label);

    SemanticCommand gotoCommand(SemanticLabel label);

    UncertainLabelGotoCommand gotoCommandUncertainLabel(SourceToken token);

    SemanticCommand callFunction(FunctionRecord name);

    SemanticCommand returnCommand();
}

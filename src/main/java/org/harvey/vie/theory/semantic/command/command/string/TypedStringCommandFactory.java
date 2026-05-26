package org.harvey.vie.theory.semantic.command.command.string;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.command.command.factory.TypedCommandFactory;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.semantic.type.TypeResolver;
import org.harvey.vie.theory.semantic.value.ConstantValue;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:04
 */
public class TypedStringCommandFactory implements TypedCommandFactory {
    private final TypeResolver typeResolver;

    public TypedStringCommandFactory(TypeResolver typeResolver) {this.typeResolver = typeResolver;}


    @Override
    public SemanticCommand loadLiteral(SourceToken token) {
        return new StringCommand("load_st_" +
                                 CommandDataType.forValue(typeResolver.literalType(token)).mnemonic() +
                                 "_static " +
                                 SourceTokenStringMapping.utf8(token));
    }

    @Override
    public SemanticCommand loadIdentifierAddress(IdentifierRecord record) {
        return new StringCommand("load_st_" +
                                 CommandDataType.forStorage(record.getDeclaredType()).mnemonic() +
                                 "_address " +
                                 record.getOffset());
    }

    @Override
    public SemanticCommand loadConstant(ConstantValue constantValue) {
        return new StringCommand("load_st_" +
                                 CommandDataType.forValue(constantValue.getType()).mnemonic() +
                                 "_static " +
                                 constantValue);
    }

    @Override
    public SemanticCommand newStruct(StructRecord record) {
        return new StringCommand("new_struct " + record.getTableIndex());
    }

    @Override
    public SemanticCommand newArray(CommandDataType elementType, int totalDimensions, int specifiedDimensions) {
        return new StringCommand(
                "new_array_" + elementType.mnemonic() + " " + totalDimensions + " " + specifiedDimensions
        );
    }

    @Override
    public SemanticCommand stOperator(OperatorFactor operatorFactor, CommandDataType operandType) {
        return new StringCommand("st_" + operatorFactor.mnemonic() + "_" + operandType.mnemonic());
    }

    @Override
    public SemanticCommand stTopAddrToVal(CommandDataType type) {
        return new StringCommand("st_top_addr_to_val_" + type.mnemonic());
    }

    @Override
    public SemanticCommand stTopRefToVal(CommandDataType type) {
        return new StringCommand("st_top_ref_to_val_" + type.mnemonic());
    }

    @Override
    public SemanticCommand assignFromStTopToAddr(CommandDataType type) {
        return new StringCommand("assign_from_st_top_to_addr_" + type.mnemonic());
    }

    @Override
    public SemanticCommand assignFromStTopToRef(CommandDataType type) {
        return new StringCommand("assign_from_st_top_to_ref_" + type.mnemonic());
    }

    @Override
    public SemanticCommand biasFromStTopToAddr(CommandDataType elementType) {
        return new StringCommand("bias_from_st_top_to_addr_" + elementType.mnemonic());
    }

    @Override
    public SemanticCommand biasFromStTopToRef(CommandDataType elementType) {
        return new StringCommand("bias_from_st_top_to_ref_" + elementType.mnemonic());
    }

    @Override
    public SemanticCommand biasFromStTopToRef(CommandDataType fieldType, int offset) {
        return new StringCommand("bias_from_st_top_to_ref_" + fieldType.mnemonic() + " " + offset);
    }

    @Override
    public SemanticCommand stTopCast(CommandDataType from, CommandDataType to) {
        return new StringCommand("st_top_" + from.mnemonic() + "_cast_" + to.mnemonic());
    }

    @Override
    public SemanticCommand ifGoto(SemanticLabel label) {
        return new StringSupplierCommand(() -> "if_goto " + label.getIndex());
    }

    @Override
    public SemanticCommand ifnGoto(SemanticLabel label) {
        return new StringSupplierCommand(() -> "ifn_goto " + label.getIndex());
    }

    @Override
    public SemanticCommand gotoCommand(SemanticLabel label) {
        return new StringSupplierCommand(() -> "goto " + label.getIndex());
    }

    @Override
    public UncertainLabelGotoCommand gotoCommandUncertainLabel(SourceToken token) {
        return new StringUncertainLabelGotoCommand(token);
    }
}

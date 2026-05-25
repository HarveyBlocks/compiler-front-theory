package org.harvey.vie.theory.semantic.command.command.string;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.command.factory.TypedCommandFactory;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.type.SemanticType;
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
                                 mnemonic(typeResolver.literalType(token)) +
                                 "_static " +
                                 SourceTokenStringMapping.utf8(token));
    }

    @Override
    public SemanticCommand loadIdentifierAddress(SourceToken token) {
        return new StringCommand("load_st_identifier_address " + SourceTokenStringMapping.utf8(token));
    }

    @Override
    public SemanticCommand loadIdentifierAddress(IdentifierRecord record) {
        return new StringCommand("load_st_" + mnemonic(record.getDeclaredType()) + "_address " + record.getOffset());
    }

    @Override
    public SemanticCommand loadConstant(ConstantValue constantValue) {
        return new StringCommand("load_st_" +
                                 mnemonic(constantValue.getType()) +
                                 "_static " +
                                 constantValue);
    }

    @Override
    public SemanticCommand newStruct(SourceToken token) {
        return new StringCommand("new_struct " + SourceTokenStringMapping.utf8(token));
    }

    @Override
    public SemanticCommand newArray(SourceToken token, SemanticType type, int dimensions) {
        return new StringCommand("new_array " + typeName(token, type) + " " + dimensions);
    }

    @Override
    public SemanticCommand stOperator(OperatorFactor operatorFactor, SemanticType operandType) {
        return new StringCommand("st_" + operatorFactor + "_" + mnemonic(operandType));
    }

    @Override
    public SemanticCommand stTopAddrToVal(SemanticType type) {
        return new StringCommand("st_top_addr_to_val_" + mnemonic(type));
    }

    @Override
    public SemanticCommand stTopRefToVal(SemanticType type) {
        return new StringCommand("st_top_ref_to_val_" + mnemonic(type));
    }

    @Override
    public SemanticCommand assignFromStTopToAddr(SemanticType type) {
        return new StringCommand("assign_from_st_top_to_addr_" + mnemonic(type));
    }

    @Override
    public SemanticCommand assignFromStTopToRef(SemanticType type) {
        return new StringCommand("assign_from_st_top_to_ref_" + mnemonic(type));
    }

    @Override
    public SemanticCommand biasFromStTopToAddr(SemanticType elementType) {
        return new StringCommand("bias_from_st_top_to_addr_" + mnemonic(elementType));
    }

    @Override
    public SemanticCommand biasFromStTopToRef(SemanticType elementType) {
        return new StringCommand("bias_from_st_top_to_ref_" + mnemonic(elementType));
    }

    @Override
    public SemanticCommand biasFromStTopToRef(SemanticType fieldType, int offset) {
        return new StringCommand("bias_from_st_top_to_ref_" + mnemonic(fieldType) + " " + offset);
    }

    @Override
    public SemanticCommand stTopCast(SemanticType from, SemanticType to) {
        return new StringCommand("st_top_" + mnemonic(from) + "_cast_" + mnemonic(to));
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

    private static String mnemonic(SemanticType type) {
        return type.getKind().name().toLowerCase();
    }

    private static String typeName(SourceToken token, SemanticType type) {
        if (type.getNamedTypeKey() != null) {
            return SourceTokenStringMapping.utf8(token);
        }
        return mnemonic(type);
    }
}

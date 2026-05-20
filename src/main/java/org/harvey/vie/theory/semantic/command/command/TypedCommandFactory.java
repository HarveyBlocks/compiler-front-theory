package org.harvey.vie.theory.semantic.command.command;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.analysis.TypeResolver;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;

public class TypedCommandFactory {
    private final TypeResolver typeResolver = new TypeResolver();

    public SemanticCommand loadLiteral(SourceToken token) {
        return new StringCommand("load_st_" + typeResolver.literalType(token).mnemonic() + "_static " + new String(token.getLexeme()));
    }

    public SemanticCommand loadIdentifierReference(SourceToken token) {
        return new StringCommand("load_st_identifier_reference " + new String(token.getLexeme()));
    }

    public SemanticCommand loadIdentifierReference(IdentifierRecord record) {
        return new StringCommand("load_st_" + record.getDeclaredType().mnemonic() + "_reference " + record.getOffset());
    }

    public SemanticCommand stOperator(OperatorFactor operatorFactor, SemanticType operandType) {
        return new StringCommand("st_" + operatorFactor + "_" + operandType.mnemonic());
    }

    public SemanticCommand stTopRefToVal(SemanticType type) {
        return new StringCommand("st_top_ref_to_val_" + type.mnemonic());
    }

    public SemanticCommand assignFromStTopToRef(SemanticType type) {
        return new StringCommand("assign_from_st_top_to_ref_" + type.mnemonic());
    }

    public SemanticCommand biasFromStTopToRef(SemanticType elementType) {
        return new StringCommand("bias_from_st_top_to_ref_" + elementType.mnemonic());
    }

    public SemanticCommand stTopCast(SemanticType from, SemanticType to) {
        return new StringCommand("st_top_" + from.mnemonic() + "_cast_" + to.mnemonic());
    }

    public SemanticCommand ifGoto(SemanticLabel label) {
        return new StringSupplierCommand(() -> "if_goto " + label.getIndex());
    }

    public SemanticCommand ifnGoto(SemanticLabel label) {
        return new StringSupplierCommand(() -> "ifn_goto " + label.getIndex());
    }

    public SemanticCommand gotoCommand(SemanticLabel label) {
        return new StringSupplierCommand(() -> "goto " + label.getIndex());
    }

    public UncertainLabelGotoCommand gotoCommandUncertainLabel(SourceToken token) {
        return new StringUncertainLabelGotoCommand(token);
    }
}

package org.harvey.vie.theory.semantic.command.command.factory;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.value.ConstantValue;

/**
 * TODO 静态工厂是不解耦的
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:24
 */
public class DefaultCommandFactory implements CommandFactory {
    private final TypedCommandFactory typedCommandFactory;
    private final SimpleCommandFactory simpleCommandFactory;

    public DefaultCommandFactory(TypedCommandFactory typedCommandFactory, SimpleCommandFactory simpleCommandFactory) {
        this.typedCommandFactory = typedCommandFactory;
        this.simpleCommandFactory = simpleCommandFactory;
    }

    /**
     * 仅用作测试和demo
     */
    @Override
    public SemanticCommand loadStatic(SourceToken token) {
        return typedCommandFactory.loadLiteral(token);
    }

    @Override
    public SemanticCommand loadIdentifierAddress(IdentifierRecord record) {
        return typedCommandFactory.loadIdentifierAddress(record);
    }

    @Override
    public SemanticCommand loadConstant(ConstantValue constantValue) {
        return typedCommandFactory.loadConstant(constantValue);
    }

    @Override
    public SemanticCommand newStruct(StructRecord record) {
        return typedCommandFactory.newStruct(record);
    }

    @Override
    public SemanticCommand newArray(CommandDataType elementType, int totalDimensions, int specifiedDimensions) {
        return typedCommandFactory.newArray(elementType, totalDimensions, specifiedDimensions);
    }

    @Override
    public SemanticCommand stOperator(OperatorFactor operatorFactor, CommandDataType operandType) {
        return typedCommandFactory.stOperator(operatorFactor, operandType);
    }

    @Override
    public SemanticCommand stTopAddrToVal(CommandDataType type) {
        return typedCommandFactory.stTopAddrToVal(type);
    }

    @Override
    public SemanticCommand stTopRefToVal(CommandDataType type) {
        return typedCommandFactory.stTopRefToVal(type);
    }

    @Override
    public SemanticCommand assignFromStTopToAddr(CommandDataType type) {
        return typedCommandFactory.assignFromStTopToAddr(type);
    }

    @Override
    public SemanticCommand assignFromStTopToRef(CommandDataType type) {
        return typedCommandFactory.assignFromStTopToRef(type);
    }

    @Override
    public SemanticCommand biasFromStTopToAddr(CommandDataType elementType) {
        return typedCommandFactory.biasFromStTopToAddr(elementType);
    }

    @Override
    public SemanticCommand biasFromStTopToRef(CommandDataType elementType) {
        return typedCommandFactory.biasFromStTopToRef(elementType);
    }

    @Override
    public SemanticCommand biasFromStTopToRef(CommandDataType fieldType, int offset) {
        return typedCommandFactory.biasFromStTopToRef(fieldType, offset);
    }

    @Override
    public SemanticCommand stTopCast(CommandDataType from, CommandDataType to) {
        return typedCommandFactory.stTopCast(from, to);
    }

    @Override
    public SemanticCommand ifGoto(SemanticLabel label) {
        return typedCommandFactory.ifGoto(label);
    }

    @Override
    public SemanticCommand ifnGoto(SemanticLabel label) {
        return typedCommandFactory.ifnGoto(label);
    }

    @Override
    public SemanticCommand gotoCommand(SemanticLabel label) {
        return typedCommandFactory.gotoCommand(label);
    }

    @Override
    public UncertainLabelGotoCommand gotoCommandUncertainLabel(SourceToken token) {
        return typedCommandFactory.gotoCommandUncertainLabel(token);
    }

    @Override
    public SemanticCommand callFunction(FunctionRecord name) {
        return simpleCommandFactory.callFunction(name);
    }

    @Override
    public SemanticCommand returnCommand() {
        return simpleCommandFactory.returnCommand();
    }

}

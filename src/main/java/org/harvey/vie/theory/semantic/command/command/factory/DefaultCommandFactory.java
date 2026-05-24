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
    public SemanticCommand loadIdentifierReference(SourceToken token) {
        return typedCommandFactory.loadIdentifierReference(token);
    }

    @Override
    public SemanticCommand loadIdentifierReference(IdentifierRecord record) {
        return typedCommandFactory.loadIdentifierReference(record);
    }

    @Override
    public SemanticCommand loadConstant(ConstantValue constantValue) {
        return typedCommandFactory.loadConstant(constantValue);
    }

    @Override
    public SemanticCommand stOperator(OperatorFactor operatorFactor, SemanticType operandType) {
        return typedCommandFactory.stOperator(operatorFactor, operandType);
    }

    @Override
    public SemanticCommand stTopRefToVal(SemanticType type) {
        return typedCommandFactory.stTopRefToVal(type);
    }

    @Override
    public SemanticCommand assignFromStTopToRef(SemanticType type) {
        return typedCommandFactory.assignFromStTopToRef(type);
    }

    @Override
    public SemanticCommand biasFromStTopToRef(SemanticType elementType) {
        return typedCommandFactory.biasFromStTopToRef(elementType);
    }

    @Override
    public SemanticCommand stTopCast(SemanticType from, SemanticType to) {
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

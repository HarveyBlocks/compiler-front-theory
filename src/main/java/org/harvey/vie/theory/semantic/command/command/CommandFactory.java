package org.harvey.vie.theory.semantic.command.command;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.value.ConstantValue;

import java.nio.charset.StandardCharsets;

/**
 * TODO 静态工厂是不解耦的
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:24
 */
public class CommandFactory {
    // TODO 糟糕的设计, 但是暂且不改
    //  正常应该是抽象工厂-具体工厂; 抽象产品-具体产品. 但是这个优先级低, 就不修改
    private static final TypedCommandFactory TYPED = new TypedCommandFactory();

    /**
     * 仅用作测试和demo
     */
    public static SemanticCommand loadStatic(SourceToken token) {
        return TYPED.loadLiteral(token);
    }

    public static SemanticCommand loadIdentifierReference(SourceToken token) {
        return TYPED.loadIdentifierReference(token);
    }

    public static SemanticCommand loadIdentifierReference(IdentifierRecord record) {
        return TYPED.loadIdentifierReference(record);
    }

    public static SemanticCommand loadConstant(ConstantValue constantValue) {
        return TYPED.loadConstant(constantValue);
    }

    public static SemanticCommand stOperator(OperatorFactor operatorFactor, SemanticType operandType) {
        return TYPED.stOperator(operatorFactor, operandType);
    }

    public static SemanticCommand stTopRefToVal(SemanticType type) {
        return TYPED.stTopRefToVal(type);
    }

    public static SemanticCommand assignFromStTopToRef(SemanticType type) {
        return TYPED.assignFromStTopToRef(type);
    }

    public static SemanticCommand biasFromStTopToRef(SemanticType elementType) {
        return TYPED.biasFromStTopToRef(elementType);
    }

    public static SemanticCommand stTopCast(SemanticType from, SemanticType to) {
        return TYPED.stTopCast(from, to);
    }

    public static SemanticCommand ifGoto(SemanticLabel label) {
        return TYPED.ifGoto(label);
    }

    public static SemanticCommand ifnGoto(SemanticLabel label) {
        return TYPED.ifnGoto(label);
    }

    public static SemanticCommand gotoCommand(SemanticLabel label) {
        return TYPED.gotoCommand(label);
    }

    public static SemanticCommand callFunction(FunctionRecord name) {
        return new StringCommand("call " +
                                 new String(name.getSignature().getNameToken().getLexeme(), StandardCharsets.UTF_8));
    }

    public static SemanticCommand returnCommand() {
        return new StringCommand("return");
    }


    public static UncertainLabelGotoCommand gotoCommandUncertainLabel(SourceToken token) {
        return TYPED.gotoCommandUncertainLabel(token);
    }
}

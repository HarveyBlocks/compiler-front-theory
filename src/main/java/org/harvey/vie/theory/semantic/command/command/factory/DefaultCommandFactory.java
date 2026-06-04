package org.harvey.vie.theory.semantic.command.command.factory;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.structure.StructRecord;
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

    /**
     * 函数功能：创建 DefaultCommandFactory 对象。
     * 输入：
     * - typedCommandFactory：TypedCommandFactory 类型参数。
     * - simpleCommandFactory：SimpleCommandFactory 类型参数。
     * 输出：无。
     */

    public DefaultCommandFactory(TypedCommandFactory typedCommandFactory, SimpleCommandFactory simpleCommandFactory) {
        this.typedCommandFactory = typedCommandFactory;
        this.simpleCommandFactory = simpleCommandFactory;
    }

    /**
     * 函数功能：生成加载静态值的命令。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */
    @Override
    public SemanticCommand loadStatic(SourceToken token) {
        return typedCommandFactory.loadLiteral(token);
    }

    /**
     * 函数功能：生成加载标识符地址的命令。
     * 输入：
     * - record：IdentifierRecord 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand loadIdentifierAddress(IdentifierRecord record) {
        return typedCommandFactory.loadIdentifierAddress(record);
    }

    /**
     * 函数功能：生成加载常量的命令。
     * 输入：
     * - constantValue：ConstantValue 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand loadConstant(ConstantValue constantValue) {
        return typedCommandFactory.loadConstant(constantValue);
    }

    /**
     * 函数功能：生成新建结构体命令。
     * 输入：
     * - record：StructRecord 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand newStruct(StructRecord record) {
        return typedCommandFactory.newStruct(record);
    }

    /**
     * 函数功能：生成新建数组命令。
     * 输入：
     * - elementType：CommandDataType 类型参数。
     * - totalDimensions：int 类型参数。
     * - specifiedDimensions：int 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand newArray(CommandDataType elementType, int totalDimensions, int specifiedDimensions) {
        return typedCommandFactory.newArray(elementType, totalDimensions, specifiedDimensions);
    }

    /**
     * 函数功能：生成栈顶运算命令。
     * 输入：
     * - operatorFactor：OperatorFactor 类型参数。
     * - operandType：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand stOperator(OperatorFactor operatorFactor, CommandDataType operandType) {
        return typedCommandFactory.stOperator(operatorFactor, operandType);
    }

    /**
     * 函数功能：生成栈顶地址取值命令。
     * 输入：
     * - type：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand stTopAddrToVal(CommandDataType type) {
        return typedCommandFactory.stTopAddrToVal(type);
    }

    /**
     * 函数功能：生成栈顶引用取值命令。
     * 输入：
     * - type：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand stTopRefToVal(CommandDataType type) {
        return typedCommandFactory.stTopRefToVal(type);
    }

    /**
     * 函数功能：生成从栈顶赋值到地址的命令。
     * 输入：
     * - type：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand assignFromStTopToAddr(CommandDataType type) {
        return typedCommandFactory.assignFromStTopToAddr(type);
    }

    /**
     * 函数功能：生成从栈顶赋值到引用的命令。
     * 输入：
     * - type：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand assignFromStTopToRef(CommandDataType type) {
        return typedCommandFactory.assignFromStTopToRef(type);
    }

    /**
     * 函数功能：生成基于地址偏移的命令。
     * 输入：
     * - elementType：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand biasFromStTopToAddr(CommandDataType elementType) {
        return typedCommandFactory.biasFromStTopToAddr(elementType);
    }

    /**
     * 函数功能：生成基于引用偏移的命令。
     * 输入：
     * - elementType：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand biasFromStTopToRef(CommandDataType elementType) {
        return typedCommandFactory.biasFromStTopToRef(elementType);
    }

    /**
     * 函数功能：生成基于引用偏移的命令。
     * 输入：
     * - fieldType：CommandDataType 类型参数。
     * - offset：int 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand biasFromStTopToRef(CommandDataType fieldType, int offset) {
        return typedCommandFactory.biasFromStTopToRef(fieldType, offset);
    }

    /**
     * 函数功能：生成栈顶类型转换命令。
     * 输入：
     * - from：CommandDataType 类型参数。
     * - to：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand stTopCast(CommandDataType from, CommandDataType to) {
        return typedCommandFactory.stTopCast(from, to);
    }

    /**
     * 函数功能：生成条件为真时跳转的命令。
     * 输入：
     * - label：SemanticLabel 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand ifGoto(SemanticLabel label) {
        return typedCommandFactory.ifGoto(label);
    }

    /**
     * 函数功能：生成条件为假时跳转的命令。
     * 输入：
     * - label：SemanticLabel 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand ifnGoto(SemanticLabel label) {
        return typedCommandFactory.ifnGoto(label);
    }

    /**
     * 函数功能：生成跳转命令。
     * 输入：
     * - label：SemanticLabel 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand gotoCommand(SemanticLabel label) {
        return typedCommandFactory.gotoCommand(label);
    }

    /**
     * 函数功能：生成未确定标签的跳转命令。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：UncertainLabelGotoCommand 类型返回值。
     */

    @Override
    public UncertainLabelGotoCommand gotoCommandUncertainLabel(SourceToken token) {
        return typedCommandFactory.gotoCommandUncertainLabel(token);
    }

    /**
     * 函数功能：生成函数调用命令。
     * 输入：
     * - name：FunctionRecord 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand callFunction(FunctionRecord name) {
        return simpleCommandFactory.callFunction(name);
    }

    /**
     * 函数功能：生成返回命令。
     * 输入：
     * - 无。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand returnCommand() {
        return simpleCommandFactory.returnCommand();
    }

}

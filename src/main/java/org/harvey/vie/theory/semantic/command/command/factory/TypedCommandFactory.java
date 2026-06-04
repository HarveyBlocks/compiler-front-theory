package org.harvey.vie.theory.semantic.command.command.factory;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.semantic.value.ConstantValue;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:04
 */
public interface TypedCommandFactory {
    /**
     * 函数功能：生成加载字面量的命令。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */
    SemanticCommand loadLiteral(SourceToken token);

    /**
     * 函数功能：生成加载标识符地址的命令。
     * 输入：
     * - record：IdentifierRecord 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand loadIdentifierAddress(IdentifierRecord record);

    /**
     * 函数功能：生成加载常量的命令。
     * 输入：
     * - constantValue：ConstantValue 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand loadConstant(ConstantValue constantValue);

    /**
     * 函数功能：生成新建结构体命令。
     * 输入：
     * - record：StructRecord 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand newStruct(StructRecord record);

    /**
     * 函数功能：生成新建数组命令。
     * 输入：
     * - elementType：CommandDataType 类型参数。
     * - totalDimensions：int 类型参数。
     * - specifiedDimensions：int 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand newArray(CommandDataType elementType, int totalDimensions, int specifiedDimensions);

    /**
     * 函数功能：生成栈顶运算命令。
     * 输入：
     * - operatorFactor：OperatorFactor 类型参数。
     * - operandType：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand stOperator(OperatorFactor operatorFactor, CommandDataType operandType);

    /**
     * 函数功能：生成栈顶地址取值命令。
     * 输入：
     * - type：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand stTopAddrToVal(CommandDataType type);

    /**
     * 函数功能：生成栈顶引用取值命令。
     * 输入：
     * - type：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand stTopRefToVal(CommandDataType type);

    /**
     * 函数功能：生成从栈顶赋值到地址的命令。
     * 输入：
     * - type：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand assignFromStTopToAddr(CommandDataType type);

    /**
     * 函数功能：生成从栈顶赋值到引用的命令。
     * 输入：
     * - type：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand assignFromStTopToRef(CommandDataType type);

    /**
     * 函数功能：生成基于地址偏移的命令。
     * 输入：
     * - elementType：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand biasFromStTopToAddr(CommandDataType elementType);

    /**
     * 函数功能：生成基于引用偏移的命令。
     * 输入：
     * - elementType：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand biasFromStTopToRef(CommandDataType elementType);

    /**
     * 函数功能：生成基于引用偏移的命令。
     * 输入：
     * - fieldType：CommandDataType 类型参数。
     * - offset：int 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand biasFromStTopToRef(CommandDataType fieldType, int offset);

    /**
     * 函数功能：生成栈顶类型转换命令。
     * 输入：
     * - from：CommandDataType 类型参数。
     * - to：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand stTopCast(CommandDataType from, CommandDataType to);

    /**
     * 函数功能：生成条件为真时跳转的命令。
     * 输入：
     * - label：SemanticLabel 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand ifGoto(SemanticLabel label);

    /**
     * 函数功能：生成条件为假时跳转的命令。
     * 输入：
     * - label：SemanticLabel 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand ifnGoto(SemanticLabel label);

    /**
     * 函数功能：生成跳转命令。
     * 输入：
     * - label：SemanticLabel 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand gotoCommand(SemanticLabel label);

    /**
     * 函数功能：生成未确定标签的跳转命令。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：UncertainLabelGotoCommand 类型返回值。
     */

    UncertainLabelGotoCommand gotoCommandUncertainLabel(SourceToken token);
}

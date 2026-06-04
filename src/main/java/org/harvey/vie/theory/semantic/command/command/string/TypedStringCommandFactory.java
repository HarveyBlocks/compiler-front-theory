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

    /**
     * 函数功能：创建 TypedStringCommandFactory 对象。
     * 输入：
     * - typeResolver：TypeResolver 类型参数。
     * 输出：无。
     */

    public TypedStringCommandFactory(TypeResolver typeResolver) {this.typeResolver = typeResolver;}

    /**
     * 函数功能：生成加载字面量的命令。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */


    @Override
    public SemanticCommand loadLiteral(SourceToken token) {
        return new StringCommand("load_st_" +
                                 CommandDataType.forValue(typeResolver.literalType(token)).mnemonic() +
                                 "_static " +
                                 SourceTokenStringMapping.utf8(token));
    }

    /**
     * 函数功能：生成加载标识符地址的命令。
     * 输入：
     * - record：IdentifierRecord 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand loadIdentifierAddress(IdentifierRecord record) {
        return new StringCommand("load_st_" +
                                 CommandDataType.forStorage(record.getDeclaredType()).mnemonic() +
                                 "_address " +
                                 record.getOffset());
    }

    /**
     * 函数功能：生成加载常量的命令。
     * 输入：
     * - constantValue：ConstantValue 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand loadConstant(ConstantValue constantValue) {
        return new StringCommand("load_st_" +
                                 CommandDataType.forValue(constantValue.getType()).mnemonic() +
                                 "_static " +
                                 constantValue);
    }

    /**
     * 函数功能：生成新建结构体命令。
     * 输入：
     * - record：StructRecord 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand newStruct(StructRecord record) {
        return new StringCommand("new_struct " + record.getTableIndex());
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
        return new StringCommand(
                "new_array_" + elementType.mnemonic() + " " + totalDimensions + " " + specifiedDimensions
        );
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
        return new StringCommand("st_" + operatorFactor.mnemonic() + "_" + operandType.mnemonic());
    }

    /**
     * 函数功能：生成栈顶地址取值命令。
     * 输入：
     * - type：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand stTopAddrToVal(CommandDataType type) {
        return new StringCommand("st_top_addr_to_val_" + type.mnemonic());
    }

    /**
     * 函数功能：生成栈顶引用取值命令。
     * 输入：
     * - type：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand stTopRefToVal(CommandDataType type) {
        return new StringCommand("st_top_ref_to_val_" + type.mnemonic());
    }

    /**
     * 函数功能：生成从栈顶赋值到地址的命令。
     * 输入：
     * - type：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand assignFromStTopToAddr(CommandDataType type) {
        return new StringCommand("assign_from_st_top_to_addr_" + type.mnemonic());
    }

    /**
     * 函数功能：生成从栈顶赋值到引用的命令。
     * 输入：
     * - type：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand assignFromStTopToRef(CommandDataType type) {
        return new StringCommand("assign_from_st_top_to_ref_" + type.mnemonic());
    }

    /**
     * 函数功能：生成基于地址偏移的命令。
     * 输入：
     * - elementType：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand biasFromStTopToAddr(CommandDataType elementType) {
        return new StringCommand("bias_from_st_top_to_addr_" + elementType.mnemonic());
    }

    /**
     * 函数功能：生成基于引用偏移的命令。
     * 输入：
     * - elementType：CommandDataType 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand biasFromStTopToRef(CommandDataType elementType) {
        return new StringCommand("bias_from_st_top_to_ref_" + elementType.mnemonic());
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
        return new StringCommand("bias_from_st_top_to_ref_" + fieldType.mnemonic() + " " + offset);
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
        return new StringCommand("st_top_" + from.mnemonic() + "_cast_" + to.mnemonic());
    }

    /**
     * 函数功能：生成条件为真时跳转的命令。
     * 输入：
     * - label：SemanticLabel 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand ifGoto(SemanticLabel label) {
        return new StringSupplierCommand(() -> "if_goto " + label.getIndex());
    }

    /**
     * 函数功能：生成条件为假时跳转的命令。
     * 输入：
     * - label：SemanticLabel 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand ifnGoto(SemanticLabel label) {
        return new StringSupplierCommand(() -> "ifn_goto " + label.getIndex());
    }

    /**
     * 函数功能：生成跳转命令。
     * 输入：
     * - label：SemanticLabel 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand gotoCommand(SemanticLabel label) {
        return new StringSupplierCommand(() -> "goto " + label.getIndex());
    }

    /**
     * 函数功能：生成未确定标签的跳转命令。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：UncertainLabelGotoCommand 类型返回值。
     */

    @Override
    public UncertainLabelGotoCommand gotoCommandUncertainLabel(SourceToken token) {
        return new StringUncertainLabelGotoCommand(token);
    }
}

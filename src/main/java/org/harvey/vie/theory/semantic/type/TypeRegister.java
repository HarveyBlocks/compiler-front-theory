package org.harvey.vie.theory.semantic.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.LocationKind;

/**
 * 保存绑定在语法树节点上的类型属性。
 * <p>
 * 作用：
 * <p>
 * TypeRegister 是 SemanticType 的属性包装器。
 * 语义分析不仅需要知道“表达式最终类型是什么”，还需要知道：
 * <p>
 * 1. 生成指令时应采用什么操作数类型。
 * 2. 报错或定位时应该指向哪个 token。
 * 3. 该表达式作为左值时保存的是地址还是引用。
 * <p>
 * 因此它同时保存 type、instructionType、anchorToken、locationKind。
 */
@Getter
@AllArgsConstructor
public class TypeRegister {
    private final SemanticType type;
    private final SemanticType instructionType;
    private final SourceToken anchorToken;
    private final LocationKind locationKind;

    /**
     * 函数功能：创建简单字符串命令工厂。
     * 输入：
     * - type：SemanticType 类型参数。
     * - anchorToken：SourceToken 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    public static TypeRegister simple(SemanticType type, SourceToken anchorToken) {
        return new TypeRegister(type, type, anchorToken, null);
    }

    /**
     * 函数功能：创建带类型字符串命令工厂。
     * 输入：
     * - type：SemanticType 类型参数。
     * - instructionType：SemanticType 类型参数。
     * - anchorToken：SourceToken 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    public static TypeRegister typed(SemanticType type, SemanticType instructionType, SourceToken anchorToken) {
        return new TypeRegister(type, instructionType, anchorToken, null);
    }

    /**
     * 函数功能：创建带位置的语义类型。
     * 输入：
     * - type：SemanticType 类型参数。
     * - instructionType：SemanticType 类型参数。
     * - anchorToken：SourceToken 类型参数。
     * - locationKind：LocationKind 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    public static TypeRegister located(
            SemanticType type,
            SemanticType instructionType,
            SourceToken anchorToken,
            LocationKind locationKind) {
        return new TypeRegister(type, instructionType, anchorToken, locationKind);
    }

    /**
     * 函数功能：判断指定节点是否已有语义类型。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */
    public boolean hasType() {
        return type != null;
    }

    /**
     * 函数功能：获取并校验语义类型。
     * 输入：
     * - message：String 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    public SemanticType requireType(String message) {
        if (!hasType()) {
            throw new CompilerException(message);
        }
        return type;
    }

    /**
     * 函数功能：获取并校验指令类型。
     * 输入：
     * - message：String 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    public SemanticType requireInstructionType(String message) {
        if (instructionType == null) {
            throw new CompilerException(message);
        }
        return instructionType;
    }

    /**
     * 函数功能：获取位置类型。
     * 输入：
     * - 无。
     * 输出：LocationKind 类型返回值。
     */
    public LocationKind getLocationKind() {
        return locationKind;
    }
}

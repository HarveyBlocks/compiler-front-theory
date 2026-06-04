package org.harvey.vie.theory.semantic.type;

import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 表示语义分析阶段使用的类型。
 *
 * 作用：
 *
 * 这个类是类型系统中的值对象，用来描述一个表达式、变量、函数返回值、
 * 结构体字段或数组元素在语义层面的类型。
 *
 * 它包含三类信息：
 *
 * 1. kind：基础类型种类，例如 int32、float64、struct、void。
 * 2. dimensions：数组维度信息。为空表示标量；非空表示数组。
 * 3. namedTypeKey：命名类型的唯一键，当前主要用于 struct 类型。
 *
 * 注意：
 *
 * SemanticType 本身不负责查符号表，也不判断某个 struct 是否已经声明。
 * 它只描述“类型长什么样”。声明存在性检查由语义上下文和相关 callback 完成。
 */
@Getter
public final class SemanticType {
    /**
     * 类型种类枚举。
     *
     * 作用：
     *
     * 描述语言当前支持的基础类型。
     *
     * 注意：
     *
     * 数组不是单独的 Kind，而是通过 kind + dimensions 组合表示。
     * 例如 int32[] 表示为 kind = INT32，dimensions 非空。
     */
    public enum Kind {
        BOOLEAN, CHARACTER, INT32, FLOAT64, STRING, VOID, STRUCT, NULL;
    }

    private final Kind kind;
    private final List<Integer> dimensions;
    private final IdentifierKey namedTypeKey;

    /**
     * 函数功能：创建 Kind 对象。
     * 输入：
     * - kind：Kind 类型参数。
     * - dimensions：List<Integer> 类型参数。
     * - namedTypeKey：IdentifierKey 类型参数。
     * 输出：无。
     */
    private SemanticType(Kind kind, List<Integer> dimensions, IdentifierKey namedTypeKey) {
        this.kind = kind;
        this.dimensions = List.copyOf(dimensions);
        this.namedTypeKey = namedTypeKey;
    }

    /**
     * 函数功能：创建标量语义类型。
     * 输入：
     * - kind：Kind 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    public static SemanticType scalar(Kind kind) {
        return new SemanticType(kind, List.of(), null);
    }

    /**
     * 函数功能：创建数组类型或数组命令数据。
     * 输入：
     * - kind：Kind 类型参数。
     * - dimensions：List<Integer> 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    public static SemanticType array(Kind kind, List<Integer> dimensions) {
        return new SemanticType(kind, dimensions, null);
    }

    /**
     * 函数功能：创建结构体语义类型。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    public static SemanticType struct(SourceToken token) {
        return new SemanticType(Kind.STRUCT, List.of(), IdentifierKey.generate(token));
    }

    /**
     * 函数功能：处理空字面量常量值。
     * 输入：
     * - 无。
     * 输出：SemanticType 类型返回值。
     */
    public static SemanticType nullLiteral() {
        return scalar(Kind.NULL);
    }

    /**
     * 函数功能：判断类型是否为标量。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */
    public boolean isScalar() {
        return dimensions.isEmpty();
    }

    /**
     * 函数功能：判断类型是否为数组类型。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */
    public boolean isArray() {
        return !dimensions.isEmpty();
    }

    /**
     * 函数功能：判断类型是否为布尔标量。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */
    public boolean isBooleanScalar() {
        return kind == Kind.BOOLEAN && isScalar();
    }

    /**
     * 函数功能：判断类型是否为数值标量。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */
    public boolean isNumericScalar() {
        return isScalar() && (kind == Kind.INT32 || kind == Kind.FLOAT64);
    }

    /**
     * 函数功能：判断类型是否为结构体类型。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */
    public boolean isStruct() {
        return kind == Kind.STRUCT && isScalar();
    }

    /**
     * 函数功能：判断类型是否为 void 标量。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */
    public boolean isVoidScalar() {
        return kind == Kind.VOID && isScalar();
    }

    /**
     * 函数功能：判断类型是否为空字面量类型。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */
    public boolean isNullLiteral() {
        return kind == Kind.NULL && isScalar();
    }

    /**
     * 函数功能：判断类型是否为引用类型。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */
    public boolean isReferenceType() {
        return kind == Kind.STRUCT || isArray();
    }

    /**
     * 函数功能：创建追加数组维度后的类型。
     * 输入：
     * - dimension：int 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    public SemanticType withAppendedDimension(int dimension) {
        ArrayList<Integer> next = new ArrayList<>(dimensions);
        next.add(dimension);
        return new SemanticType(kind, next, namedTypeKey);
    }

    /**
     * 函数功能：创建追加数组维度后的类型。
     * 输入：
     * - 无。
     * 输出：SemanticType 类型返回值。
     */
    public SemanticType withAppendedDimension() {
        return withAppendedDimension(0);
    }

    /**
     * 函数功能：获取数组元素类型。
     * 输入：
     * - 无。
     * 输出：SemanticType 类型返回值。
     */
    public SemanticType arrayElementType() {
        if (!isArray()) {
            throw new IllegalStateException("array element type requires an array semantic type");
        }
        if (dimensions.size() == 1) {
            return new SemanticType(kind, List.of(), namedTypeKey);
        }
        return new SemanticType(kind, dimensions.subList(1, dimensions.size()), namedTypeKey);
    }

    /**
     * 函数功能：获取数值类型的公共类型。
     * 输入：
     * - other：SemanticType 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    public SemanticType commonNumericType(SemanticType other) {
        if (!isNumericScalar() || !other.isNumericScalar()) {
            throw new IllegalStateException("common numeric type requires numeric scalar operands");
        }
        return kind == Kind.FLOAT64 || other.kind == Kind.FLOAT64 ? scalar(Kind.FLOAT64) : scalar(Kind.INT32);
    }

    /**
     * 函数功能：判断当前类型是否可隐式转换为目标类型。
     * 输入：
     * - target：SemanticType 类型参数。
     * 输出：判断结果布尔值。
     */
    public boolean canImplicitlyCastTo(SemanticType target) {
        return equals(target) || isNumericScalar() && target.isNumericScalar();
    }



    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(kind.name().toLowerCase());
        for (int i = 0; i < dimensions.size(); i++) {
            joiner.add("[");
            joiner.add("]");
        }
        return joiner.toString();
    }

    /**
     * 函数功能：判断当前对象是否与指定对象相等。
     * 输入：
     * - object：Object 类型参数。
     * 输出：判断结果布尔值。
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SemanticType)) {
            return false;
        }
        SemanticType that = (SemanticType) object;
        return kind == that.kind &&
               Objects.equals(dimensions, that.dimensions) &&
               Objects.equals(namedTypeKey, that.namedTypeKey);
    }

    /**
     * 函数功能：返回当前对象的哈希值。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */
    @Override
    public int hashCode() {
        return Objects.hash(kind, dimensions, namedTypeKey);
    }
}

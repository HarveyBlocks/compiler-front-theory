package org.harvey.vie.theory.semantic.type;

/**
 * 定义当前语言的类型兼容和隐式转换规则。
 * <p>
 * 作用：
 * <p>
 * TypeConversionRule 集中回答三个问题：
 * <p>
 * 1. 一个类型能否隐式转换到另一个类型。
 * 2. 这种兼容关系是否需要在中间代码里插入 cast 指令。
 * 3. 两个数值操作数参与二元运算时应使用什么公共类型。
 * <p>
 * 注意：
 * <p>
 * 该类描述的是当前语义分析实际使用的转换规则。
 * 如果修改语言的类型系统，应优先检查这里以及 SemanticType 中的相关辅助方法。
 */
public class TypeConversionRule {
    /**
     * 函数功能：判断源类型是否可隐式转换为目标类型。
     * 输入：
     * - from：SemanticType 类型参数。
     * - to：SemanticType 类型参数。
     * 输出：判断结果布尔值。
     */
    public boolean canImplicitlyConvert(SemanticType from, SemanticType to) {
        if (from.isNullLiteral()) {
            return to.isReferenceType();
        }
        if (from.equals(to)) {
            return true;
        }
        if (!from.isNumericScalar() || !to.isNumericScalar()) {
            return false;
        }
        return from.getKind() == SemanticType.Kind.INT32 && to.getKind() == SemanticType.Kind.FLOAT64;
    }

    /**
     * 函数功能：判断是否需要隐式类型转换。
     * 输入：
     * - from：SemanticType 类型参数。
     * - to：SemanticType 类型参数。
     * 输出：判断结果布尔值。
     */
    public boolean requiresImplicitCast(SemanticType from, SemanticType to) {
        if (from.isNullLiteral()) {
            return false;
        }
        return canImplicitlyConvert(from, to) && !from.equals(to);
    }

    /**
     * 函数功能：获取二元表达式的公共类型。
     * 输入：
     * - left：SemanticType 类型参数。
     * - right：SemanticType 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    public SemanticType commonBinaryType(SemanticType left, SemanticType right) {
        if (!left.isNumericScalar() || !right.isNumericScalar()) {
            throw new IllegalStateException("common binary type requires numeric scalar operands");
        }
        if (left.getKind() == SemanticType.Kind.FLOAT64 || right.getKind() == SemanticType.Kind.FLOAT64) {
            return SemanticType.scalar(SemanticType.Kind.FLOAT64);
        }
        return SemanticType.scalar(SemanticType.Kind.INT32);
    }
}


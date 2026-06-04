package org.harvey.vie.theory.semantic.value;

import lombok.Getter;
import org.harvey.vie.theory.semantic.type.SemanticType;

/**
 * 表示编译期可以确定的常量值。
 *
 * 作用：
 *
 * ConstantValue 是常量折叠和常量传播阶段使用的值对象。
 * 它同时保存：
 *
 * 1. type：常量的语义类型。
 * 2. value：常量的实际 Java 值。
 *
 * 例如：
 *
 * 1. int32 常量使用 Integer 保存。
 * 2. float64 常量使用 Double 保存。
 * 3. boolean 常量使用 Boolean 保存。
 * 4. null 字面量使用 type = NULL，value = null 保存。
 *
 * 注意：
 *
 * 该类只表示“已经确定是常量”的值。
 * 如果某个表达式不是编译期常量，通常不会绑定 ConstantValue。
 */
@Getter
public class ConstantValue {
    private final SemanticType type;
    private final Object value;

    /**
     * 函数功能：创建 ConstantValue 对象。
     * 输入：
     * - type：SemanticType 类型参数。
     * - value：Object 类型参数。
     * 输出：无。
     */
    public ConstantValue(SemanticType type, Object value) {
        this.type = type;
        this.value = value;
    }

    /**
     * 函数功能：创建整数命令数据。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */
    public int int32() {
        return (Integer) value;
    }

    /**
     * 函数功能：创建双精度浮点命令数据。
     * 输入：
     * - 无。
     * 输出：double 类型返回值。
     */
    public double float64() {
        return (Double) value;
    }

    /**
     * 函数功能：创建布尔类型命令数据。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */
    public boolean bool() {
        return (Boolean) value;
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}


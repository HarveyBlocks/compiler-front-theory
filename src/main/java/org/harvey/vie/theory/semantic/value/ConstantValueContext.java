package org.harvey.vie.theory.semantic.value;

import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 保存语法树节点到编译期常量值的映射。
 * <p>
 * 作用：
 * <p>
 * ConstantValueContext 是常量属性表。
 * ConstantValueBuildCallback 在归约表达式时，如果发现某个节点可以在编译期求值，
 * 就把该节点和对应 ConstantValue 绑定到这里。
 * <p>
 * 后续阶段可以根据这张表进行：
 * <p>
 * 1. 常量折叠：例如把 1 + 2 直接变成 3。
 * 2. 常量传播：例如引用初始化后仍未改变的常量变量。
 * 3. 中间代码优化：直接生成 loadConstant，而不是生成完整表达式求值指令。
 * <p>
 * 注意：
 * <p>
 * 这里使用 IdentityHashMap，因此节点 key 按对象身份比较。
 * 两个内容相同但不是同一个对象的语法树节点，会被视为不同 key。
 */
public class ConstantValueContext {
    private final Map<ShiftReduceSyntaxTreeNode, ConstantValue> values = new IdentityHashMap<>();

    /**
     * 函数功能：绑定指定节点与属性值。
     * 输入：
     * - node：ShiftReduceSyntaxTreeNode 类型参数。
     * - value：ConstantValue 类型参数。
     * 输出：无。
     */
    public void bind(ShiftReduceSyntaxTreeNode node, ConstantValue value) {
        if (node == null) {
            throw new IllegalArgumentException("node must not be null");
        }
        if (value == null) {
            values.remove(node);
            return;
        }
        values.put(node, value);
    }

    /**
     * 函数功能：获取指定键或索引对应的对象。
     * 输入：
     * - node：ShiftReduceSyntaxTreeNode 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    public ConstantValue get(ShiftReduceSyntaxTreeNode node) {
        return values.get(node);
    }

    /**
     * 函数功能：判断指定节点是否已有绑定。
     * 输入：
     * - node：ShiftReduceSyntaxTreeNode 类型参数。
     * 输出：判断结果布尔值。
     */
    public boolean has(ShiftReduceSyntaxTreeNode node) {
        return values.containsKey(node);
    }
}


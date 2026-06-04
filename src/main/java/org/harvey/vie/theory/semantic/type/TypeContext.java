package org.harvey.vie.theory.semantic.type;

import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 保存语法树节点到类型属性的映射。
 * <p>
 * 作用：
 * <p>
 * TypeContext 是类型属性表。
 * TypeBuildCallback 在归约时为某些语法树节点绑定 TypeRegister，
 * 后续常量折叠、函数检查、结构体检查、命令生成等阶段再从这里读取类型属性。
 * <p>
 * 注意：
 * <p>
 * 这里使用 IdentityHashMap，而不是普通 HashMap。
 * 因此 key 的比较依据是节点对象身份，而不是 equals/hashCode。
 * 这适合语法树节点这种“同内容但不同节点也应区分”的场景。
 */
public class TypeContext {
    private final Map<ShiftReduceSyntaxTreeNode, TypeRegister> attributes = new IdentityHashMap<>();

    /**
     * 函数功能：绑定指定节点与属性值。
     * 输入：
     * - node：ShiftReduceSyntaxTreeNode 类型参数。
     * - register：TypeRegister 类型参数。
     * 输出：无。
     */
    public void bind(ShiftReduceSyntaxTreeNode node, TypeRegister register) {
        if (node == null) {
            throw new IllegalArgumentException("node must not be null");
        }
        if (register == null) {
            attributes.remove(node);
            return;
        }
        attributes.put(node, register);
    }

    /**
     * 函数功能：获取指定键或索引对应的对象。
     * 输入：
     * - node：ShiftReduceSyntaxTreeNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    public TypeRegister get(ShiftReduceSyntaxTreeNode node) {
        return attributes.get(node);
    }

    /**
     * 函数功能：判断指定节点是否已有绑定。
     * 输入：
     * - node：ShiftReduceSyntaxTreeNode 类型参数。
     * 输出：判断结果布尔值。
     */
    public boolean has(ShiftReduceSyntaxTreeNode node) {
        return attributes.containsKey(node);
    }

    /**
     * 函数功能：移动指定节点的绑定信息。
     * 输入：
     * - from：ShiftReduceSyntaxTreeNode 类型参数。
     * - to：ShiftReduceSyntaxTreeNode 类型参数。
     * 输出：无。
     */
    public void move(ShiftReduceSyntaxTreeNode from, ShiftReduceSyntaxTreeNode to) {
        if (from == to) {
            return;
        }
        TypeRegister register = attributes.remove(from);
        if (register != null) {
            attributes.put(to, register);
        }
    }
}


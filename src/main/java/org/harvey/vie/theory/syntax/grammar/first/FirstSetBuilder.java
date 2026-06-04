package org.harvey.vie.theory.syntax.grammar.first;

import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 19:40
 */
public class FirstSetBuilder {
    private final Set<TerminalSymbol> set = new HashSet<>();
    @Getter
    @Setter
    private boolean containsEpsilon = false;

    /**
     * 函数功能：处理终结符匹配。
     * 输入：
     * - terminal：TerminalSymbol 类型参数。
     * 输出：FirstSetBuilder 类型返回值。
     */

    public static FirstSetBuilder terminal(TerminalSymbol terminal) {
        FirstSetBuilder builder = new FirstSetBuilder();
        builder.set.add(terminal);
        builder.containsEpsilon = false;
        return builder;
    }

    /**
     * 函数功能：添加除空串外的 FIRST 集元素。
     * 输入：
     * - builder：FirstSetBuilder 类型参数。
     * 输出：无。
     */

    public void addAllExceptEpsilon(FirstSetBuilder builder) {
        set.addAll(builder.set);
    }

    /**
     * 函数功能：构建目标对象。
     * 输入：
     * - 无。
     * 输出：FirstSet 类型返回值。
     */

    public FirstSet build() {
        return new FirstSetImpl(set, containsEpsilon);
    }

    /**
     * 函数功能：获取当前集合大小。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    public int setSize() {
        return set.size();
    }
}

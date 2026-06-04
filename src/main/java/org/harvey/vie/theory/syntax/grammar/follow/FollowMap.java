package org.harvey.vie.theory.syntax.grammar.follow;

import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 15:25
 */
public interface FollowMap {
    /**
     * 函数功能：获取指定位置或键对应的元素。
     * 输入：
     * - head：HeadSymbol 类型参数。
     * 输出：FollowSet 类型返回值。
     */
    FollowSet get(HeadSymbol head);
/**
 * 函数功能：获取键集合。
 * 输入：
 * - 无。
 * 输出：Set<HeadSymbol> 类型集合或迭代结果。
 */

    Set<HeadSymbol> keySet();
/**
 * 函数功能：获取值集合。
 * 输入：
 * - 无。
 * 输出：Collection<FollowSet> 类型集合或迭代结果。
 */

    Collection<FollowSet> values();

    Set<Map.Entry<HeadSymbol, FollowSet>> entrySet();
/**
 * 函数功能：获取元素数量。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

    int size();
}

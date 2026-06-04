package org.harvey.vie.theory.syntax.grammar.follow;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 15:26
 */
@AllArgsConstructor
public class FollowMapImpl implements FollowMap {
    private final Map<HeadSymbol, FollowSet> map;
/**
 * 函数功能：获取指定位置或键对应的元素。
 * 输入：
 * - head：HeadSymbol 类型参数。
 * 输出：FollowSet 类型返回值。
 */

    @Override
    public FollowSet get(HeadSymbol head) {
        return map.get(head);
    }
/**
 * 函数功能：获取键集合。
 * 输入：
 * - 无。
 * 输出：Set<HeadSymbol> 类型集合或迭代结果。
 */

    @Override
    public Set<HeadSymbol> keySet() {
        return map.keySet();
    }
/**
 * 函数功能：获取值集合。
 * 输入：
 * - 无。
 * 输出：Collection<FollowSet> 类型集合或迭代结果。
 */

    @Override
    public Collection<FollowSet> values() {
        return map.values();
    }
/**
 * 函数功能：获取键值项集合。
 * 输入：
 * - 无。
 * 输出：FollowSet>> 类型返回值。
 */

    @Override
    public Set<Map.Entry<HeadSymbol, FollowSet>> entrySet() {
        return map.entrySet();
    }
/**
 * 函数功能：获取元素数量。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

    @Override
    public int size() {
        return map.size();
    }
}

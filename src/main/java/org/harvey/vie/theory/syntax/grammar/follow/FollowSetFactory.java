package org.harvey.vie.theory.syntax.grammar.follow;

import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;

/**
 * TODO Follow(X)
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 00:36
 */
public interface FollowSetFactory {
    /**
     * 函数功能：获取 FOLLOW 集合。
     * 输入：
     * - startHead：String 类型参数。
     * - context：ProductionSetContext 类型参数。
     * - firstMap：FirstMap 类型参数。
     * 输出：FollowMap 类型返回值。
     */
    FollowMap follow(String startHead, ProductionSetContext context, FirstMap firstMap);
}

package org.harvey.vie.theory.syntax.td.table;

import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.follow.FollowMap;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 18:59
 */
public interface PredictiveParsingTableFactory {
/**
 * 函数功能：根据输入数据创建目标对象。
 * 输入：
 * - context：ProductionSetContext 类型参数。
 * - firstMap：FirstMap 类型参数。
 * - followMap：FollowMap 类型参数。
 * 输出：PredictiveParsingTable 类型返回值。
 */

    PredictiveParsingTable produce(ProductionSetContext context, FirstMap firstMap, FollowMap followMap);

}

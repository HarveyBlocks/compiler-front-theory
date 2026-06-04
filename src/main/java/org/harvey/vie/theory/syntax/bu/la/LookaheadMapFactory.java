package org.harvey.vie.theory.syntax.bu.la;

import org.harvey.vie.theory.syntax.bu.item.ItemSetFamily;
import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 15:01
 */
public interface LookaheadMapFactory {
/**
 * 函数功能：根据输入数据创建目标对象。
 * 输入：
 * - startHead：String 类型参数。
 * - context：ProductionSetContext 类型参数。
 * - family：ItemSetFamily 类型参数。
 * - firstMap：FirstMap 类型参数。
 * 输出：LookaheadMap[] 类型数组。
 */

    LookaheadMap[] produce(String startHead, ProductionSetContext context, ItemSetFamily family, FirstMap firstMap);
}

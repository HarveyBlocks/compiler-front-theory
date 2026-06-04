package org.harvey.vie.theory.syntax.bu.table;

import org.harvey.vie.theory.syntax.bu.item.ItemSetFamily;
import org.harvey.vie.theory.syntax.bu.la.LookaheadMap;
import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:40
 */
public interface ShiftReduceParsingTableFactory {
/**
 * 函数功能：根据输入数据创建目标对象。
 * 输入：
 * - startHead：String 类型参数。
 * - context：ProductionSetContext 类型参数。
 * - firstMap：FirstMap 类型参数。
 * - family：ItemSetFamily 类型参数。
 * - lookaheadMaps：LookaheadMap[] 类型参数。
 * 输出：ShiftReduceParsingTable 类型返回值。
 */

    ShiftReduceParsingTable produce(
            String startHead,
            ProductionSetContext context,
            FirstMap firstMap,
            ItemSetFamily family,
            LookaheadMap[] lookaheadMaps);
}

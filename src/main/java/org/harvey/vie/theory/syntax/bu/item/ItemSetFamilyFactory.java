package org.harvey.vie.theory.syntax.bu.item;

import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 23:40
 */
public interface ItemSetFamilyFactory {
    /**
     * 函数功能：根据输入数据创建目标对象。
     * 输入：
     * - startHead：String 类型参数。
     * - context：ProductionSetContext 类型参数。
     * - firstMap：FirstMap 类型参数。
     * 输出：ItemSetFamily 类型返回值。
     */
    ItemSetFamily produce(String startHead, ProductionSetContext context, FirstMap firstMap);
}

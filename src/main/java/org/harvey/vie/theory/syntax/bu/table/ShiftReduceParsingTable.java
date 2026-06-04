package org.harvey.vie.theory.syntax.bu.table;

import org.harvey.vie.theory.io.ILoader;
import org.harvey.vie.theory.io.Storage;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.syntax.bu.item.ItemSet;
import org.harvey.vie.theory.syntax.bu.table.element.ActiveTableElement;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:39
 */
public interface ShiftReduceParsingTable extends Storage {
    int NONE = ItemSet.NONE;

    /**
     * 函数功能：获取起始元素。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    int getStart();

    /**
     * 函数功能：获取规约后跳转的状态。
     * 输入：
     * - originStatus：int 类型参数。
     * - head：HeadSymbol 类型参数。
     * 输出：整数结果。
     */

    int gotoNext(int originStatus, HeadSymbol head);

    /**
     * 函数功能：查询动作表中的下一动作。
     * 输入：
     * - originStatus：int 类型参数。
     * - terminal：int 类型参数。
     * 输出：ActiveTableElement 类型返回值。
     */

    ActiveTableElement activeNext(int originStatus, int terminal);

    /**
     * 函数功能：获取指定产生式。
     * 输入：
     * - i：int 类型参数。
     * 输出：SimpleGrammarProduction 类型返回值。
     */

    SimpleGrammarProduction getProduction(int i);

    /**
     * 函数功能：判断词法单元是否匹配终结符。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：整数结果。
     */

    int matchTerminal(SourceToken token);

    /**
     * 函数功能：获取产生式编号。
     * 输入：
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：整数结果。
     */

    Integer getProductionId(SimpleGrammarProduction production);

    interface Loader<T extends ShiftReduceParsingTable> extends ILoader<T> {
    }
}

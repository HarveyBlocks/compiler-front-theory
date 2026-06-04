package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactor;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:56
 */
public interface ProductionSetContextBuilder {
    /**
     * 函数功能：定义新的语法产生式。
     * 输入：
     * - name：String 类型参数。
     * - tags：SemanticTag... 类型参数。
     * 输出：GrammarProductionBuilder 类型返回值。
     */
    GrammarProductionBuilder define(String name, SemanticTag... tags);

    /**
     * 函数功能：创建终结符符号。
     * 输入：
     * - factor：TerminalFactor 类型参数。
     * 输出：TerminalSymbol 类型返回值。
     */

    TerminalSymbol createTerminal(TerminalFactor factor);

    /**
     * 函数功能：构建目标对象。
     * 输入：
     * - 无。
     * 输出：ProductionSetContext 类型返回值。
     */

    ProductionSetContext build();
}

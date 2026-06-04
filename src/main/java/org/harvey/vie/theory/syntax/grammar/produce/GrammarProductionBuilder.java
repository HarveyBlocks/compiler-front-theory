package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactor;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:54
 */
public interface GrammarProductionBuilder {
/**
 * 函数功能：添加终结符候选产生式。
 * 输入：
 * - factor：TerminalFactor 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    GrammarProductionBuilder alternateTerminal(TerminalFactor factor);
/**
 * 函数功能：添加非终结符候选产生式。
 * 输入：
 * - definition：String 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */


    GrammarProductionBuilder alternateDefinition(String definition);
/**
 * 函数功能：添加空串候选产生式。
 * 输入：
 * - 无。
 * 输出：GrammarProductionBuilder 类型返回值。
 */


    GrammarProductionBuilder alternateEpsilon();
/**
 * 函数功能：添加自身候选产生式。
 * 输入：
 * - 无。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    GrammarProductionBuilder alternateSelf();
/**
 * 函数功能：添加占位候选产生式。
 * 输入：
 * - 无。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    GrammarProductionBuilder alternatePlaceholder();
/**
 * 函数功能：在最后一个候选式末尾连接终结符。
 * 输入：
 * - factor：TerminalFactor 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    GrammarProductionBuilder concatenateTerminalLast(TerminalFactor factor);
/**
 * 函数功能：在最后一个候选式末尾连接非终结符。
 * 输入：
 * - definition：String 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    GrammarProductionBuilder concatenateDefinitionLast(String definition);
/**
 * 函数功能：在最后一个候选式末尾连接自身符号。
 * 输入：
 * - 无。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    GrammarProductionBuilder concatenateSelfLast();
/**
 * 函数功能：在指定候选式中连接终结符。
 * 输入：
 * - i：int 类型参数。
 * - factor：TerminalFactor 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */


    GrammarProductionBuilder concatenateTerminal(int i, TerminalFactor factor);
/**
 * 函数功能：在指定候选式中连接非终结符。
 * 输入：
 * - i：int 类型参数。
 * - definition：String 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */


    GrammarProductionBuilder concatenateDefinition(int i, String definition);
/**
 * 函数功能：在指定候选式中连接自身符号。
 * 输入：
 * - i：int 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    GrammarProductionBuilder concatenateSelf(int i);
/**
 * 函数功能：为最后一个候选式添加语义标签。
 * 输入：
 * - tag：SemanticTag... 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    GrammarProductionBuilder tagLast(SemanticTag... tag);
/**
 * 函数功能：为指定候选式添加语义标签。
 * 输入：
 * - i：int 类型参数。
 * - tag：SemanticTag... 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    GrammarProductionBuilder tag(int i, SemanticTag... tag);
/**
 * 函数功能：构建目标对象。
 * 输入：
 * - 无。
 * 输出：GrammarDefineProduction 类型返回值。
 */

    GrammarDefineProduction build();
/**
 * 函数功能：获取产生式头部符号。
 * 输入：
 * - 无。
 * 输出：HeadDefineSymbol 类型返回值。
 */


    HeadDefineSymbol getHead();

}

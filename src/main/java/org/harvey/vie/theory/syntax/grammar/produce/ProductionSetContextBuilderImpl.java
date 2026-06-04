package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbolImpl;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactor;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactory;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:15
 */
public class ProductionSetContextBuilderImpl implements ProductionSetContextBuilder {
    private final Map<String, Integer> definitionIdxMap;
    private final List<GrammarProductionBuilder> list;
    private final Map<TerminalFactor, TerminalSymbol> terminalMap;
    private final TerminalFactory terminalFactory;

    /**
     * 函数功能：创建 ProductionSetContextBuilderImpl 对象。
     * 输入：
     * - terminalFactory：TerminalFactory 类型参数。
     * 输出：无。
     */

    public ProductionSetContextBuilderImpl(TerminalFactory terminalFactory) {
        this.list = new ArrayList<>();
        this.definitionIdxMap = new HashMap<>();
        this.terminalMap = new HashMap<>();
        this.terminalFactory = terminalFactory;
    }

    /**
     * 函数功能：定义新的语法产生式。
     * 输入：
     * - name：String 类型参数。
     * - tags：SemanticTag... 类型参数。
     * 输出：GrammarProductionBuilder 类型返回值。
     */

    @Override
    public GrammarProductionBuilder define(String name, SemanticTag... tags) {
        Integer idx = definitionIdxMap.computeIfAbsent(name, k -> {
            int i = list.size();
            HeadDefineSymbolImpl head = new HeadDefineSymbolImpl(name);
            list.add(new GrammarProductionBuilderImpl(head, this));
            return i;
        });
        GrammarProductionBuilder productionBuilder = list.get(idx);
        if (tags != null && tags.length != 0) {
            productionBuilder.getHead().addTag(tags);
        }
        return productionBuilder;
    }

    /**
     * 函数功能：创建终结符符号。
     * 输入：
     * - factor：TerminalFactor 类型参数。
     * 输出：TerminalSymbol 类型返回值。
     */

    @Override
    public TerminalSymbol createTerminal(TerminalFactor factor) {
        return terminalMap.computeIfAbsent(factor, terminalFactory::create);
    }

    /**
     * 函数功能：构建目标对象。
     * 输入：
     * - 无。
     * 输出：ProductionSetContext 类型返回值。
     */

    @Override
    public ProductionSetContext build() {
        GrammarDefineProduction[] productions = list.stream()
                .map(GrammarProductionBuilder::build)
                .toArray(GrammarDefineProduction[]::new);
        return new ProductionSetContextImpl(terminalFactory, definitionIdxMap, productions);
    }


}

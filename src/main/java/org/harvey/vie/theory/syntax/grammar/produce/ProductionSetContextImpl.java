package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:37
 */
public class ProductionSetContextImpl implements ProductionSetContext {
    private final Map<String, Integer> definitionIdxMap;
    private final GrammarDefineProduction[] productions;

    public ProductionSetContextImpl(Map<String, Integer> definitionIdxMap, GrammarDefineProduction[] productions) {
        this.definitionIdxMap = definitionIdxMap;
        this.productions = productions;
    }

    @Override
    public HeadDefineSymbol getDefinition(String name) {
        return productions[indexOf(name)].getDefine();
    }

    @Override
    public Integer indexOf(String name) {
        return definitionIdxMap.get(name);
    }

    @Override
    public GrammarDefineProduction get(int i) {
        return productions[i];
    }

    @Override
    public Integer indexOf(HeadDefineSymbol define) {
        return indexOf(define.getName());
    }

    @Override
    public int length() {
        return productions.length;
    }

    @Override
    public boolean isEmpty() {
        return productions.length == 0;
    }

    @Override
    public Iterator<GrammarDefineProduction> iterator() {
        return Arrays.stream(productions).iterator();
    }

    @Override
    public String toString() {
        return Arrays.stream(productions).map(Object::toString).collect(Collectors.joining("\n"));
    }
}

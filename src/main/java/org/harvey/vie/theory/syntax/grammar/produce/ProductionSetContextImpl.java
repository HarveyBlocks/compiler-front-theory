package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarAlternation;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

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
    public Iterable<HeadSymbol> headIterable() {
        return HeadIterator::new;
    }

    @Override
    public GrammarAlternation getAlternation(HeadSymbol head) {
        if (!head.isDefine()) {
            throw new IllegalStateException("The head of production is not define head symbol!");
        }
        HeadDefineSymbol define = head.toDefine();
        Integer index = indexOf(define);
        if (index == null) {
            throw new IllegalStateException("Can not found define from context!");
        }
        return get(index).getBody();
    }

    @Override
    public Iterator<GrammarDefineProduction> iterator() {
        return Arrays.stream(productions).iterator();
    }

    @Override
    public String toString() {
        return Arrays.stream(productions).map(Object::toString).collect(Collectors.joining("\n"));
    }

    private class HeadIterator implements Iterator<HeadSymbol> {
        private final Iterator<Map.Entry<String, Integer>> it = definitionIdxMap.entrySet().iterator();

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public HeadSymbol next() {
            return productions[it.next().getValue()].getHead();
        }
    }
}

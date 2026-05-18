package org.harvey.vie.theory.syntax.grammar.first;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 19:36
 */
public class FirstMapBuilder {
    private final ProductionSetContext context;
    private final Map<HeadSymbol, FirstSetBuilder> firstMap;
    private final Set<TerminalSymbol> terminalSet;

    FirstMapBuilder(ProductionSetContext context) {
        this.context = context;
        firstMap = new HashMap<>();
        terminalSet = new HashSet<>();
    }

    public FirstSetBuilder getBuilder(HeadSymbol head) {
        return firstMap.computeIfAbsent(head, k -> new FirstSetBuilder());
    }

    public GrammarAlternation getAlternation(HeadSymbol head) {
        return context.getAlternation(head);
    }

    /**
     * 构造terminalSet. 没有 addTerminal 是因为计算First集合不会去看所有的 concatenable, 只看First
     */
    public void addAllTerminal(GrammarConcatenation concatenation) {
        concatenation.stream()
                .filter(GrammarUnitSymbol::isTerminal)
                .map(GrammarUnitSymbol::toTerminal)
                .forEach(terminalSet::add);
    }

    public FirstMap build() {
        Map<HeadSymbol, FirstSet> built = firstMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build()));
        return new FirstMapImpl(built, terminalSet);
    }


    public int terminalSetSize() {
        return terminalSet.size();
    }
}

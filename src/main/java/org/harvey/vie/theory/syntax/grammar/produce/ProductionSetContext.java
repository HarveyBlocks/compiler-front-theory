package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarAlternation;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactory;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * TODO 上下文, 便于数据重复利用, 例如对于同一个head name definition, 产生同一个对象
 *  * 与 {@link ProductionSet} 的区别在于, {@link ProductionSet} 是成品, 本类是半成品, 是中间过程
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:36
 */
public interface ProductionSetContext extends Iterable<GrammarDefineProduction> {

    /**
     * @return null for not exist
     */
    HeadDefineSymbol getDefinition(String name);

    Integer indexOf(String name);

    GrammarDefineProduction get(int i);

    Integer indexOf(HeadDefineSymbol define);

    int length();

    boolean isEmpty();

    Iterable<HeadSymbol> headIterable();
    GrammarAlternation getAlternation(HeadSymbol head);

    TerminalFactory getTerminalFactory();

    default Stream<GrammarDefineProduction> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}

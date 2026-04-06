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

    ShiftReduceParsingTable produce(
            String startHead,
            ProductionSetContext context,
            FirstMap firstMap,
            ItemSetFamily family,
            LookaheadMap[] lookaheadMaps);
}

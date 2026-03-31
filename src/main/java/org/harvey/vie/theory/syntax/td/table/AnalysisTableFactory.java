package org.harvey.vie.theory.syntax.td.table;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.td.first.FirstMap;
import org.harvey.vie.theory.syntax.td.follow.FollowMap;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 18:59
 */
public interface AnalysisTableFactory {

    AnalysisTable produce(ProductionSetContext context, FirstMap firstMap, FollowMap followMap);

}

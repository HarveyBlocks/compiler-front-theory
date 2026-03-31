package org.harvey.vie.theory.syntax.td.follow;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.td.first.FirstMap;
import org.harvey.vie.theory.syntax.td.first.FirstSet;

import java.util.Map;

/**
 * TODO Follow(X)
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 00:36
 */
public interface FollowSetFactory {
    /**
     * @param startHead 想要多个开始? 定义一个新的H, H->A|B|C|D....
     */
    FollowMap follow(
            String startHead,
            ProductionSetContext context,
            FirstMap firstMap);
}

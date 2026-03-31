package org.harvey.vie.theory.syntax.td.follow;

import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 15:25
 */
public interface FollowMap {
    FollowSet get(HeadSymbol head);

    Set<HeadSymbol> keySet();

    Collection<FollowSet> values();

    Set<Map.Entry<HeadSymbol, FollowSet>> entrySet();

    int size();
}

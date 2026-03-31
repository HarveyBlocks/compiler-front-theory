package org.harvey.vie.theory.syntax.td.follow;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 15:26
 */
@AllArgsConstructor
public class FollowMapImpl implements FollowMap {
    private final Map<HeadSymbol,FollowSet> map;
    @Override
    public FollowSet get(HeadSymbol head) {
        return map.get(head);
    }

    @Override
    public Set<HeadSymbol> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<FollowSet> values() {
        return map.values();
    }

    @Override
    public Set<Map.Entry<HeadSymbol, FollowSet>> entrySet() {
        return map.entrySet();
    }
}

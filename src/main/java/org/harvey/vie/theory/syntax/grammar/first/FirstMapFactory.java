package org.harvey.vie.theory.syntax.grammar.first;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;

/**
 * TODO First(X)
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 00:36
 */
public interface FirstMapFactory {
    FirstMap first(ProductionSetContext context);
}

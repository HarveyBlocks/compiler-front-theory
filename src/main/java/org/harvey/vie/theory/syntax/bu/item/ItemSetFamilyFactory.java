package org.harvey.vie.theory.syntax.bu.item;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;

import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 23:40
 */
public interface ItemSetFamilyFactory {
    ItemSetFamily produce(String startHead, ProductionSetContext context);
}

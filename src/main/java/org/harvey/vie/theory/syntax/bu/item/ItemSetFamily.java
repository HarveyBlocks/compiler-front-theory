package org.harvey.vie.theory.syntax.bu.item;

import org.harvey.vie.theory.util.SimpleCollection;

/**
 * TODO 项集族
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 22:13
 */
public interface ItemSetFamily extends SimpleCollection<ItemSet> {
    ItemSet get(int i);

    int startIndex();

}

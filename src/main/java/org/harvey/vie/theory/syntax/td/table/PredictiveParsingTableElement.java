package org.harvey.vie.theory.syntax.td.table;

import java.util.stream.IntStream;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 21:33
 */
public interface PredictiveParsingTableElement {
    IntStream rightIdStream();

    Integer rightId();
}

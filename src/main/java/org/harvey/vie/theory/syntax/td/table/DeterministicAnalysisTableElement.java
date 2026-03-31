package org.harvey.vie.theory.syntax.td.table;

import lombok.AllArgsConstructor;

import java.util.stream.IntStream;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 21:33
 */
@AllArgsConstructor
public class DeterministicAnalysisTableElement implements AnalysisTableElement {
    private final Integer rightId;

    @Override
    public IntStream rightIdStream() {
        return rightId == null ? IntStream.of() : IntStream.of(new int[]{rightId});
    }

    @Override
    public Integer rightId() {
        return rightId;
    }
}

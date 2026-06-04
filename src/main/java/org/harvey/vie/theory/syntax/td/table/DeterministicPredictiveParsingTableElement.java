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
public class DeterministicPredictiveParsingTableElement implements PredictiveParsingTableElement {
    private final Integer rightId;

    /**
     * 函数功能：获取右部编号流。
     * 输入：
     * - 无。
     * 输出：IntStream 类型集合或迭代结果。
     */

    @Override
    public IntStream rightIdStream() {
        return rightId == null ? IntStream.of() : IntStream.of(rightId);
    }

    /**
     * 函数功能：获取右部编号。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    @Override
    public Integer rightId() {
        return rightId;
    }
}

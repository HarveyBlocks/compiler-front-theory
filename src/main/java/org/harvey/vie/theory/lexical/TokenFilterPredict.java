package org.harvey.vie.theory.lexical;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

import java.util.function.Predicate;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-10 14:14
 */
@FunctionalInterface
public interface TokenFilterPredict extends Predicate<SourceToken> {
    /**
     * 函数功能：判断源词法单元是否保留。
     * 输入：
     * - token：待判断的源词法单元。
     * 输出：是否保留该词法单元的布尔值。
     */
    @Override
    boolean test(SourceToken token);
}

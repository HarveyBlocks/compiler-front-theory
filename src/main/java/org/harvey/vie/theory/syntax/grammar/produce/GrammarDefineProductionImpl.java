package org.harvey.vie.theory.syntax.grammar.produce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarAlternation;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;

import java.util.Objects;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:46
 */
@AllArgsConstructor
@Getter
public class GrammarDefineProductionImpl implements GrammarDefineProduction {
    private final HeadDefineSymbol define;
    private final GrammarAlternation body;
/**
 * 函数功能：返回当前对象的字符串表示。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    @Override
    public String toString() {
        return define + " -> " + body;
    }
/**
 * 函数功能：判断当前对象是否与指定对象相等。
 * 输入：
 * - o：Object 类型参数。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GrammarDefineProduction)) {
            return false;
        }
        GrammarDefineProduction that = (GrammarDefineProduction) o;
        return Objects.equals(getDefine(), that.getDefine()) &&
               Objects.equals(getBody(), that.getBody());
    }

}

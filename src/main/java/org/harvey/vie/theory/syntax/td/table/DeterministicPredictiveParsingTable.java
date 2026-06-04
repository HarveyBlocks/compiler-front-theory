package org.harvey.vie.theory.syntax.td.table;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.follow.FollowMap;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

import java.util.Objects;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 21:36
 */
public class DeterministicPredictiveParsingTable implements PredictiveParsingTable {
    private final HeadSymbol[] heads;
    /**
     * index {@link PredictiveParsingTable#END_MARK_REFERENCE} is for end mark (null)
     */
    private final TerminalSymbol[] terminals;
    private final GrammarConcatenation[] concatenations;
    private final PredictiveParsingTableElement[][] table;
    private final FirstMap firstMap;
    private final FollowMap followMap;
    private final TerminalMatcher terminalMatcher;

    /**
     * 函数功能：创建 DeterministicPredictiveParsingTable 对象。
     * 输入：
     * - heads：HeadSymbol[] 类型参数。
     * - terminals：TerminalSymbol[] 类型参数。
     * - concatenations：GrammarConcatenation[] 类型参数。
     * - table：PredictiveParsingTableElement[][] 类型参数。
     * - firstMap：FirstMap 类型参数。
     * - followMap：FollowMap 类型参数。
     * - terminalMatcher：TerminalMatcher 类型参数。
     * 输出：无。
     */

    public DeterministicPredictiveParsingTable(
            HeadSymbol[] heads,
            TerminalSymbol[] terminals,
            GrammarConcatenation[] concatenations,
            PredictiveParsingTableElement[][] table,
            FirstMap firstMap,
            FollowMap followMap,
            TerminalMatcher terminalMatcher) {
        this.heads = heads;
        this.terminals = terminals;
        this.concatenations = concatenations;
        this.table = table;
        this.firstMap = firstMap;
        this.followMap = followMap;
        this.terminalMatcher = terminalMatcher;
    }

    /**
     * 函数功能：获取指定位置或键对应的元素。
     * 输入：
     * - head：HeadSymbol 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：AlterableSymbol 类型返回值。
     */

    @Override
    public AlterableSymbol get(HeadSymbol head, SourceToken token) {
        int terminalIndex =
                token == SourceTokenIterator.NO_MORE_TOKEN ? END_MARK_REFERENCE : terminalMatcher.indexOf(token);
        PredictiveParsingTableElement element = table[headIndexOf(head)][terminalIndex];
        return toConcatenation(element);
    }

    /**
     * 函数功能：获取终结符对应的起始语法符号。
     * 输入：
     * - factor：TerminalFactor 类型参数。
     * 输出：GrammarUnitSymbol 类型返回值。
     */

    @Override
    public GrammarUnitSymbol terminalStart(TerminalFactor factor) {
        for (TerminalSymbol terminal : terminals) {
            if (terminal.getFactor().equals(factor)) {
                return terminal;
            }
        }
        throw new IllegalArgumentException("Not find terminal which factor is:" + factor);
    }

    /**
     * 函数功能：获取非终结符定义对应的起始语法符号。
     * 输入：
     * - definition：String 类型参数。
     * 输出：GrammarUnitSymbol 类型返回值。
     */

    @Override
    public GrammarUnitSymbol headStart(String definition) {
        for (HeadSymbol headSymbol : heads) {
            if (headSymbol.isDefine() && headSymbol.toDefine().getName().equals(definition)) {
                return headSymbol;
            }
        }
        throw new IllegalArgumentException("Not find head definition which definition is:" + definition);
    }

    /**
     * 函数功能：获取非终结符头部的索引。
     * 输入：
     * - head：HeadSymbol 类型参数。
     * 输出：整数结果。
     */

    private int headIndexOf(HeadSymbol head) {
        for (int i = 0; i < heads.length; i++) {
            if (heads[i] == head) {
                return i;
            }
        }
        throw new IllegalStateException("Unknown head symbol");
    }

    /**
     * 函数功能：转换为语法符号连接体。
     * 输入：
     * - element：PredictiveParsingTableElement 类型参数。
     * 输出：AlterableSymbol 类型返回值。
     */

    private AlterableSymbol toConcatenation(PredictiveParsingTableElement element) {
        if (element.rightId() == null) {
            return null;
        }
        return element.rightId() == EPSILON_REFERENCE ? GrammarSymbol.epsilon() : concatenations[element.rightId()];
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

    @Override
    public String toString() {
        int rows = heads.length;
        int cols = terminals.length;

        // 缓存行头
        String[] rowHeaders = new String[rows];
        int maxRowLen = 0;
        for (int i = 0; i < rows; i++) {
            rowHeaders[i] = heads[i].toString();
            int len = rowHeaders[i].length();
            maxRowLen = Math.max(maxRowLen, len);
        }

        // 缓存列头（terminals[0] 为 null 时显示 "$"）
        String[] colHeaders = new String[cols];
        for (int j = 0; j < cols; j++) {
            colHeaders[j] = terminals[j].toString();
        }

        // 缓存所有单元格字符串
        String[][] cells = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = Objects.toString(toConcatenation(table[i][j]));
            }
        }

        // 计算每列宽度（列头 + 单元格）
        int[] colWidths = new int[cols];
        for (int j = 0; j < cols; j++) {
            int maxLen = colHeaders[j].length();
            for (int i = 0; i < rows; i++) {
                int len = cells[i][j].length();
                maxLen = Math.max(maxLen, len);
            }
            colWidths[j] = maxLen;
        }

        // 构建输出
        StringBuilder sb = new StringBuilder();
        // 表头行：第一列为空（留给行头）
        sb.append(String.format("%-" + maxRowLen + "s", ""));
        for (int j = 0; j < cols; j++) {
            sb.append(String.format("%-" + colWidths[j] + "s    ", colHeaders[j]));
        }
        sb.append('\n');

        // 数据行
        for (int i = 0; i < rows; i++) {
            sb.append(String.format("%-" + maxRowLen + "s", rowHeaders[i]));
            for (int j = 0; j < cols; j++) {
                sb.append(String.format("%-" + colWidths[j] + "s    ", cells[i][j]));
            }
            sb.append('\n');
        }

        return sb.toString();
    }


}

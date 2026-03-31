package org.harvey.vie.theory.syntax.td.table;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Objects;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 21:36
 */
public class DeterministicAnalysisTable implements AnalysisTable {
    private final HeadSymbol[] heads;
    /**
     * index {@link AnalysisTable#END_MARK_REFERENCE} is for end mark (null)
     */
    private final TerminalSymbol[] terminals;
    private final GrammarConcatenation[] concatenations;
    private final AnalysisTableElement[][] table;

    public DeterministicAnalysisTable(
            HeadSymbol[] heads,
            TerminalSymbol[] terminals,
            GrammarConcatenation[] concatenations,
            AnalysisTableElement[][] table) {
        this.heads = heads;
        this.terminals = terminals;
        this.concatenations = concatenations;
        this.table = table;
    }

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
            colHeaders[j] = (j == 0 && terminals[0] == null) ? "$" : terminals[j].toString();
        }

        // 缓存所有单元格字符串
        String[][] cells = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = Objects.toString( concatenationStream(table[i][j]));
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

    private GrammarSymbol concatenationStream(AnalysisTableElement element) {
        return element.rightId() == null ? null :
                (element.rightId() == EPSILON_REFERENCE ? GrammarSymbol.EPSILON : concatenations[element.rightId()]);
    }
}

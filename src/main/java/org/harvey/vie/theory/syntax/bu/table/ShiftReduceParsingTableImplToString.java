package org.harvey.vie.theory.syntax.bu.table;

import org.harvey.vie.theory.syntax.bu.table.element.ActiveTableElement;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable.NONE;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 11:48
 */
public class ShiftReduceParsingTableImplToString {

    // region show string
    public static String toString(ShiftReduceParsingTableImpl table) {
        ActiveTableElement[][] activeTable = table.getActiveTable();
        TerminalSymbol[] terminalSymbols = table.getTerminalSymbols();
        HeadSymbol[] headSymbols = table.getHeadSymbols();
        int[][] gotoTable = table.getGotoTable();
        SimpleGrammarProduction[] productionPool = table.getProductionPool();
        int stateCount = activeTable.length;
        int termCount = terminalSymbols.length;
        int nonTermCount = headSymbols.length;
        int padding = 2;

        // ========== 1. 计算各列最大内容宽度 ==========
        int stateWidth = Math.max("State".length(), String.valueOf(stateCount - 1).length());

        // 终结符列宽度（内容 + 符号本身）
        int[] termWidths = new int[termCount];
        for (int j = 0; j < termCount; j++) {
            termWidths[j] = terminalSymbols[j].toString().length();
        }
        for (int i = 0; i < stateCount; i++) {
            for (int j = 0; j < termCount; j++) {
                String cell = (activeTable[i][j] == null) ? "null" : activeTable[i][j].toString();
                if (cell.length() > termWidths[j]) {
                    termWidths[j] = cell.length();
                }
            }
        }

        // 非终结符列宽度（内容 + 符号本身）
        int[] nonTermWidths = new int[nonTermCount];
        for (int j = 0; j < nonTermCount; j++) {
            nonTermWidths[j] = headSymbols[j].toString().length();
        }
        for (int i = 0; i < stateCount; i++) {
            for (int j = 0; j < nonTermCount; j++) {
                String cell = (gotoTable[i][j] == NONE) ? "NaN" : Integer.toString(gotoTable[i][j]);
                if (cell.length() > nonTermWidths[j]) {
                    nonTermWidths[j] = cell.length();
                }
            }
        }

        // 加上 padding 后的最终列宽
        int finalStateWidth = stateWidth + padding;
        int[] finalTermWidths = new int[termCount];
        int[] finalNonTermWidths = new int[nonTermCount];
        for (int j = 0; j < termCount; j++) {
            finalTermWidths[j] = termWidths[j] + padding;
        }
        for (int j = 0; j < nonTermCount; j++) {
            finalNonTermWidths[j] = nonTermWidths[j] + padding;
        }

        // ========== 2. 计算 ACTION / GOTO 区域总宽（用于标题居中） ==========
        int actionTotalWidth = 0;
        for (int w : finalTermWidths) {
            actionTotalWidth += w;
        }
        int gotoTotalWidth = 0;
        for (int w : finalNonTermWidths) {
            gotoTotalWidth += w;
        }

        StringBuilder sb = new StringBuilder();
        // 构建文法
        sb.append("production pool: \n").append(IntStream.range(0, productionPool.length)
                .mapToObj(i -> i + "\t: " + productionPool[i])
                .collect(Collectors.joining("\n"))).append("\n");
        // ========== 3. 构建表头第一行（State + ACTION + GOTO） ==========
        sb.append(String.format("%-" + finalStateWidth + "s", "State"));
        // ACTION 居中
        String actionTitle = "ACTION";
        int actionLeftPad = (actionTotalWidth - actionTitle.length()) / 2;
        int actionRightPad = actionTotalWidth - actionTitle.length() - actionLeftPad;
        sb.append(" ".repeat(actionLeftPad)).append(actionTitle).append(" ".repeat(actionRightPad));
        // GOTO 居中
        String gotoTitle = "GOTO";
        int gotoLeftPad = (gotoTotalWidth - gotoTitle.length()) / 2;
        int gotoRightPad = gotoTotalWidth - gotoTitle.length() - gotoLeftPad;
        sb.append(" ".repeat(gotoLeftPad)).append(gotoTitle).append(" ".repeat(gotoRightPad));
        sb.append('\n');

        // ========== 4. 构建表头第二行（空 + 终结符符号 + 非终结符符号） ==========
        sb.append(String.format("%-" + finalStateWidth + "s", ""));
        for (int j = 0; j < termCount; j++) {
            sb.append(String.format("%-" + finalTermWidths[j] + "s", terminalSymbols[j].toString()));
        }
        for (int j = 0; j < nonTermCount; j++) {
            sb.append(String.format("%-" + finalNonTermWidths[j] + "s", headSymbols[j].toString()));
        }
        sb.append('\n');

        // ========== 5. 构建数据行 ==========
        for (int i = 0; i < stateCount; i++) {
            sb.append(String.format("%-" + finalStateWidth + "s", i));
            // ACTION 部分
            for (int j = 0; j < termCount; j++) {
                String cell = (activeTable[i][j] == null) ? "null" : activeTable[i][j].toString();
                sb.append(String.format("%-" + finalTermWidths[j] + "s", cell));
            }
            // GOTO 部分
            for (int j = 0; j < nonTermCount; j++) {
                String cell = (gotoTable[i][j] == NONE) ? "NaN" : Integer.toString(gotoTable[i][j]);
                sb.append(String.format("%-" + finalNonTermWidths[j] + "s", cell));
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    // endregion
}

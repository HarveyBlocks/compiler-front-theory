package org.harvey.vie.theory.syntax.bu.table;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.syntax.bu.table.element.ActiveTableElement;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalMatcher;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalMatcherFactory;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.util.CollectionUtil;

import java.util.Arrays;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 23:07
 */
public class ShiftReduceParsingTableImpl implements ShiftReduceParsingTable {
    private final int start;
    private final TerminalSymbol[] terminalSymbols;
    private final HeadSymbol[] headSymbols;

    private final Map<HeadSymbol, Integer> headDict;
    private final ActiveTableElement[][] activeTable;
    private final int[][] gotoTable;
    private final SimpleGrammarProduction[] productionPool;
    private final TerminalMatcher terminalMatcher;


    public ShiftReduceParsingTableImpl(
            int start,
            TerminalSymbol[] terminalSymbols,
            HeadSymbol[] headSymbols,
            ActiveTableElement[][] activeTable,
            int[][] gotoTable,
            SimpleGrammarProduction[] productionPool,
            TerminalMatcherFactory matcherFactory) {
        this.start = start;
        this.terminalSymbols = terminalSymbols;
        this.headSymbols = headSymbols;
        this.activeTable = activeTable;
        this.gotoTable = gotoTable;
        this.headDict = CollectionUtil.dict(headSymbols);
        this.productionPool = productionPool;
        this.terminalMatcher = matcherFactory.produce(terminalSymbols);
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int gotoNext(int originStatus, HeadSymbol head) {
        return gotoTable[originStatus][CollectionUtil.validIndex(headDict, head)];
    }

    @Override
    public ActiveTableElement activeNext(int originStatus, int terminal) {
        return activeTable[originStatus][terminal];
    }

    @Override
    public SimpleGrammarProduction getProduction(int i) {
        return productionPool[i];
    }

    @Override
    public int matchTerminal(SourceToken token) {
        return terminalMatcher.indexOf(token);
    }
    // region show string
    @Override
    public String toString() {

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
                String cell = (gotoTable[i][j] == -1) ? "NaN" : Integer.toString(gotoTable[i][j]);
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
        sb.append(Arrays.toString(productionPool)).append("\n");
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
                String cell = (gotoTable[i][j] == -1) ? "NaN" : Integer.toString(gotoTable[i][j]);
                sb.append(String.format("%-" + finalNonTermWidths[j] + "s", cell));
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    // endregion
}

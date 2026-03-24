package org.harvey.vie.theory.lexical.dfa.status;

import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.source.character.SourceCharacter;

import java.util.Arrays;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 11:31
 */
public class DfaStatusTable {
    public static final int UNKNOWN_CHAR_STATUS = -1;
    /**
     * status table
     */
    private final int[][] table;
    /**
     * sorted
     */
    private final SourceCharacter[] alphabet;
    @Getter
    private final int start;
    /**
     * true for accept
     */
    private final TokenType[] accepts;

    public DfaStatusTable(int[][] table, SourceCharacter[] alphabet, int start, TokenType[] accepts) {
        this.table = table;
        this.alphabet = alphabet;
        this.start = start;
        this.accepts = accepts;
    }

    /**
     * @return status next. {@link #UNKNOWN_CHAR_STATUS} for unknown char status
     */
    public int move(int statusNow, SourceCharacter ch) {
        // 调用Comparable
        int chIndex = Arrays.binarySearch(alphabet, ch);
        if (chIndex < 0) {
            return UNKNOWN_CHAR_STATUS;
        }
        return table[statusNow][chIndex];
    }

    public TokenType accept(int i) {
        return accepts[i];
    }

    @Override
    public String toString() {
        if (s != null) {
            return s;
        }
        synchronized (this) {
            if (s == null) {
                s = buildString();
            }
        }
        return s;
    }

    private volatile String s;

    private String buildString() {
        if (table == null || alphabet == null) {
            return "null";
        }
        int nStates = table.length;
        int nCols = alphabet.length;

        // 计算状态编号的固定宽度（零填充）
        int width = Integer.toString(nStates - 1).length();
        String zeroPad = "%0" + width + "d";

        // 状态列字符串：接受状态为 ((数字))，非接受状态为 "  数字  "（左右各两个空格）
        String[] stateStrings = new String[nStates];
        int maxStateColWidth = 0;
        for (int i = 0; i < nStates; i++) {
            String stateNum = String.format(zeroPad, i);
            String stateStr;
            if (accepts[i]!=null) {
                stateStr = String.format("%s %s", stateNum, accepts[i].hint());
            } else {
                stateStr = String.format("%s    ", stateNum);  // 左右各两个空格，保证长度与接受状态一致
            }
            stateStrings[i] = stateStr;
            maxStateColWidth = Math.max(maxStateColWidth, stateStr.length());
        }
        // 计算每一列转移值的最大宽度，并缓存所有转移字符串
        String[][] transStrings = new String[nStates][nCols];
        int[] colWidths = new int[nCols];
        for (int j = 0; j < nCols; j++) {
            colWidths[j] = alphabet[j].toString().length(); // 列标题宽度
            for (int i = 0; i < nStates; i++) {
                int target = table[i][j];
                String str;
                if (target == UNKNOWN_CHAR_STATUS) {
                    str = "NaN";
                } else {
                    str = String.format(zeroPad, target);
                }
                transStrings[i][j] = str;
                colWidths[j] = Math.max(colWidths[j], str.length());
            }
        }

        // 动态构建行格式字符串
        StringBuilder rowFormat = new StringBuilder("%" + maxStateColWidth + "s");
        for (int j = 0; j < nCols; j++) {
            rowFormat.append("  %").append(colWidths[j]).append("s");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Start state: ").append(start).append("\n");

        // 打印表头（第一列留空）
        String headerFirstCol = String.format("%" + maxStateColWidth + "s", " ");
        sb.append(headerFirstCol);
        for (int j = 0; j < nCols; j++) {
            sb.append(String.format("  %" + colWidths[j] + "s", alphabet[j].toString()));
        }
        sb.append("\n");

        // 打印每一行
        for (int i = 0; i < nStates; i++) {
            Object[] args = new Object[nCols + 1];
            args[0] = stateStrings[i];
            System.arraycopy(transStrings[i], 0, args, 1, nCols);
            sb.append(String.format(rowFormat.toString(), args)).append("\n");
        }

        return sb.toString();
    }
}

package org.harvey.vie.theory.lexical.dfa.status;

import lombok.Getter;
import org.harvey.vie.theory.io.*;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacterFactory;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

import static org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter.UNSUPPORTED;

/**
 * Represents a state transition first for a Deterministic Finite Automaton (DFA).
 * This class encapsulates the transition logic, mapping a current state and an
 * input character to a subsequent state. It also maintains information about
 * start states and acceptance types for each state.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 11:31
 */
public class RegexDfaStatusTable implements DfaStatusTable<AlphabetCharacter, TokenType>, Storage {
    public static final int UNKNOWN_CHAR_STATUS = -2;
    public static final int UNKNOWN_MOVE_STATUS = -1;
    /**
     * status first
     */
    private final int[][] table;
    /**
     * sorted
     */
    private final AlphabetCharacter[] alphabet;
    @Getter
    private final int start;
    /**
     * true for accept
     */
    private final TokenType[] accepts;
    private volatile String s;

    public RegexDfaStatusTable(int[][] table, AlphabetCharacter[] alphabet, int start, TokenType[] accepts) {
        this.table = table;
        this.alphabet = alphabet;
        this.start = start;
        this.accepts = accepts;
    }

    /**
     * @return status next. {@link #UNKNOWN_CHAR_STATUS} for unknown char status
     */
    @Override
    public int move(int statusNow, AlphabetCharacter ch) {
        if (ch == UNSUPPORTED) {
            throw new UnsupportedOperationException("Unsupported character");
        }
        // 调用Comparable
        int chIndex = Arrays.binarySearch(alphabet, ch);
        if (chIndex < 0) {
            return UNKNOWN_CHAR_STATUS;
        }
        return table[statusNow][chIndex];
    }

    @Override
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

    private String buildString() {
        if (table == null || alphabet == null) {
            return "null";
        }
        int nStates = table.length;
        int nCols = alphabet.length;

        // 状态编号零填充宽度
        int width = Integer.toString(nStates - 1).length();
        String zeroPad = "%0" + width + "d";

        // 1. 生成原始状态字符串（不带填充）
        String[] rawStateStrs = new String[nStates];
        for (int i = 0; i < nStates; i++) {
            String stateNum = String.format(zeroPad, i);
            if (accepts[i] != null) {
                rawStateStrs[i] = stateNum + " " + accepts[i].hint();
            } else {
                rawStateStrs[i] = stateNum;
            }
        }

        // 2. 计算状态列需要的最大宽度
        int maxStateColWidth = 0;
        for (String s : rawStateStrs) {
            maxStateColWidth = Math.max(maxStateColWidth, s.length());
        }

        // 3. 生成最终状态列字符串（统一宽度，分别对齐）
        String[] stateStrings = new String[nStates];
        for (int i = 0; i < nStates; i++) {
            if (accepts[i] != null) {
                // 接受状态：左对齐，右侧填充空格
                stateStrings[i] = String.format("%-" + maxStateColWidth + "s", rawStateStrs[i]);
            } else {
                // 非接受状态：居中对齐
                stateStrings[i] = center(rawStateStrs[i], maxStateColWidth);
            }
        }

        // 4. 计算转移列宽度及转移字符串（与原逻辑相同，保留右对齐）
        String[][] transStrings = new String[nStates][nCols];
        int[] colWidths = new int[nCols];
        for (int j = 0; j < nCols; j++) {
            colWidths[j] = alphabet[j].toString().length(); // 列标题宽度
            for (int i = 0; i < nStates; i++) {
                int target = table[i][j];
                String str = (target == UNKNOWN_MOVE_STATUS) ? "NaN" : String.format(zeroPad, target);
                transStrings[i][j] = str;
                colWidths[j] = Math.max(colWidths[j], str.length());
            }
        }

        // 5. 构建行格式：状态列左对齐，转移列右对齐
        StringBuilder rowFormat = new StringBuilder("%-" + maxStateColWidth + "s");
        for (int j = 0; j < nCols; j++) {
            rowFormat.append("  %").append(colWidths[j]).append("s");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Start state: ").append(start).append("\n");

        // 表头第一列：左对齐空字符串（与状态列对齐方式一致）
        // String headerFirstCol = String.format("%-" + maxStateColWidth + "s", " ");
        sb.append(" ".repeat(maxStateColWidth));
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

    private String center(String s, int width) {
        if (s.length() >= width) {
            return s;
        }
        int totalPadding = width - s.length();
        int leftPad = totalPadding / 2;
        int rightPad = totalPadding - leftPad;
        return " ".repeat(leftPad) + s + " ".repeat(rightPad);
    }

    @Override
    public int store(OutputStream os) throws IOException {
        byte[] alphabetData = alphabetToBytes();
        byte[] tableData = tableToBytes();

        int len = 0;
        len += Storages.storeInteger(os, start); // start
        len += Storages.storeInteger(os, table.length); // row
        len += Storages.storeInteger(os, table.length == 0 ? 0 : table[0].length); // col
        len += Storages.store(os, alphabetData); // alphabet
        // token types
        for (TokenType accept : accepts) {
            len += accept == null ? Storages.storeInteger(os, -1) : accept.store(os);
        }
        len += Storages.store(os, tableData);//first
        return len;
    }

    private byte[] alphabetToBytes() {
        ByteOutStream alphabetBos = new ByteOutStream();
        Arrays.stream(alphabet)
                .map(AlphabetCharacter::uniqueCode)
                .map(ToBytes::fromInt)
                .forEach(row -> alphabetBos.write(row, 0, row.length));
        return alphabetBos.toByteArray();
    }

    private byte[] tableToBytes() {
        ByteOutStream tableBos = new ByteOutStream();
        Arrays.stream(table)
                .flatMapToInt(Arrays::stream)
                .mapToObj(ToBytes::fromInt)
                .forEach(n -> tableBos.write(n, 0, n.length));
        return tableBos.toByteArray();
    }

    public static class Loader implements ILoader<RegexDfaStatusTable> {
        private final TokenType.Loader<?> acceptLoader;
        private final AlphabetCharacterFactory alphabetFactory;

        public Loader(TokenType.Loader<? extends TokenType> acceptLoader, AlphabetCharacterFactory alphabetFactory) {
            this.acceptLoader = acceptLoader;
            this.alphabetFactory = alphabetFactory;
        }

        private static int[][] loadTable(InputStream is, int row, int col) throws IOException {
            IntStream stream = FromBytes.toIntArray(Loaders.loadBytes(is, row * col * 4));
            int[][] result = new int[row][col];
            PrimitiveIterator.OfInt it = stream.iterator();
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    result[i][j] = it.nextInt();
                }
            }
            return result;
        }

        @Override
        public RegexDfaStatusTable load(InputStream is) throws IOException {
            int start = Loaders.loadInteger(is);
            int row = Loaders.loadInteger(is);
            int col = Loaders.loadInteger(is);
            AlphabetCharacter[] alphabet = loadAlphabet(is, col);
            TokenType[] accepts = loadAccepts(is, row);
            int[][] table = loadTable(is, row, col);
            return new RegexDfaStatusTable(table, alphabet, start, accepts);
        }

        private AlphabetCharacter[] loadAlphabet(InputStream is, int len) throws IOException {
            return FromBytes.toIntArray(Loaders.loadBytes(is, len << 2))
                    .mapToObj(alphabetFactory::byUniqueCode)
                    .toArray(AlphabetCharacter[]::new);
        }

        private TokenType[] loadAccepts(InputStream is, int row) throws IOException {
            TokenType[] accepts = new TokenType[row];
            for (int i = 0; i < row; i++) {
                accepts[i] = acceptLoader.load(is);
            }
            return accepts;
        }
    }
}

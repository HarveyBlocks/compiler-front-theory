package org.harvey.vie.theory.demo.program;

import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenImpl;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reclassifies declared struct names into a dedicated token when they are used
 * in type positions. This keeps the grammar explicit while avoiding the
 * classic IDENTIFIER/type-name ambiguity.
 */
public class ProgramStructAwareTokenIterator implements SourceTokenIterator {
    private final SourceTokenIterator delegate;
    private final List<Entry> rawEntries = new ArrayList<>();
    private final List<Entry> normalizedEntries = new ArrayList<>();
    private final Set<String> declaredStructs = new HashSet<>();
    private int normalizedCursor;
    private int index;
    private boolean endReached;

    /**
     * 函数功能：创建结构体类型感知的词法单元迭代器。
     * 输入：
     * - delegate：原始词法单元迭代器。
     * 输出：无。
     */
    public ProgramStructAwareTokenIterator(SourceTokenIterator delegate) {
        this.delegate = delegate;
    }

    /**
     * 函数功能：判断是否还有可读取的词法单元。
     * 输入：
     * - 无。
     * 输出：是否存在下一个词法单元的布尔值。
     */
    @Override
    public boolean hasNext() {
        ensureNormalized(index);
        if (index >= normalizedEntries.size()) {
            return false;
        }
        Entry entry = normalizedEntries.get(index);
        return entry.error != null || entry.token != NO_MORE_TOKEN;
    }

    /**
     * 函数功能：读取下一个归一化后的词法单元。
     * 输入：
     * - 无。
     * 输出：下一个 SourceToken；结束时返回 NO_MORE_TOKEN。
     */
    @Override
    public SourceToken next() throws CompileException {
        ensureNormalized(index);
        if (index >= normalizedEntries.size()) {
            return NO_MORE_TOKEN;
        }
        Entry entry = normalizedEntries.get(index++);
        if (entry.error != null) {
            throw entry.error;
        }
        return entry.token;
    }

    /**
     * 函数功能：获取底层词法迭代器当前偏移量。
     * 输入：
     * - 无。
     * 输出：当前偏移量整数。
     */
    @Override
    public int getOffset() {
        return delegate.getOffset();
    }

    /**
     * 函数功能：读取当前归一化后的词法单元但不推进迭代器。
     * 输入：
     * - 无。
     * 输出：当前 SourceToken；结束时返回 NO_MORE_TOKEN。
     */
    @Override
    public SourceToken current() throws CompileException {
        ensureNormalized(index);
        if (index >= normalizedEntries.size()) {
            return NO_MORE_TOKEN;
        }
        Entry entry = normalizedEntries.get(index);
        if (entry.error != null) {
            throw entry.error;
        }
        return entry.token;
    }

    /**
     * 函数功能：关闭底层词法单元迭代器。
     * 输入：
     * - 无。
     * 输出：无。
     */
    @Override
    public void close() throws Exception {
        delegate.close();
    }

    /**
     * 函数功能：确保指定位置之前的词法单元已完成归一化。
     * 输入：
     * - target：需要归一化到的目标索引。
     * 输出：无。
     */
    private void ensureNormalized(int target) {
        while (normalizedEntries.size() <= target) {
            ensureRaw(normalizedCursor);
            if (normalizedCursor >= rawEntries.size()) {
                return;
            }
            Entry raw = rawEntries.get(normalizedCursor);
            Entry normalized = raw.error == null
                    ? new Entry(normalizeToken(normalizedCursor, raw.token), null)
                    : raw;
            normalizedEntries.add(normalized);
            updateDeclaredStructs(normalizedCursor, raw);
            normalizedCursor++;
        }
    }

    /**
     * 函数功能：确保指定位置之前的原始词法单元已被读取。
     * 输入：
     * - target：需要读取到的目标索引。
     * 输出：无。
     */
    private void ensureRaw(int target) {
        while (!endReached && rawEntries.size() <= target) {
            try {
                SourceToken token = delegate.next();
                rawEntries.add(new Entry(token, null));
                if (token == NO_MORE_TOKEN) {
                    endReached = true;
                }
            } catch (CompileException e) {
                rawEntries.add(new Entry(null, e));
            }
        }
    }

    /**
     * 函数功能：按结构体类型使用位置归一化词法单元类型。
     * 输入：
     * - currentIndex：当前词法单元在原始序列中的索引。
     * - token：待归一化的源词法单元。
     * 输出：归一化后的 SourceToken。
     */
    private SourceToken normalizeToken(int currentIndex, SourceToken token) {
        if (token == null || token == NO_MORE_TOKEN || token.getType() != ProgramTokenType.IDENTIFIER) {
            return token;
        }
        String name = SourceTokenStringMapping.utf8(token);
        if (!declaredStructs.contains(name)) {
            return token;
        }
        if (!isStructTypeUse(currentIndex)) {
            return token;
        }
        return SourceTokenImpl.create(ProgramTokenType.TYPE_IDENTIFIER, new byte[][]{token.getLexeme()}, token.getOffset());
    }

    /**
     * 函数功能：判断指定索引处的标识符是否作为结构体类型使用。
     * 输入：
     * - currentIndex：待判断词法单元在原始序列中的索引。
     * 输出：是否为结构体类型使用位置的布尔值。
     */
    private boolean isStructTypeUse(int currentIndex) {
        int previous = previousSignificantIndex(currentIndex);
        if (previous >= 0 && tokenType(previous) == ProgramTokenType.KEYWORD_NEW) {
            return true;
        }
        return hasDeclaratorFollower(currentIndex);
    }

    /**
     * 函数功能：判断指定索引之后是否存在声明器后继。
     * 输入：
     * - currentIndex：待判断词法单元在原始序列中的索引。
     * 输出：是否存在声明器后继的布尔值。
     */
    private boolean hasDeclaratorFollower(int currentIndex) {
        int cursor = nextSignificantIndex(currentIndex);
        while (cursor >= 0 && tokenType(cursor) == ProgramTokenType.OPERATOR_SQUARE_OPEN) {
            int close = nextSignificantIndex(cursor);
            if (close < 0 || tokenType(close) != ProgramTokenType.OPERATOR_SQUARE_CLOSE) {
                return false;
            }
            cursor = nextSignificantIndex(close);
        }
        return cursor >= 0 && tokenType(cursor) == ProgramTokenType.IDENTIFIER;
    }

    /**
     * 函数功能：根据结构体声明位置更新已声明结构体名称集合。
     * 输入：
     * - currentIndex：当前原始词法单元索引。
     * - raw：当前原始词法单元条目。
     * 输出：无。
     */
    private void updateDeclaredStructs(int currentIndex, Entry raw) {
        if (raw.error != null || raw.token == null || raw.token.getType() != ProgramTokenType.IDENTIFIER) {
            return;
        }
        int previous = previousSignificantIndex(currentIndex);
        int next = nextSignificantIndex(currentIndex);
        if (previous >= 0
                && tokenType(previous) == ProgramTokenType.KEYWORD_STRUCT
                && next >= 0
                && tokenType(next) == ProgramTokenType.OPERATOR_BRACE_OPEN) {
            declaredStructs.add(SourceTokenStringMapping.utf8(raw.token));
        }
    }

    /**
     * 函数功能：查找当前索引之前最近的有效词法单元索引。
     * 输入：
     * - currentIndex：当前原始词法单元索引。
     * 输出：前一个有效词法单元索引；不存在时返回 -1。
     */
    private int previousSignificantIndex(int currentIndex) {
        for (int i = currentIndex - 1; i >= 0; i--) {
            if (isSignificant(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 函数功能：查找当前索引之后最近的有效词法单元索引。
     * 输入：
     * - currentIndex：当前原始词法单元索引。
     * 输出：下一个有效词法单元索引；不存在时返回 -1。
     */
    private int nextSignificantIndex(int currentIndex) {
        int i = currentIndex + 1;
        while (true) {
            ensureRaw(i);
            if (i >= rawEntries.size()) {
                return -1;
            }
            if (isSignificant(i)) {
                return i;
            }
            i++;
        }
    }

    /**
     * 函数功能：判断指定原始索引处的词法单元是否参与语法判断。
     * 输入：
     * - index：原始词法单元索引。
     * 输出：是否为有效词法单元的布尔值。
     */
    private boolean isSignificant(int index) {
        Entry entry = rawEntries.get(index);
        if (entry.error != null || entry.token == null || entry.token == NO_MORE_TOKEN) {
            return false;
        }
        ProgramTokenType type = (ProgramTokenType) entry.token.getType();
        return type != ProgramTokenType.SPACE
                && type != ProgramTokenType.COMMENT_BLOCK
                && type != ProgramTokenType.COMMENT_LINE;
    }

    /**
     * 函数功能：获取指定原始索引处词法单元的程序词法类型。
     * 输入：
     * - index：原始词法单元索引。
     * 输出：ProgramTokenType 类型；词法单元为空时返回 null。
     */
    private ProgramTokenType tokenType(int index) {
        Entry entry = rawEntries.get(index);
        return entry.token == null ? null : (ProgramTokenType) entry.token.getType();
    }

    private static final class Entry {
        private final SourceToken token;
        private final CompileException error;

        /**
         * 函数功能：创建词法单元或编译异常条目。
         * 输入：
         * - token：读取到的源词法单元。
         * - error：读取过程中捕获的编译异常。
         * 输出：无。
         */
        private Entry(SourceToken token, CompileException error) {
            this.token = token;
            this.error = error;
        }
    }
}

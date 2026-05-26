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

    public ProgramStructAwareTokenIterator(SourceTokenIterator delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        ensureNormalized(index);
        if (index >= normalizedEntries.size()) {
            return false;
        }
        Entry entry = normalizedEntries.get(index);
        return entry.error != null || entry.token != NO_MORE_TOKEN;
    }

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

    @Override
    public int getOffset() {
        return delegate.getOffset();
    }

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

    @Override
    public void close() throws Exception {
        delegate.close();
    }

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

    private boolean isStructTypeUse(int currentIndex) {
        int previous = previousSignificantIndex(currentIndex);
        if (previous >= 0 && tokenType(previous) == ProgramTokenType.KEYWORD_NEW) {
            return true;
        }
        return hasDeclaratorFollower(currentIndex);
    }

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

    private int previousSignificantIndex(int currentIndex) {
        for (int i = currentIndex - 1; i >= 0; i--) {
            if (isSignificant(i)) {
                return i;
            }
        }
        return -1;
    }

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

    private ProgramTokenType tokenType(int index) {
        Entry entry = rawEntries.get(index);
        return entry.token == null ? null : (ProgramTokenType) entry.token.getType();
    }

    private static final class Entry {
        private final SourceToken token;
        private final CompileException error;

        private Entry(SourceToken token, CompileException error) {
            this.token = token;
            this.error = error;
        }
    }
}

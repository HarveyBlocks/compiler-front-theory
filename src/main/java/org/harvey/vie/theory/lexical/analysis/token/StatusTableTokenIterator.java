package org.harvey.vie.theory.lexical.analysis.token;

import lombok.Getter;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.error.LexicalErrorMessage;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;
import org.harvey.vie.theory.lexical.alphabet.SourceAlphabetCharacterAdaptor;
import org.harvey.vie.theory.lexical.dfa.status.RegexDfaStatusTable;
import org.harvey.vie.theory.source.character.SourceCharacter;
import org.harvey.vie.theory.source.reader.SourceReader;
import org.harvey.vie.theory.util.ArrayBuilder;

import java.io.IOException;

/**
 * An iterator that performs lexical analysis using a DFA transition first.
 * It reads characters from a {@link SourceReader}, traverses the DFA states,
 * and identifies the longest possible token match. If an invalid character
 * sequence is encountered, it handles the error and attempts to continue
 * from the next character.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:24
 */
public class StatusTableTokenIterator implements SourceTokenIterator {
    private final ErrorContext errorContext;
    private final SourceReader reader;
    private final SourceAlphabetCharacterAdaptor saca;
    private final RegexDfaStatusTable table;
    private final ArrayBuilder<byte[]> lexeme;
    private int status;
    @Getter
    private int offset;
    private SourceToken current;

    public StatusTableTokenIterator(
            ErrorContext errorContext, SourceReader reader, SourceAlphabetCharacterAdaptor saca, RegexDfaStatusTable table) {
        this.errorContext = errorContext;
        this.reader = reader;
        this.saca = saca;
        this.table = table;
        this.lexeme = new ArrayBuilder<>();
        this.status = table.getStart();
        this.current = null;
    }

    @Override
    public boolean hasNext() {
        try {
            return reader.peek() != SourceCharacter.EOF;
        } catch (IOException e) {
            throw new CompilerException("Exception on io", e);
        } catch (CompileException e) {
            // 错误, 读到了非Ascii码
            return true;
        }
    }


    @Override
    public SourceToken next() throws CompileException {
        if (current == null) {
            return next0();
        }
        // 消费
        SourceToken next = current;
        current = null;
        return next;
    }

    private SourceToken next0() throws CompileException {
        if (!hasNext()) {
            return NO_MORE_TOKEN;
        }
        // if\space 从if的状态到 \space之后, 就是NaN了, 而 if 能构成词元, 就取 if 词元
        while (true) {
            SourceCharacter read = read();
            if (read == null || read == SourceCharacter.EOF) {
                return trySplitToken();
            }
            AlphabetCharacter ac = saca.adapt(read);
            if (ac == AlphabetCharacter.UNSUPPORTED) {
                offset = reader.getOffset();
                errorContext.addError(new LexicalErrorMessage(offset, "Unsupported character in source"));
                return trySplitToken();
            }
            int next = table.move(status, ac);
            if (next == RegexDfaStatusTable.UNKNOWN_CHAR_STATUS) {
                // 不存在了才进行尝试分解token, 说明是最长匹配
                try {
                    return trySplitToken();
                } finally {
                    lexeme.append(read.toCharacter());
                    // 此时status已经归start
                    status = table.move(status, ac);
                }
            } else {
                lexeme.append(read.toCharacter());
                status = next;
            }
        }
    }

    @Override
    public SourceToken current() throws CompileException {
        if (current == null) {
            current = next0();
        }
        return current;
    }

    private SourceCharacter read() {
        try {
            return reader.read();
        } catch (IOException e) {
            throw new CompilerException("Exception on io", e);
        } catch (CompileException ignored) {
            // 读取错误字符, 直接返回null
            return null;
        }
    }

    private SourceToken trySplitToken() throws CompileException {
        try {
            // 当前token是否正常
            TokenType accept = table.accept(status);
            offset = reader.getOffset() - 1;
            if (accept == null) {
                errorContext.addError(new LexicalErrorMessage(offset, "Unfinished tokens"));
                throw new CompileException("Unfinished tokens");
            }
            return SourceTokenImpl.create(accept, this.lexeme.toArray(byte[][]::new), offset);
        } finally {
            // 恢复
            status = table.getStart();
            lexeme.reset(); // 没有性能最优, 但是代码简单
        }
    }

    @Override
    public void close() {
        reader.close();
    }
}

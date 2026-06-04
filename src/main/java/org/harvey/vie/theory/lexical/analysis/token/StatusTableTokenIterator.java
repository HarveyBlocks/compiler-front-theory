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
 * An iterator that performs lexical phase using a DFA transition first.
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

    /**
     * 函数功能：创建基于状态表的源词法单元迭代器。
     * 输入：
     * - errorContext：用于收集词法错误的错误上下文。
     * - reader：用于读取源字符的读取器。
     * - saca：源字符到字母表字符的适配器。
     * - table：词法 DFA 状态表。
     * 输出：无。
     */
    public StatusTableTokenIterator(
            ErrorContext errorContext,
            SourceReader reader,
            SourceAlphabetCharacterAdaptor saca,
            RegexDfaStatusTable table) {
        this.errorContext = errorContext;
        this.reader = reader;
        this.saca = saca;
        this.table = table;
        this.lexeme = new ArrayBuilder<>();
        this.status = table.getStart();
        this.current = null;
    }

    /**
     * 函数功能：判断是否还有可读取的源词法单元。
     * 输入：
     * - 无。
     * 输出：是否存在下一个词法单元的布尔值。
     */
    @Override
    public boolean hasNext() {
        try {
            if (reader.peek() != SourceCharacter.EOF) {
                return true;
            }
        } catch (IOException e) {
            throw new CompilerException("Exception on io", e);
        } catch (CompileException e) {
            // 错误, 读到了非Ascii码
            return true;
        }
        if (current != null) {
            return true;
        }
        return !lexeme.isEmpty();
    }

    /**
     * 函数功能：获取当前源词法单元。
     * 输入：
     * - 无。
     * 输出：当前 SourceToken。
     */
    @Override
    public SourceToken current() throws CompileException {
        if (current == null) {
            current = next0();
        }
        return current;
    }

    /**
     * 函数功能：读取下一个源词法单元。
     * 输入：
     * - 无。
     * 输出：下一个 SourceToken。
     */
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

    /**
     * 函数功能：执行一次词法扫描并生成下一个源词法单元。
     * 输入：
     * - 无。
     * 输出：扫描得到的 SourceToken。
     */
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
                return unsupportedCharacter();
            }

            int next = table.move(status, ac);
            if (next == RegexDfaStatusTable.UNKNOWN_CHAR_STATUS) {
                return unsupportedCharacter();
            }
            if (next == RegexDfaStatusTable.UNKNOWN_MOVE_STATUS) {
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

    /**
     * 函数功能：处理不支持字符并尝试分割当前词法单元。
     * 输入：
     * - 无。
     * 输出：分割得到的 SourceToken。
     */
    private SourceToken unsupportedCharacter() throws CompileException {
        offset = reader.getOffset();
        errorContext.addError(new LexicalErrorMessage(offset, "Unsupported character in source"));
        return trySplitToken();
    }

    /**
     * 函数功能：从源读取器读取一个源字符。
     * 输入：
     * - 无。
     * 输出：读取到的 SourceCharacter；读取编译异常时返回 null。
     */
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

    /**
     * 函数功能：尝试按当前状态分割出源词法单元。
     * 输入：
     * - 无。
     * 输出：分割得到的 SourceToken。
     */
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

    /**
     * 函数功能：关闭源字符读取器。
     * 输入：
     * - 无。
     * 输出：无。
     */
    @Override
    public void close() {
        reader.close();
    }
}

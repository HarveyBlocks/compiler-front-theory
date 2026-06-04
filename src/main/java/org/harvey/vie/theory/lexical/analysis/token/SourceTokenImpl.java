package org.harvey.vie.theory.lexical.analysis.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.io.ByteOutStream;

/**
 * Implementation of {@link SourceToken} representing a recognized token in the
 * source code. It stores the token's type, the exact character sequence (lexeme),
 * and its position in the source.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:17
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SourceTokenImpl implements SourceToken {
    private final TokenType type;
    private final byte[] lexeme;
    private final int offset;

    /**
     * 函数功能：创建源词法单元对象。
     * 输入：
     * - type：词法单元类型。
     * - lexeme：词素分段字节数组。
     * - offset：词法单元偏移量。
     * 输出：创建得到的 SourceToken。
     */
    public static SourceToken create(TokenType type, byte[][] lexeme, int offset) {
        return new SourceTokenImpl(type, ByteOutStream.flap(lexeme), offset);
    }

    /**
     * 函数功能：获取源词法单元的提示字符串。
     * 输入：
     * - 无。
     * 输出：源词法单元提示字符串。
     */
    @Override
    public String hintString() {
        return String.format("%d:%s`%s`", offset, type.hint(), SourceTokenStringMapping.utf8(this));
    }


    /**
     * 函数功能：获取源词法单元的字符串表示。
     * 输入：
     * - 无。
     * 输出：源词法单元字符串。
     */
    @Override
    public String toString() {
        return hintString();
    }

}

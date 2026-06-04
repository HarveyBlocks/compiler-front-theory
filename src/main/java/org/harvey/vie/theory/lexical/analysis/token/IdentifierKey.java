package org.harvey.vie.theory.lexical.analysis.token;

import java.util.Arrays;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 21:44
 */
public class IdentifierKey {
    private final byte[] lexeme;

    /**
     * 函数功能：创建标识符键对象。
     * 输入：
     * - lexeme：标识符词素字节数组。
     * 输出：无。
     */
    private IdentifierKey(byte[] lexeme) {this.lexeme = lexeme;}

    /**
     * 函数功能：根据源词法单元生成标识符键。
     * 输入：
     * - token：提供标识符词素的源词法单元。
     * 输出：生成的 IdentifierKey。
     */
    public static IdentifierKey generate(SourceToken token){
        return generate(token.getLexeme());
    }

    /**
     * 函数功能：根据词素字节数组生成标识符键。
     * 输入：
     * - lexeme：标识符词素字节数组。
     * 输出：生成的 IdentifierKey。
     */
    public static IdentifierKey generate(byte[] lexeme) {
        return new IdentifierKey(lexeme);
    }

    /**
     * 函数功能：判断对象是否与当前标识符键相等。
     * 输入：
     * - o：待比较的对象。
     * 输出：是否相等的布尔值。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdentifierKey)) {
            return false;
        }
        IdentifierKey that = (IdentifierKey) o;
        return Arrays.equals(lexeme, that.lexeme);
    }

    /**
     * 函数功能：计算标识符键的哈希值。
     * 输入：
     * - 无。
     * 输出：标识符键哈希值整数。
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(lexeme);
    }
}

package org.harvey.vie.theory.lexical.analysis.token;

import java.nio.charset.StandardCharsets;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 22:41
 */
public class SourceTokenStringMapping {
    /**
     * 函数功能：阻止创建源词法单元字符串映射工具实例。
     * 输入：
     * - 无。
     * 输出：无。
     */
    private SourceTokenStringMapping() {}

    /**
     * 函数功能：将源词法单元词素转换为 UTF-8 字符串。
     * 输入：
     * - token：待转换的源词法单元。
     * 输出：词素对应的 UTF-8 字符串。
     */
    public static String utf8(SourceToken token) {
        return utf8(token.getLexeme());
    }

    /**
     * 函数功能：将词素字节数组转换为 UTF-8 字符串。
     * 输入：
     * - lexeme：待转换的词素字节数组。
     * 输出：词素对应的 UTF-8 字符串。
     */
    public static String utf8(byte[] lexeme) {
        return new String(lexeme, StandardCharsets.UTF_8);
    }
}

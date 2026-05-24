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
    private SourceTokenStringMapping() {}

    public static String utf8(SourceToken token) {
        return utf8(token.getLexeme());
    }

    public static String utf8(byte[] lexeme) {
        return new String(lexeme, StandardCharsets.UTF_8);
    }
}

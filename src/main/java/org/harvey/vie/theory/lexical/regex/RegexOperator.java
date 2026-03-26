package org.harvey.vie.theory.lexical.regex;

import lombok.Getter;

import java.util.Arrays;

/**
 * Enumeration of special characters (operators) used in regular expression syntax.
 * These operators define the structure and behavior of regex patterns, such
 * as grouping, repetition, and alternation.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 10:58
 */
@Getter
public enum RegexOperator {
    PARENTHESES_PRE((byte) '('),
    PARENTHESES_POST((byte) ')'),
    CLOSURE((byte) '*'),
    OR((byte) '|'),
    ;

    private final byte c;

    RegexOperator(byte c) {
        this.c = c;
    }

    public static RegexOperator regexOperator(byte c) {
        return Arrays.stream(RegexOperator.values()).filter(op -> c == op.c).findFirst().orElse(null);
    }

    public static boolean isRegexOperator(byte c) {
        return regexOperator(c) != null;
    }
}

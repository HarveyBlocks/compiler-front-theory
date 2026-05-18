package org.harvey.vie.theory.lexical.regex;

import lombok.Getter;
import org.harvey.vie.theory.util.CollectionUtil;

import java.util.Map;

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
    ESCAPE((byte) '\\'),
    ;

    private final byte c;
    private static final Map<Byte, RegexOperator> ENUM_DICT = CollectionUtil.dictOnEnum(
            RegexOperator.values(),
            RegexOperator::getC
    );

    RegexOperator(byte c) {
        this.c = c;
    }

    public static RegexOperator regexOperator(byte c) {
        return ENUM_DICT.get(c);
    }

    public static boolean isRegexOperator(byte c) {
        return regexOperator(c) != null;
    }
}

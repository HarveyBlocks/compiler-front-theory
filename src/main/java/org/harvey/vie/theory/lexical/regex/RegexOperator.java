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

    /**
     * 函数功能：创建指定字符编码的正则操作符枚举项。
     * 输入：
     * - c：正则操作符的字符编码。
     * 输出：无。
     */
    RegexOperator(byte c) {
        this.c = c;
    }

    /**
     * 函数功能：根据字符编码获取对应的正则操作符。
     * 输入：
     * - c：待匹配的字符编码。
     * 输出：对应的正则操作符；不存在则返回 null。
     */
    public static RegexOperator regexOperator(byte c) {
        return ENUM_DICT.get(c);
    }

    /**
     * 函数功能：判断指定字符编码是否为正则操作符。
     * 输入：
     * - c：待判断的字符编码。
     * 输出：若为正则操作符则返回 true，否则返回 false。
     */
    public static boolean isRegexOperator(byte c) {
        return regexOperator(c) != null;
    }
}

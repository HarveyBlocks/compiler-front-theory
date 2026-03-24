package org.harvey.vie.theory.lexical.regex;

import java.util.Arrays;

/**
 * TODO 可以使用转义
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 10:58
 */
public enum RegexOperator {
    PARENTHESES_PRE('('),
    PARENTHESES_POST(')'),
    CLOSURE('*'),
    OR('|'), ;

    private final char c;

    RegexOperator(char c) {
        this.c = c;
    }
    public static boolean isRegexOperator(char c){
        return Arrays.stream(RegexOperator.values()).anyMatch(op -> c == op.c);
    }
}

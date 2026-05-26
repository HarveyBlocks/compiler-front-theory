package org.harvey.vie.theory.util;

/**
 * Centralized runtime property access.
 *
 * @author Temper
 */
public final class RuntimeProperties {
    public static final String LEXICAL_FLUSH_TABLE = "lexical.flushTable";
    public static final String SYNTAX_FLUSH_TABLE = "syntax.flushTable";
    public static final String PROGRAM_TEST_CASE = "program.testCase";

    private RuntimeProperties() {
    }

    public static boolean lexicalFlushTable() {
        return Boolean.getBoolean(LEXICAL_FLUSH_TABLE);
    }

    public static boolean syntaxFlushTable() {
        return Boolean.getBoolean(SYNTAX_FLUSH_TABLE);
    }

    public static String programTestCase() {
        return System.getProperty(PROGRAM_TEST_CASE);
    }
}

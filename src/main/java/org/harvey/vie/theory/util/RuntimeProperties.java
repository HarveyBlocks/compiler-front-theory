package org.harvey.vie.theory.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Centralized runtime property access.
 *
 * Configuration source priority:
 * 1. JVM system properties
 * 2. Config file
 *
 * Config file path priority:
 * 1. JVM system property {@value #CONFIG_PATH_PROPERTY}
 * 2. {@value #DEFAULT_CONFIG_PATH}
 *
 * @author Temper
 */
public final class RuntimeProperties {
    public static final String CONFIG_PATH_PROPERTY = "config.properties";
    public static final String DEFAULT_CONFIG_PATH = "src/main/resources/config.properties";

    public static final String LEXICAL_FLUSH_TABLE = "lexical.flushTable";
    public static final String SYNTAX_FLUSH_TABLE = "syntax.flushTable";
    public static final String PROGRAM_TEST_CASE = "program.testCase";

    private static final Properties FILE_PROPERTIES = loadFileProperties();

    private RuntimeProperties() {
    }

    public static boolean lexicalFlushTable() {
        return booleanProperty(LEXICAL_FLUSH_TABLE, false);
    }

    public static boolean syntaxFlushTable() {
        return booleanProperty(SYNTAX_FLUSH_TABLE, false);
    }

    public static String programTestCase() {
        return stringProperty(PROGRAM_TEST_CASE);
    }

    public static Path configPath() {
        String configured = System.getProperty(CONFIG_PATH_PROPERTY);
        if (configured == null || configured.isBlank()) {
            return Path.of(DEFAULT_CONFIG_PATH);
        }
        return Path.of(configured);
    }

    private static boolean booleanProperty(String key, boolean defaultValue) {
        String value = stringProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    private static String stringProperty(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return systemValue;
        }
        String fileValue = FILE_PROPERTIES.getProperty(key);
        if (fileValue == null) {
            return null;
        }
        return fileValue.trim();
    }

    private static Properties loadFileProperties() {
        Properties properties = new Properties();
        Path path = configPath();
        if (!Files.exists(path)) {
            return properties;
        }
        try (InputStream input = Files.newInputStream(path)) {
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("failed to load config file: " + path, e);
        }
    }
}

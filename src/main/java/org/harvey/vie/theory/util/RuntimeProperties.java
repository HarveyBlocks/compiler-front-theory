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

    /**
     * 函数功能：阻止创建运行时配置工具类实例。
     * 输入：
     * - 无。
     * 输出：无。
     */
    private RuntimeProperties() {
    }

    /**
     * 函数功能：获取是否刷新词法分析表的配置值。
     * 输入：
     * - 无。
     * 输出：是否刷新词法分析表的布尔值。
     */
    public static boolean lexicalFlushTable() {
        return booleanProperty(LEXICAL_FLUSH_TABLE, false);
    }

    /**
     * 函数功能：获取是否刷新语法分析表的配置值。
     * 输入：
     * - 无。
     * 输出：是否刷新语法分析表的布尔值。
     */
    public static boolean syntaxFlushTable() {
        return booleanProperty(SYNTAX_FLUSH_TABLE, false);
    }

    /**
     * 函数功能：获取指定程序测试用例的配置值。
     * 输入：
     * - 无。
     * 输出：程序测试用例名称字符串；未配置时返回 null。
     */
    public static String programTestCase() {
        return stringProperty(PROGRAM_TEST_CASE);
    }

    /**
     * 函数功能：获取运行时配置文件路径。
     * 输入：
     * - 无。
     * 输出：配置文件 Path。
     */
    public static Path configPath() {
        String configured = System.getProperty(CONFIG_PATH_PROPERTY);
        if (configured == null || configured.isBlank()) {
            return Path.of(DEFAULT_CONFIG_PATH);
        }
        return Path.of(configured);
    }

    /**
     * 函数功能：读取布尔类型运行时配置值。
     * 输入：
     * - key：配置项名称。
     * - defaultValue：配置项缺失时使用的默认值。
     * 输出：解析得到的布尔值。
     */
    private static boolean booleanProperty(String key, boolean defaultValue) {
        String value = stringProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * 函数功能：读取字符串类型运行时配置值。
     * 输入：
     * - key：配置项名称。
     * 输出：配置项字符串值；未配置时返回 null。
     */
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

    /**
     * 函数功能：加载配置文件中的运行时配置项。
     * 输入：
     * - 无。
     * 输出：加载得到的 Properties 对象。
     */
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

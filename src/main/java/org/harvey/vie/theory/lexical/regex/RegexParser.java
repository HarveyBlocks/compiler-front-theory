package org.harvey.vie.theory.lexical.regex;

import org.harvey.vie.theory.lexical.regex.node.RegexNode;

import java.text.ParseException;

/**
 * Interface for parsing regular expression strings into a hierarchical
 * structure of {@link RegexNode} instances.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:22
 */
public interface RegexParser {
    /**
     * 函数功能：解析正则表达式字符串并生成正则表达式节点。
     * 输入：
     * - regex：待解析的正则表达式字符串。
     * 输出：解析得到的正则表达式节点。
     */
    RegexNode parse(String regex) throws ParseException;
}

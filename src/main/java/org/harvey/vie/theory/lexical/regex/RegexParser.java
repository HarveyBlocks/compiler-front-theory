package org.harvey.vie.theory.lexical.regex;

import org.harvey.vie.theory.lexical.regex.node.RegexNode;

import java.text.ParseException;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:22
 */
public interface RegexParser {
    RegexNode parse(String regex) throws ParseException;
}

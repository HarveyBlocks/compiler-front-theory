package org.harvey.vie.theory.lexical;

import org.harvey.vie.theory.lexical.analysis.DefaultLexicalAnalyzer;
import org.harvey.vie.theory.lexical.analysis.LexicalAnalyzer;

import java.text.ParseException;
import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 00:32
 */
public interface LexicalDirector {
    LexicalAnalyzer direct(LexicalPattern parten) throws ParseException;
    LexicalAnalyzer direct(List<LexicalPattern> patterns) throws ParseException;
}

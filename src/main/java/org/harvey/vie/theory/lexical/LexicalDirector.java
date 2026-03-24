package org.harvey.vie.theory.lexical;

import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusTable;

import java.text.ParseException;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 00:32
 */
public interface LexicalDirector {
    DfaStatusTable direct(String regex) throws ParseException;
}

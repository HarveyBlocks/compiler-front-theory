package org.harvey.vie.theory.lexical;

import org.harvey.vie.theory.lexical.dfa.status.DfaStatusTable;

import java.text.ParseException;
import java.util.List;

/**
 * Interface for directing the compilation of lexical patterns into an
 * executable DFA transition table. It abstracts the multi-step process
 * of regex-to-DFA conversion.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 00:32
 */
public interface LexicalDirector {
    DfaStatusTable direct(LexicalPattern parten) throws ParseException;

    DfaStatusTable direct(List<LexicalPattern> patterns) throws ParseException;
}

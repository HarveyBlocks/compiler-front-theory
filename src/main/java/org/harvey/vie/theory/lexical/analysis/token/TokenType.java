package org.harvey.vie.theory.lexical.analysis.token;

import org.harvey.vie.theory.io.ILoader;
import org.harvey.vie.theory.io.Storage;
import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactor;

/**
 * Represents the type of lexical token.
 * This interface defines the contract for token types, including methods to retrieve
 * the priority of the token type (used for resolving ambiguities during lexical phase)
 * and a human-readable hint or name for the type.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:30
 */
public interface TokenType extends Storage, TerminalFactor, StatusVertex {

    interface Loader<T extends TokenType> extends ILoader<T> {
    }

}

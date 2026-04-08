package org.harvey.vie.theory.syntax.callback;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.td.SyntaxParsingContext;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 12:24
 */
public interface PredictiveCallback  extends SemanticCallback {

    void onStart(SyntaxParsingContext ctx);

    void onTerminal(SyntaxParsingContext ctx, TerminalSymbol terminal);

    void onEpsilonProduction(SyntaxParsingContext ctx, HeadSymbol head);

    void onProduction(SyntaxParsingContext ctx, GrammarConcatenation concatenation);

    void onAccept(SyntaxParsingContext ctx);

    void onError(SyntaxParsingContext ctx, PredicativeErrorType errorType);

    void beforeAccept(SyntaxParsingContext ctx);
}

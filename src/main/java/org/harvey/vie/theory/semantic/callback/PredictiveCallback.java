package org.harvey.vie.theory.semantic.callback;

import org.harvey.vie.theory.semantic.context.PredictiveSemanticContext;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 12:24
 */
public interface PredictiveCallback  extends SemanticCallback {

    void onStart(PredictiveSemanticContext ctx);

    void onTerminal(PredictiveSemanticContext ctx, TerminalSymbol terminal);

    void onEpsilonProduction(PredictiveSemanticContext ctx, HeadSymbol head);

    void onProduction(PredictiveSemanticContext ctx, GrammarConcatenation concatenation);

    void onAccept(PredictiveSemanticContext ctx);

    void onError(PredictiveSemanticContext ctx, PredicativeErrorType errorType);

    void beforeAccept(PredictiveSemanticContext ctx);
}

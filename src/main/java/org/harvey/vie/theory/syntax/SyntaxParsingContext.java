package org.harvey.vie.theory.syntax;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-12 21:57
 */
public interface SyntaxParsingContext<T> {
    T getStart();

    void pop();

    void push(T next);
    boolean hasNext();
    void consumeCurrentToken();

    SourceToken currentToken();

    boolean isStackEmpty();

    T peek();

    boolean validAcceptStack();
}

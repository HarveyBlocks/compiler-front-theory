package org.harvey.vie.theory.syntax;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.error.SyntaxErrorMessage;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.TokenFilterPredict;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-10 14:22
 */
@AllArgsConstructor
public class PanicSourceTokenIterator {
    private SourceTokenIterator iterator;
    private final ErrorContext errorContext;
    private final TokenFilterPredict tokenFilterPredict;

    public SourceToken currentToken() {
        while (true) {
            SourceToken current;
            try {
                current = iterator.current();
            } catch (CompileException e) {
                // 未完成的token
                errorContext.addError(new SyntaxErrorMessage(iterator.getOffset(), e.getMessage()));
                // 跳过(panic模式)
                continue;
            }
            if (current == SourceTokenIterator.NO_MORE_TOKEN) {
                return current;
            }
            if (tokenFilterPredict.test(current)) {
                return current;
            } else {
                consumeCurrentToken0(); // 消费
            }
        }
    }


    public void consumeCurrentToken() {
        // 消费
        SourceToken next = consumeCurrentToken0();
        if (!tokenFilterPredict.test(next)) {
            throw new CompilerException("Incorrect current-next using: for consumed token what should be filtered");
        }
    }

    private SourceToken consumeCurrentToken0() {

        try {
            return iterator.next();
        } catch (CompileException e) {
            throw new CompilerException(
                    "current mechanism fails. If current must be executed before this next, then this next must not fail!  ",
                    e
            );
        }
    }

    public boolean hasNext() {
        return currentToken() != SourceTokenIterator.NO_MORE_TOKEN;
    }
}

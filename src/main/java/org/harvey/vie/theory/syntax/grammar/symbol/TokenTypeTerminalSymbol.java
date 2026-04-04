package org.harvey.vie.theory.syntax.grammar.symbol;

import lombok.EqualsAndHashCode;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;

/**
 * TODO 最简单的一种, 直接 TokenType 和 TokenType 相等, 但是需要考虑到和 Lexical 阶段达成规范. 同时也会考验 Lexical 阶段 regex 的设计
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:01
 */
@EqualsAndHashCode
public class TokenTypeTerminalSymbol implements TerminalSymbol {
    private final TokenType type;

    public TokenTypeTerminalSymbol(TokenType type) {
        this.type = type;
    }

    @Override
    public TokenType getFactor() {
        return type;
    }

    @Override
    public String toString() {
        return type.hint();
    }

    @Override
    public boolean match(SourceToken token) {
        return type.hint().equals(token.getType().hint());
    }

    @Override
    public String hint() {
        return type.hint();
    }
}

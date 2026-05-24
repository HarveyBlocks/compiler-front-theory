package org.harvey.vie.theory.semantic.value;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:53
 */
public interface ConstantResolver {
    int integerLiteral(SourceToken token);
}

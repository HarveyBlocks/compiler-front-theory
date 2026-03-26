package org.harvey.vie.theory.lexical.analysis.token;

import java.io.InputStream;

/**
 * Interface for a factory that creates {@link TokenType} instances,
 * typically by deserializing them from an {@link InputStream}.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 13:55
 */
public interface TokenTypeFactory {
    TokenType produce(InputStream is);
}

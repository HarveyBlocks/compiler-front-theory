package org.harvey.vie.theory.semantic.command.translator;

import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.semantic.command.translator.token.TokenTranslator;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:30
 */
public interface TokenTranslatorStrategy {
    /**
     * 函数功能：获取指定键或索引对应的对象。
     * 输入：
     * - type：TokenType 类型参数。
     * 输出：TokenTranslator 类型返回值。
     */
    TokenTranslator get(TokenType type);
}

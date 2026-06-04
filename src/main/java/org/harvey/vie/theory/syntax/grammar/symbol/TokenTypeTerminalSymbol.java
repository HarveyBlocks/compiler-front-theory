package org.harvey.vie.theory.syntax.grammar.symbol;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
/**
 * 函数功能：创建 TokenTypeTerminalSymbol 对象。
 * 输入：
 * - type：TokenType 类型参数。
 * 输出：无。
 */

    public TokenTypeTerminalSymbol(TokenType type) {
        this.type = type;
    }
/**
 * 函数功能：获取终结符因子。
 * 输入：
 * - 无。
 * 输出：TokenType 类型返回值。
 */

    @Override
    public TokenType getFactor() {
        return type;
    }
/**
 * 函数功能：返回当前对象的字符串表示。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    @Override
    public String toString() {
        return type.hint();
    }
/**
 * 函数功能：判断是否匹配指定词法单元。
 * 输入：
 * - token：SourceToken 类型参数。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean match(SourceToken token) {
        // TODO: NOT GOOD TO COMPARE EQUALS
        return type.hint().equals(token.getType().hint());
    }
/**
 * 函数功能：获取提示文本。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    @Override
    public String hint() {
        return type.hint();
    }
/**
 * 函数功能：将对象写入输出流。
 * 输入：
 * - os：OutputStream 类型参数。
 * 输出：整数结果。
 */


    @Override
    public int store(OutputStream os) throws IOException {
        return type.store(os);
    }

    @AllArgsConstructor
    public static class Loader implements TerminalSymbol.Loader<TokenTypeTerminalSymbol> {
        private  final TokenType.Loader<?> tokenTypeLoader;
        /**
         * 函数功能：从输入流加载对象。
         * 输入：
         * - is：InputStream 类型参数。
         * 输出：TokenTypeTerminalSymbol 类型返回值。
         */
        @Override
        public TokenTypeTerminalSymbol load(InputStream is) throws IOException {
            return new TokenTypeTerminalSymbol(tokenTypeLoader.load(is));
        }
    }
}

package org.harvey.vie.theory.semantic.command.translator;

import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.semantic.command.CommandBuildCallback;
import org.harvey.vie.theory.semantic.command.translator.token.BreakTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.ContinueTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.SimpleStringTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.TokenTranslator;

/**
 * 移进翻译器选择策略。
 * <p>
 * {@link CommandBuildCallback} 在每次 shift 时按 token 类型调用 {@link #get(TokenType)}。
 * 常量 token 通常由 {@link SimpleStringTokenTranslator} 直接生成装载常量的命令；
 * {@code break}/{@code continue} token 分别进入 {@link BreakTokenTranslator} 和
 * {@link ContinueTokenTranslator}，先生成目标未知的跳转命令，等循环翻译器再绑定真实标签。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:30
 */
public interface TokenTranslatorStrategy {
    TokenTranslator get(TokenType type);
}

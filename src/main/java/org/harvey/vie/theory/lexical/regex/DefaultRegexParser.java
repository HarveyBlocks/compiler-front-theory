package org.harvey.vie.theory.lexical.regex;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacterFactory;
import org.harvey.vie.theory.lexical.regex.node.*;

import java.text.ParseException;

/**
 * Default implementation of the {@link RegexParser} interface.
 * It uses a recursive descent parsing strategy to convert a regular expression
 * string into a hierarchical structure of {@link RegexNode}s.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:23
 */
@AllArgsConstructor
public class DefaultRegexParser implements RegexParser {
    private final AlphabetCharacterFactory factory;

    /**
     * 函数功能：解析正则上下文并生成正则表达式节点。
     * 输入：
     * - ctx：待解析的正则上下文。
     * 输出：解析得到的正则表达式节点。
     */
    public static RegexNode parse(RegexContext ctx) throws ParseException {
        if (ctx.current() == RegexContext.DONE) {
            return RegexContext.OCCUPANCY;
        }
        RegexNode result = expression(ctx);
        // 确保所有字符都被解析完毕
        if (ctx.current() != RegexContext.DONE) {
            throw new ParseException("Unexpected character at position " + ctx.getIndex(), ctx.getIndex());
        }
        return result;
    }

    /**
     * 函数功能：解析正则表达式中的选择表达式。
     * 输入：
     * - ctx：待解析的正则上下文。
     * 输出：解析得到的正则表达式节点。
     */
    private static RegexNode expression(RegexContext ctx) throws ParseException {
        RegexNode left = term(ctx);
        while (ctx.skipIf('|')) {
            RegexNode right = term(ctx);
            left = new CupRegexNode(left, right);
        }
        return left;
    }

    /**
     * 函数功能：解析正则表达式中的连接项。
     * 输入：
     * - ctx：待解析的正则上下文。
     * 输出：解析得到的正则表达式节点。
     */
    private static RegexNode term(RegexContext ctx) throws ParseException {
        RegexNode left = factor(ctx);
        for (int cur = ctx.current(); cur != RegexContext.DONE && cur != '|' && cur != ')'; cur = ctx.current()) {
            RegexNode right = factor(ctx);
            left = new ConcatenationRegexNode(left, right);
        }
        return left;
    }

    /**
     * 函数功能：解析正则表达式中的基本因子。
     * 输入：
     * - ctx：待解析的正则上下文。
     * 输出：解析得到的正则表达式节点。
     */
    private static RegexNode factor(RegexContext ctx) throws ParseException {
        ctx.currentNotDone();
        int ch = ctx.current();
        RegexNode node;
        if (ctx.skipIf('\\')) {
            ctx.currentNotDone();
            ch = ctx.current();
            ctx.next(); // 消费字符
            node = new CharRegexNode(ctx.createEscape(ch)); // 后一个无论如何都是转义字符
        } else if (ctx.skipIf('(')) {
            // 检查空括号 "()"
            if (ctx.skipIf(')')) {
                node = RegexContext.OCCUPANCY;
            } else {
                node = expression(ctx);
                if (!ctx.skipIf(')')) {
                    throw new ParseException("Missing ')'", ctx.getIndex());
                }
            }
        } else if (ch == ')' || ch == '|' || ch == '*') {
            // 这些字符出现在不该出现的位置
            throw new ParseException("Unexpected '" + ch + "'", ctx.getIndex());
        } else {
            // 普通字符
            node = new CharRegexNode(ctx.createRaw(ch));
            ctx.next(); // 消费字符
        }
        return closure(ctx, node);
    }

    /**
     * 函数功能：解析正则表达式节点上的闭包标记。
     * 输入：
     * - ctx：待解析的正则上下文。
     * - node：待处理的正则表达式节点。
     * 输出：处理后的正则表达式节点。
     */
    private static RegexNode closure(RegexContext ctx, RegexNode node) {
        return ctx.skipIf('*') ? new ClosureRegexNode(node) : node;
    }

    /**
     * 函数功能：解析正则表达式字符串并生成正则表达式节点。
     * 输入：
     * - regex：待解析的正则表达式字符串。
     * 输出：解析得到的正则表达式节点；输入为 null 时返回 null。
     */
    @Override
    public RegexNode parse(String regex) throws ParseException {
        return regex == null ? null : parse(new RegexContext(factory, regex));
    }

}

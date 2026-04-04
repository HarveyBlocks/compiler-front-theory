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
    @Override
    public RegexNode parse(String regex) throws ParseException {
        return regex == null ? null : parse(new RegexContext(factory, regex));
    }

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
     * <pre>{@code
     *  expression -> term
     *             | expression '|' term
     * }</pre>
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
     * <pre>{@code
     * term -> factor
     *      | term factor
     * }</pre>
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
     * <pre>{@code
     * factor -> char
     *         | \char
     *         | '(' expression ')'
     *         | '()'
     * }</pre>
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
     * <pre>{@code
     * closure -> factor '*'
     * }</pre>
     */
    private static RegexNode closure(RegexContext ctx, RegexNode node) {
        return ctx.skipIf('*') ? new ClosureRegexNode(node) : node;
    }

}
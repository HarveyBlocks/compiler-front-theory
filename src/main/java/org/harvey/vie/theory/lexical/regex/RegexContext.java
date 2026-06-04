package org.harvey.vie.theory.lexical.regex;

import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacterFactory;
import org.harvey.vie.theory.lexical.regex.node.EpsilonRegexNode;
import org.harvey.vie.theory.lexical.regex.node.RegexNode;

import java.text.ParseException;
import java.util.Objects;

/**
 * 定义空括号为\epsilon
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 12:33
 */
public class RegexContext {
    public static final RegexNode OCCUPANCY = new EpsilonRegexNode();
    public static final int DONE = -1;
    private final String text;
    private final AlphabetCharacterFactory alphabetFactory;
    private final int end;
    private int pos;

    /**
     * 函数功能：创建指定文本和字符工厂的正则解析上下文。
     * 输入：
     * - factory：用于创建字母字符的工厂。
     * - text：待解析的正则表达式文本。
     * 输出：无。
     */
    public RegexContext(AlphabetCharacterFactory factory, String text) {
        Objects.requireNonNull(text);
        this.text = text;
        this.pos = 0;
        this.end = text.codePointCount(0, text.length());
        this.alphabetFactory = factory;
    }

    /**
     * 函数功能：在当前字符匹配指定字符时跳过该字符。
     * 输入：
     * - c：待匹配并跳过的字符编码。
     * 输出：若成功跳过则返回 true，否则返回 false。
     */
    public boolean skipIf(int c) {
        if (current() == c) {
            next(); // 消费字符
            return true;
        }
        return false;
    }

    /**
     * 函数功能：获取当前解析位置。
     * 输入：
     * - 无。
     * 输出：当前解析位置索引。
     */
    public int getIndex() {
        return pos;
    }

    /**
     * 函数功能：获取当前解析位置的字符编码。
     * 输入：
     * - 无。
     * 输出：当前字符编码；已结束时返回结束标记。
     */
    public int current() {
        if (pos >= 0 && pos < end) {
            return text.codePointAt(pos);
        } else {
            return DONE;
        }
    }

    /**
     * 函数功能：将当前解析位置移动到下一个字符。
     * 输入：
     * - 无。
     * 输出：无。
     */
    public void next() {
        if (pos < end - 1) {
            pos++;
        } else {
            pos = end;
        }
    }

    /**
     * 函数功能：确认当前解析位置尚未到达输入结束。
     * 输入：
     * - 无。
     * 输出：无。
     */
    public void currentNotDone() throws ParseException {
        if (current() == DONE) {
            throw new ParseException("Unexpected end of input", pos);
        }
    }


    /**
     * 函数功能：根据转义字符编码创建字母字符。
     * 输入：
     * - ch：转义字符编码。
     * 输出：创建得到的字母字符。
     */
    public AlphabetCharacter createEscape(int ch) {
        return alphabetFactory.createEscape(ch);
    }

    /**
     * 函数功能：根据原始字符编码创建字母字符。
     * 输入：
     * - ch：原始字符编码。
     * 输出：创建得到的字母字符。
     */
    public AlphabetCharacter createRaw(int ch) {
        return alphabetFactory.createRaw(ch);
    }
}

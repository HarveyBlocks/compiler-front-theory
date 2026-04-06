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

    public RegexContext(AlphabetCharacterFactory factory, String text) {
        Objects.requireNonNull(text);
        this.text = text;
        this.pos = 0;
        this.end = text.codePointCount(0, text.length());
        this.alphabetFactory = factory;
    }

    public boolean skipIf(int c) {
        if (current() == c) {
            next(); // 消费字符
            return true;
        }
        return false;
    }

    public int getIndex() {
        return pos;
    }

    public int current() {
        if (pos >= 0 && pos < end) {
            return text.codePointAt(pos);
        } else {
            return DONE;
        }
    }

    public void next() {
        if (pos < end - 1) {
            pos++;
        } else {
            pos = end;
        }
    }

    public void currentNotDone() throws ParseException {
        if (current() == DONE) {
            throw new ParseException("Unexpected end of input", pos);
        }
    }


    public AlphabetCharacter createEscape(int ch) {
        return alphabetFactory.createEscape(ch);
    }

    public AlphabetCharacter createRaw(int ch) {
        return alphabetFactory.createRaw(ch);
    }
}

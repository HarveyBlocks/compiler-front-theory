package org.harvey.vie.theory.lexical.regex;

import org.harvey.vie.theory.lexical.regex.node.EpsilonRegexNode;
import org.harvey.vie.theory.lexical.regex.node.RegexNode;
import org.harvey.vie.theory.source.SourceCharacter;
import org.harvey.vie.theory.source.SourceCharacterFactory;

import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.StringCharacterIterator;
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
    private final CharacterIterator it;
    private final SourceCharacterFactory sourceCharacterFactory;

    public RegexContext(String text) {
        Objects.requireNonNull(text);
        this.it = new StringCharacterIterator(text);
        this.sourceCharacterFactory = new RegexSourceCharacterFactory();
    }

    public boolean skipIf(char c) {
        if (it.current() == c) {
            it.next();
            return true;
        }
        return false;
    }

    public int getIndex() {
        return it.getIndex();
    }

    public char current() {
        return it.current();
    }

    public char next() {
        return it.next();
    }

    public void currentNotDone() throws ParseException {
        if (current() == CharacterIterator.DONE) {
            throw new ParseException("Unexpected end of input", it.getIndex());
        }
    }


    public SourceCharacter createEscape(char ch) {
        return sourceCharacterFactory.createRaw(ch);
    }

    public SourceCharacter createRaw(char ch) {
        return sourceCharacterFactory.createRaw(ch);
    }
}

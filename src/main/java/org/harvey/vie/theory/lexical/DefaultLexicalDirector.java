package org.harvey.vie.theory.lexical;

import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacterFactory;
import org.harvey.vie.theory.lexical.dfa.DefaultDfaMinimizer;
import org.harvey.vie.theory.lexical.dfa.DefaultNfaDfaAdaptor;
import org.harvey.vie.theory.lexical.dfa.DfaMinimizer;
import org.harvey.vie.theory.lexical.dfa.NfaDfaAdaptor;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusTable;
import org.harvey.vie.theory.lexical.nfa.DefaultRegexNfaAdaptor;
import org.harvey.vie.theory.lexical.nfa.RegexNfaAdaptor;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusGraph;
import org.harvey.vie.theory.lexical.regex.DefaultRegexParser;
import org.harvey.vie.theory.lexical.regex.RegexParser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the {@link LexicalDirector} interface.
 * This class coordinates the process of parsing regular expressions, converting
 * them to an NFA, transforming the NFA to a DFA, and finally minimizing the DFA
 * to produce an optimized state transition first.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 00:32
 */
public class DefaultLexicalDirector implements LexicalDirector {

    private final RegexParser regexParser;
    private final RegexNfaAdaptor regexNfaAdaptor;
    private final NfaDfaAdaptor nfaDfaAdaptor;
    private final DfaMinimizer dfaMinimizer;

    public DefaultLexicalDirector(AlphabetCharacterFactory factory) {
        this.regexParser = new DefaultRegexParser(factory);
        this.regexNfaAdaptor = new DefaultRegexNfaAdaptor();
        this.nfaDfaAdaptor = new DefaultNfaDfaAdaptor();
        this.dfaMinimizer = new DefaultDfaMinimizer();
    }

    @Override
    public DfaStatusTable direct(LexicalPattern pattern) throws ParseException {
        return direct(List.of(pattern));
    }

    @Override
    public DfaStatusTable direct(List<LexicalPattern> patterns) throws ParseException {
        List<RegexTypePair> pairs = new ArrayList<>();
        for (LexicalPattern pattern : patterns) {
            // 正则解析成树
            pairs.add(new RegexTypePair(regexParser.parse(pattern.getRegex()), pattern.getType()));
        }
        // 正则->nfa
        NfaStatusGraph nfaStatusGraph = regexNfaAdaptor.adapt(pairs);
        // nfa->dfa
        DfaStatusGraph dfaStatusGraph = nfaDfaAdaptor.adapt(nfaStatusGraph);
        // 最小化
        return dfaMinimizer.minimize(dfaStatusGraph);
    }

}

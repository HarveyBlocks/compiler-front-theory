package org.harvey.vie.theory.lexical;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacterFactory;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.dfa.DefaultDfaMinimizer;
import org.harvey.vie.theory.lexical.dfa.DefaultNfaDfaAdaptor;
import org.harvey.vie.theory.lexical.dfa.DfaMinimizer;
import org.harvey.vie.theory.lexical.dfa.NfaDfaAdaptor;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.dfa.status.RegexDfaStatusTable;
import org.harvey.vie.theory.lexical.dfa.status.RegexDfaStatusTableFactory;
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
@Slf4j
public class DefaultLexicalDirector implements LexicalDirector {

    private final RegexParser regexParser;
    private final RegexNfaAdaptor regexNfaAdaptor;
    private final NfaDfaAdaptor nfaDfaAdaptor;
    private final DfaMinimizer dfaMinimizer;
    private final RegexDfaStatusTableFactory regexDfaStatusTableFactory;

    public DefaultLexicalDirector(AlphabetCharacterFactory factory) {
        this.regexParser = new DefaultRegexParser(factory);
        this.regexNfaAdaptor = new DefaultRegexNfaAdaptor();
        this.nfaDfaAdaptor = new DefaultNfaDfaAdaptor();
        this.dfaMinimizer = new DefaultDfaMinimizer();
        this.regexDfaStatusTableFactory = new RegexDfaStatusTableFactory();
    }

    @Override
    public RegexDfaStatusTable direct(LexicalPattern pattern) throws ParseException {
        return direct(List.of(pattern));
    }

    @Override
    public RegexDfaStatusTable direct(List<LexicalPattern> patterns) throws ParseException {
        List<RegexTypePair> pairs = new ArrayList<>();
        for (LexicalPattern pattern : patterns) {
            // 正则解析成树
            pairs.add(new RegexTypePair(regexParser.parse(pattern.getRegex()), pattern.getType()));
            log.info("phased regex tree: " + pattern.getType());
        }
        // 正则->nfa
        NfaStatusGraph<AlphabetCharacter, TokenType> nfaStatusGraph = regexNfaAdaptor.adapt(pairs);
        log.info("phased nfa.");
        // nfa->dfa
        DfaStatusGraph<AlphabetCharacter, TokenType> dfaStatusGraph = nfaDfaAdaptor.adapt(nfaStatusGraph);
        log.info("phased dfa.");
        // 最小化
        return dfaMinimizer.minimize(regexDfaStatusTableFactory, dfaStatusGraph);
    }

}

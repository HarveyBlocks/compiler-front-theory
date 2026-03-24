package org.harvey.vie.theory.lexical;

import org.harvey.vie.theory.lexical.dfa.*;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusTable;
import org.harvey.vie.theory.lexical.nfa.DefaultRegexNfaAdaptor;
import org.harvey.vie.theory.lexical.nfa.RegexNfaAdaptor;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusTable;
import org.harvey.vie.theory.lexical.regex.DefaultRegexParser;
import org.harvey.vie.theory.lexical.regex.RegexParser;
import org.harvey.vie.theory.lexical.regex.node.RegexNode;

import java.text.ParseException;

/**
 * TODO
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

    public DefaultLexicalDirector() {
        this.regexParser = new DefaultRegexParser();
        this.regexNfaAdaptor = new DefaultRegexNfaAdaptor();
        this.nfaDfaAdaptor = new DefaultNfaDfaAdaptor();
        this.dfaMinimizer = new DefaultDfaMinimizer();
    }

    @Override
    public DfaStatusTable direct(String regex) throws ParseException {
        // 正则解析成树
        RegexNode regexNode = regexParser.parse(regex);
        // 正则->nfa
        NfaStatusTable nfaStatusTable = regexNfaAdaptor.adapt(regexNode);
        // nfa->dfa
        DfaStatusGraph dfaStatusGraph = nfaDfaAdaptor.adapt(nfaStatusTable);
        // 最小化
        return dfaMinimizer.minimize(dfaStatusGraph);
    }
}

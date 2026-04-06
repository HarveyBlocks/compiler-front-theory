package org.harvey.vie.theory.demo;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.error.DefaultErrorContext;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.io.resource.AsciiStringResource;
import org.harvey.vie.theory.io.resource.Resource;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacterFactory;
import org.harvey.vie.theory.lexical.alphabet.RegexAlphabetCharacterFactory;
import org.harvey.vie.theory.lexical.alphabet.SourceAlphabetCharacterAdaptorImpl;
import org.harvey.vie.theory.lexical.analysis.DefaultLexicalAnalyzer;
import org.harvey.vie.theory.lexical.analysis.LexicalAnalyzer;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.dfa.status.RegexDfaStatusTable;
import org.harvey.vie.theory.syntax.bu.item.ItemSet;
import org.harvey.vie.theory.syntax.bu.item.ItemSetFamily;
import org.harvey.vie.theory.syntax.bu.item.ItemSetFamilyFactory;
import org.harvey.vie.theory.syntax.bu.item.ItemSetFamilyFactoryImpl;
import org.harvey.vie.theory.syntax.bu.la.LookaheadMap;
import org.harvey.vie.theory.syntax.bu.la.LookaheadMapFactory;
import org.harvey.vie.theory.syntax.bu.la.LookaheadMapFactoryImpl;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTableFactory;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTableFactoryImpl;
import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.first.FirstMapFactory;
import org.harvey.vie.theory.syntax.grammar.first.IterativeFixedPointFirstMapFactory;
import org.harvey.vie.theory.syntax.grammar.first.NaiveRecursiveFirstMapFactory;
import org.harvey.vie.theory.syntax.grammar.follow.FollowMap;
import org.harvey.vie.theory.syntax.grammar.follow.FollowSetFactory;
import org.harvey.vie.theory.syntax.grammar.follow.FollowSetFactoryImpl;
import org.harvey.vie.theory.syntax.grammar.normalize.LeftFactoringEliminator;
import org.harvey.vie.theory.syntax.grammar.normalize.LeftFactoringEliminatorImpl;
import org.harvey.vie.theory.syntax.grammar.normalize.LeftRecursionEliminator;
import org.harvey.vie.theory.syntax.grammar.normalize.LeftRecursionEliminatorImpl;
import org.harvey.vie.theory.syntax.grammar.produce.GrammarProductionBuilder;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContextBuilder;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContextBuilderImpl;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.syntax.td.PredictivePhaser;
import org.harvey.vie.theory.syntax.td.PredictivePhaserImpl;
import org.harvey.vie.theory.syntax.td.conflict.LexicalConflictResolver;
import org.harvey.vie.theory.syntax.td.table.DeterministicPredictiveParsingTableFactory;
import org.harvey.vie.theory.syntax.td.table.PredictiveParsingTable;
import org.harvey.vie.theory.syntax.td.table.PredictiveParsingTableFactory;
import org.harvey.vie.theory.syntax.td.tree.node.SyntaxTreeNode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

/**
 * 词法分析器Demo
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:19
 */
@Slf4j
public class SyntaxDemo {

    private static final TerminalFactory TERMINAL_FACTORY = terminal -> new TokenTypeTerminalSymbol((TokenType) terminal);
    private static final TerminalMatcherFactory MATCHER_FACTORY = array -> (TerminalMatcher) token -> {
        for (int i = 0; i < array.length; i++) {
            if (array[i].match(token)) {
                return i;
            }
        }
        throw new CompilerException("Unexpected source token: " +
                                    token.hintString() +
                                    ". For can not found in grammar production set.");
    };

    public static void main(String[] args) {
//         testPredictive();
        ShiftReduceParsingTable shiftReduceParsingTable = buildShiftReduceParsingTable();
    }

    private static void testShiftReduce() {

    }

    private static ShiftReduceParsingTable buildShiftReduceParsingTable() {
        ProductionSetContext context = ProductionSetContextBuilds.build5(TERMINAL_FACTORY);
        System.out.println(context);
        System.out.println("-----------------------");
        FirstMapFactory firstMapFactory = new IterativeFixedPointFirstMapFactory();
        FirstMap firstMap = firstMapFactory.first(context);
        firstMap.forEach(System.out::println);
        System.out.println("-----------------------");
        ItemSetFamilyFactory itemSetFamilyFactory = new ItemSetFamilyFactoryImpl();
        ItemSetFamily family = itemSetFamilyFactory.produce("S", context, firstMap);
        showItemSetFamily(family);
        System.out.println("-----------------------");
        LookaheadMapFactory lookaheadMapFactory = new LookaheadMapFactoryImpl();
        LookaheadMap[] lookaheadMaps = lookaheadMapFactory.produce("S", context, family, firstMap);
        int cur = 0;
        for (LookaheadMap lookaheadMap : lookaheadMaps) {
            System.out.println("I" + (cur++) + ": " + lookaheadMap);
        }
        System.out.println("-----------------------");
        ShiftReduceParsingTableFactory shiftReduceParsingTableFactory = new ShiftReduceParsingTableFactoryImpl();
        ShiftReduceParsingTable shiftReduceParsingTable = shiftReduceParsingTableFactory.produce(
                "S",
                context,
                firstMap,
                family,
                lookaheadMaps
        );
        System.out.println(shiftReduceParsingTable);
        System.out.println("-----------------------");
        return shiftReduceParsingTable;
    }

    private static void showItemSetFamily(ItemSetFamily family) {
        int cur = 0;
        for (ItemSet set : family) {
            System.out.println("I" + (cur++) + ": " + set);
        }
        System.out.println("------------goto-terminal-----------");
        cur = 0;
        for (ItemSet set : family) {
            Map<TerminalSymbol, Integer> map = set.getTerminalGoto();
            int id = cur++;
            for (TerminalSymbol symbol : map.keySet()) {
                System.out.print("GOTO(I" + id + "," + symbol + ")=I" + map.get(symbol) + "\t");
            }
            if (!map.isEmpty()) {
                System.out.println();
            }
        }
        System.out.println("------------goto-head-----------");
        cur = 0;
        for (ItemSet set : family) {
            Map<HeadSymbol, Integer> map = set.getHeadGoto();
            int id = cur++;
            for (HeadSymbol symbol : map.keySet()) {
                System.out.print("GOTO(I" + id + "," + symbol + ")=I" + map.get(symbol) + "\t");
            }
            if (!map.isEmpty()) {
                System.out.println();
            }
        }
        System.out.println("------------DR-----------");
        cur = 0;
        for (ItemSet set : family) {
            Map<HeadSymbol, Set<TerminalSymbol>> decisionRule = set.getDecisionRule();
            int id = cur++;
            for (HeadSymbol symbol : decisionRule.keySet()) {
                System.out.print("DR(I" + id + "," + symbol + ")=" + decisionRule.get(symbol) + "\t");
            }
            if (!decisionRule.isEmpty()) {
                System.out.println();
            }
        }
    }

    private static void testPredictive() {
        // lexical analyzer
        AlphabetCharacterFactory alphabetCharacterFactory = new RegexAlphabetCharacterFactory();
        RegexDfaStatusTable table = LexicalDemo.buildTable(alphabetCharacterFactory);
        SourceAlphabetCharacterAdaptorImpl saca = new SourceAlphabetCharacterAdaptorImpl(alphabetCharacterFactory);
        LexicalAnalyzer analyzer = new DefaultLexicalAnalyzer(table, saca);

        // syntax analyzer
        PredictiveParsingTable predictiveParsingTable = buildPredictiveParsingTable();
        GrammarUnitSymbol start = predictiveParsingTable.headStart("E");
        PredictivePhaser predictivePhaser = new PredictivePhaserImpl(
                predictiveParsingTable,
                LexicalConflictResolver.passive()
        );

        // resource
        Resource resource = new AsciiStringResource("(id+id)*id");
        // error context
        ErrorContext errorContext = new DefaultErrorContext();

        try (SourceTokenIterator iterator = analyzer.iterator(errorContext, resource)) {
            SyntaxTreeNode node = predictivePhaser.phase(start, iterator, errorContext);
            System.out.println("ok");
        } catch (CompileException e) {
            log.warn("compile error", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!errorContext.isEmpty()) {
                log.info("{}", errorContext);
            }
        }
    }

    public static PredictiveParsingTable buildPredictiveParsingTable() {
        ProductionSetContext context = ProductionSetContextBuilds.build4(TERMINAL_FACTORY);
        System.out.println(context);
        System.out.println("-----------------------");
        LeftRecursionEliminator leftRecursionEliminator = new LeftRecursionEliminatorImpl(s -> s + '\'');
        LeftFactoringEliminator leftFactoringEliminator = new LeftFactoringEliminatorImpl((s, i) -> s + i);
        ProductionSetContext eliminated = leftFactoringEliminator.eliminate(leftRecursionEliminator.eliminate(context));
        System.out.println(eliminated);
        System.out.println("-----------------------");
        FirstMapFactory firstMapFactory = new NaiveRecursiveFirstMapFactory();
        FirstMap firstMap = firstMapFactory.first(eliminated);
        firstMap.forEach(System.out::println);
        System.out.println("-----------------------");
        FollowSetFactory followSetFactory = new FollowSetFactoryImpl();
        FollowMap followMap = followSetFactory.follow("E", eliminated, firstMap);
        followMap.entrySet().forEach(System.out::println);
        System.out.println("-----------------------");
        PredictiveParsingTableFactory tableFactory = new DeterministicPredictiveParsingTableFactory(MATCHER_FACTORY);
        PredictiveParsingTable predictiveParsingTable = tableFactory.produce(eliminated, firstMap, followMap);
        System.out.println(predictiveParsingTable);
        System.out.println("-----------------------");
        return predictiveParsingTable;
    }
}

class ProductionSetContextBuilds {

    public static ProductionSetContext build1(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        GrammarProductionBuilder itemBuilder = contextBuilder.define("item");
        GrammarProductionBuilder digitBuilder = contextBuilder.define("digit");
        itemBuilder.alternateSelf()
                .concatenateTerminalLast(of("+"))
                .concatenateDefinitionLast("digit")
                .alternateTerminal(of("digit"));
        digitBuilder.alternateTerminal(of("0"))
                .alternateTerminal(of("1"))
                .alternateTerminal(of("2"))
                .alternateTerminal(of("3"))
                .alternateTerminal(of("4"));
        GrammarProductionBuilder digitBuilder2 = contextBuilder.define("digit");
        digitBuilder2.alternateTerminal(of("5"))
                .alternateTerminal(of("6"))
                .alternateTerminal(of("7"))
                .alternateTerminal(of("8"))
                .alternateTerminal(of("9"));
        return contextBuilder.build();
    }

    // 消解左递归, 有间接递归
    public static ProductionSetContext build2(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        GrammarProductionBuilder sBuilder = contextBuilder.define("S");
        GrammarProductionBuilder aBuilder = contextBuilder.define("A");
        sBuilder.alternateDefinition("A").concatenateTerminalLast(of("a")).alternateTerminal(of("b"));
        aBuilder.alternateSelf()
                .concatenateTerminalLast(of("c"))
                .alternateDefinition("S")
                .concatenateTerminalLast(of("d"))
                .alternateEpsilon();
        return contextBuilder.build();
    }

    // 消解左相同因子
    public static ProductionSetContext build3(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        contextBuilder.define("B").alternateEpsilon();
        contextBuilder.define("M")
                .alternateTerminal(of("N"))
                .alternateTerminal(of("N"))
                .concatenateTerminalLast(of("O"));
        contextBuilder.define("X")
                .alternateEpsilon()
                .alternateTerminal(of("A"))
                .concatenateDefinitionLast("B")
                .concatenateTerminalLast(of("C"))
                .concatenateTerminalLast(of("E"))
                .concatenateTerminalLast(of("F"))
                .concatenateTerminalLast(of("G"))
                .alternateTerminal(of("A"))
                .concatenateDefinitionLast("B")
                .concatenateTerminalLast(of("C"))
                .concatenateTerminalLast(of("D"))
                .concatenateTerminalLast(of("E"))
                .concatenateTerminalLast(of("F"))
                .alternateTerminal(of("A"))
                .concatenateDefinitionLast("B")
                .concatenateTerminalLast(of("D"))
                .alternateTerminal(of("A"))
                .concatenateTerminalLast(of("D"))
                .alternateTerminal(of("D"))
                .alternateTerminal(of("A"))
                .concatenateDefinitionLast("B")
                .concatenateTerminalLast(of("D"))
                .concatenateTerminalLast(of("E"))
                .alternateTerminal(of("A"))
                .concatenateTerminalLast(of("D"))
                .concatenateTerminalLast(of("E"))
                .alternateDefinition("B")
                .concatenateTerminalLast(of("A"))
                .concatenateTerminalLast(of("C"))
                .concatenateTerminalLast(of("D"))
                .concatenateTerminalLast(of("E"))
                .alternateDefinition("B")
                .concatenateTerminalLast(of("A"))
                .concatenateTerminalLast(of("C"))
                .alternateEpsilon();
        return contextBuilder.build();
    }

    public static ProductionSetContext build4(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        return classic(contextBuilder);
    }

    private static TokenType of(String hint) {
        return new StringTokenType(hint);
    }

    public static ProductionSetContext build5(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        contextBuilder.define("S").alternateDefinition("E");
        return classic(contextBuilder);
    }

    private static ProductionSetContext classic(ProductionSetContextBuilder contextBuilder) {
        contextBuilder.define("E")
                .alternateDefinition("E")
                .concatenateTerminalLast(of("+"))
                .concatenateDefinitionLast("T")
                .alternateDefinition("T");
        contextBuilder.define("T")
                .alternateDefinition("T")
                .concatenateTerminalLast(of("*"))
                .concatenateDefinitionLast("F")
                .alternateDefinition("F");
        contextBuilder.define("F")
                .alternateTerminal(of("("))
                .concatenateDefinitionLast("E")
                .concatenateTerminalLast(of(")"))
                .alternateTerminal(of("id"));
        return contextBuilder.build();
    }

    @AllArgsConstructor
    static class StringTokenType implements TokenType {
        private final String hint;

        @Override
        public int store(OutputStream os) throws IOException {
            throw new UnsupportedEncodingException("just test");
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public @NonNull String hint() {
            return hint;
        }
    }
}
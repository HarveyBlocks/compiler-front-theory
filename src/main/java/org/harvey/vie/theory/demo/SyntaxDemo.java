package org.harvey.vie.theory.demo;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.demo.grammar.ProductionSetContextBuilds;
import org.harvey.vie.theory.demo.program.ProgramSyntaxDemo;
import org.harvey.vie.theory.demo.program.ProgramTokenType;
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
import org.harvey.vie.theory.semantic.command.command.factory.CommandFactory;
import org.harvey.vie.theory.semantic.command.command.factory.DefaultCommandFactory;
import org.harvey.vie.theory.semantic.command.command.string.SimpleStringCommandFactory;
import org.harvey.vie.theory.semantic.command.command.string.TypedStringCommandFactory;
import org.harvey.vie.theory.semantic.context.SemanticResult;
import org.harvey.vie.theory.semantic.function.FunctionManager;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaser;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaserImpl;
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
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTableImpl;
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
import org.harvey.vie.theory.syntax.grammar.produce.DefineSimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTagComparator;
import org.harvey.vie.theory.syntax.td.PredictivePhaserImpl;
import org.harvey.vie.theory.syntax.td.table.DeterministicPredictiveParsingTableFactory;
import org.harvey.vie.theory.syntax.td.table.PredictiveParsingTable;
import org.harvey.vie.theory.syntax.td.table.PredictiveParsingTableFactory;
import org.harvey.vie.theory.util.RuntimeProperties;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * 词法分析器Demo, 打印输出过程
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:19
 */
@Slf4j
public class SyntaxDemo {

    private static class Predicative {
        /**
         * 函数功能：运行预测分析演示入口。
         * 输入：
         * - args：命令行参数数组。
         * 输出：无。
         */
        public static void main(String[] args) {
            SemanticResult result = SyntaxDemo.demo("(id+id)*id", (iter, errCtx) -> {
                // syntax analyzer
                PredictiveParsingTable predictiveParsingTable = buildPredictiveParsingTable("E");
                GrammarUnitSymbol start = predictiveParsingTable.headStart("E");
                PredictivePhaserImpl phaser = new PredictivePhaserImpl(
                        start,
                        predictiveParsingTable,
                        SemanticDemo.buildPredicativeRegister(),
                        t -> true
                );
                return phaser.phase(iter, errCtx);
            });
            System.out.println(result);
        }
    }

    public static final CommandFactory STRING_COMMAND_FACTORY = new DefaultCommandFactory(
            new TypedStringCommandFactory(ProgramSyntaxDemo.TYPE_RESOLVER),
            new SimpleStringCommandFactory()
    );

    private static class ShiftReduce {
        /**
         * 函数功能：运行移进归约分析演示入口。
         * 输入：
         * - args：命令行参数数组。
         * 输出：无。
         */
        public static void main(String[] args) {
            SemanticResult result = SyntaxDemo.demo("(id+id)*id", (iter, errCtx) -> {
                ProductionSetContext context = ProductionSetContextBuilds.build5(TERMINAL_FACTORY);
                System.out.println(context);
                ShiftReduceParsingTable shiftReduceParsingTable = buildShiftReduceParsingTable(
                        "S",
                        context,
                        "syntax_simple.data",
                        is -> null, /*不是Program的, 不支持Tag*/
                        (t1, t2) -> 0 /*不是Program的, 不支持Tag*/
                );
                ShiftReducePhaser phaser = new ShiftReducePhaserImpl(
                        shiftReduceParsingTable,
                        t -> true,
                        SemanticDemo.buildSimpleShiftReduceRegister(),
                        STRING_COMMAND_FACTORY,
                        null, // 未定
                        ProgramSyntaxDemo.CONSTANT_RESOLVER,
                        null
                );
                return phaser.phase(iter, errCtx);
            });
            System.out.println(result);
        }
    }

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

    /**
     * 函数功能：对输入文本执行词法分析并交给指定语法分析流程处理。
     * 输入：
     * - text：待分析的源文本。
     * - syntaxPhaserMapper：将词法单元迭代器和错误上下文映射为语义结果的函数。
     * 输出：语法分析得到的 SemanticResult；分析失败时返回 null。
     */
    public static SemanticResult demo(
            String text, BiFunction<SourceTokenIterator, ErrorContext, SemanticResult> syntaxPhaserMapper) {
        LexicalAnalyzer analyzer = lexicalAnalyzer();
        // resource
        Resource resource = new AsciiStringResource(text);
        // error context
        ErrorContext errorContext = new DefaultErrorContext();
        try (SourceTokenIterator iterator = analyzer.iterator(errorContext, resource)) {
            return syntaxPhaserMapper.apply(iterator, errorContext);
        } catch (CompileException e) {
            log.warn("compile error", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!errorContext.isEmpty()) {
                log.info("{}", errorContext);
            }
        }
        return null;
    }

    /**
     * 函数功能：创建演示语法分析使用的词法分析器。
     * 输入：
     * - 无。
     * 输出：配置完成的 LexicalAnalyzer。
     */
    private static LexicalAnalyzer lexicalAnalyzer() {
        AlphabetCharacterFactory alphabetCharacterFactory = new RegexAlphabetCharacterFactory();
        RegexDfaStatusTable table = LexicalDemo.buildTable(alphabetCharacterFactory);
        SourceAlphabetCharacterAdaptorImpl saca = new SourceAlphabetCharacterAdaptorImpl(alphabetCharacterFactory);
        return new DefaultLexicalAnalyzer(table, saca);
    }

    public static final boolean FLUSH_TABLE = RuntimeProperties.syntaxFlushTable();
    private static volatile ShiftReduceParsingTable cachedShiftReduceParsingTable;

    /**
     * 函数功能：构建或加载移进归约分析表并缓存结果。
     * 输入：
     * - startHead：文法开始符号名称。
     * - context：产生式集合上下文。
     * - filename：分析表序列化文件名。
     * - tagLoader：语义标签加载器。
     * - tagComparator：语义标签比较器。
     * 输出：可用于移进归约分析的 ShiftReduceParsingTable。
     */
    public static ShiftReduceParsingTable buildShiftReduceParsingTable(
            String startHead,
            ProductionSetContext context,
            String filename,
            SemanticTag.Loader<?> tagLoader,
            SemanticTagComparator<? super SemanticTag> tagComparator) {
        if (cachedShiftReduceParsingTable != null) {
            return cachedShiftReduceParsingTable;
        }
        synchronized (SyntaxDemo.class) {
            if (cachedShiftReduceParsingTable != null) {
                return cachedShiftReduceParsingTable;
            }
            ShiftReduceParsingTable table;
            if (FLUSH_TABLE) {
                table = buildShiftReduceParsingTable0(startHead, context, tagComparator);
                try {
                    storeTable(table, filename);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try (InputStream is = new FileInputStream("src/main/resources/serial/" + filename)) {
                    ShiftReduceParsingTableImpl.Loader loader = getLoader(tagLoader);
                    table = loader.load(is);
                    log.info("loaded = {}", table);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            cachedShiftReduceParsingTable = table;
            return table;
        }
    }

    /**
     * 函数功能：从序列化文件加载移进归约分析表。
     * 输入：
     * - filename：分析表序列化文件名。
     * - tagLoader：语义标签加载器。
     * 输出：加载得到的 ShiftReduceParsingTable。
     */
    public static ShiftReduceParsingTable loadShiftReduceParsingTable(
            String filename, SemanticTag.Loader<?> tagLoader) {
        try (InputStream is = new FileInputStream("src/main/resources/serial/" + filename)) {
            ShiftReduceParsingTableImpl.Loader loader = getLoader(tagLoader);
            return loader.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 函数功能：将移进归约分析表保存到序列化文件。
     * 输入：
     * - table：待保存的移进归约分析表。
     * - filename：分析表序列化文件名。
     * 输出：无。
     */
    private static void storeTable(ShiftReduceParsingTable table, String filename) throws IOException {
        try (OutputStream os = new FileOutputStream("src/main/resources/serial/" + filename)) {
            int store = table.store(os);
            os.flush();
            log.info("store = {}", store);
        }
    }

    /**
     * 函数功能：创建移进归约分析表加载器。
     * 输入：
     * - tagLoader：语义标签加载器。
     * 输出：配置完成的 ShiftReduceParsingTableImpl.Loader。
     */
    private static ShiftReduceParsingTableImpl.Loader getLoader(SemanticTag.Loader<?> tagLoader) {
        TokenTypeTerminalSymbol.Loader terminalSymbolLoader = new TokenTypeTerminalSymbol.Loader(new ProgramTokenType.Loader());
        HeadDefineSymbolImpl.Loader headSymbolLoader = new HeadDefineSymbolImpl.Loader();
        DefineSimpleGrammarProduction.Loader productionLoader = new DefineSimpleGrammarProduction.Loader(
                headSymbolLoader,
                terminalSymbolLoader,
                tagLoader
        );
        return new ShiftReduceParsingTableImpl.Loader(
                terminalSymbolLoader,
                headSymbolLoader,
                productionLoader,
                MATCHER_FACTORY
        );
    }

    /**
     * 函数功能：根据产生式上下文构建移进归约分析表。
     * 输入：
     * - startHead：文法开始符号名称。
     * - context：产生式集合上下文。
     * - tagComparator：语义标签比较器。
     * 输出：构建完成的 ShiftReduceParsingTable。
     */
    private static ShiftReduceParsingTable buildShiftReduceParsingTable0(
            String startHead, ProductionSetContext context, SemanticTagComparator<? super SemanticTag> tagComparator) {
        System.out.println("----------first map-------------");
        FirstMapFactory firstMapFactory = new IterativeFixedPointFirstMapFactory();
        FirstMap firstMap = firstMapFactory.first(context);
        firstMap.forEach(System.out::println);
        System.out.println("-----------item set family------------");
        ItemSetFamilyFactory itemSetFamilyFactory = new ItemSetFamilyFactoryImpl();
        ItemSetFamily family = itemSetFamilyFactory.produce(startHead, context, firstMap);
        showItemSetFamily(family);
        System.out.println("-----------look ahead------------");
        LookaheadMapFactory lookaheadMapFactory = new LookaheadMapFactoryImpl();
        LookaheadMap[] lookaheadMaps = lookaheadMapFactory.produce(startHead, context, family, firstMap);
        int cur = 0;
        for (LookaheadMap lookaheadMap : lookaheadMaps) {
            System.out.println("I" + (cur++) + ": " + lookaheadMap);
        }
        System.out.println("------------shift reduce table-----------");
        ShiftReduceParsingTableFactory shiftReduceParsingTableFactory = new ShiftReduceParsingTableFactoryImpl(
                MATCHER_FACTORY,
                tagComparator
        );
        ShiftReduceParsingTable shiftReduceParsingTable = shiftReduceParsingTableFactory.produce(
                startHead,
                context,
                firstMap,
                family,
                lookaheadMaps
        );
        System.out.println(shiftReduceParsingTable);
        System.out.println("---------finish all--------------");
        return shiftReduceParsingTable;
    }

    /**
     * 函数功能：输出项目集族及其转移信息。
     * 输入：
     * - family：待展示的项目集族。
     * 输出：无。
     */
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

    /**
     * 函数功能：构建预测分析表并输出相关文法集合信息。
     * 输入：
     * - startHead：文法开始符号名称。
     * 输出：构建完成的 PredictiveParsingTable。
     */
    public static PredictiveParsingTable buildPredictiveParsingTable(String startHead) {
        ProductionSetContext context = ProductionSetContextBuilds.build4(TERMINAL_FACTORY);
        System.out.println(context);
        System.out.println("-----------消除左递归和提取左因子------------");
        LeftRecursionEliminator leftRecursionEliminator = new LeftRecursionEliminatorImpl(s -> s + '\'');
        LeftFactoringEliminator leftFactoringEliminator = new LeftFactoringEliminatorImpl((s, i) -> s + i);
        ProductionSetContext eliminated = leftFactoringEliminator.eliminate(leftRecursionEliminator.eliminate(context));
        System.out.println(eliminated);
        System.out.println("------------first-----------");
        FirstMapFactory firstMapFactory = new NaiveRecursiveFirstMapFactory();
        FirstMap firstMap = firstMapFactory.first(eliminated);
        firstMap.forEach(System.out::println);
        System.out.println("-----------follow------------");
        FollowSetFactory followSetFactory = new FollowSetFactoryImpl();
        FollowMap followMap = followSetFactory.follow(startHead, eliminated, firstMap);
        followMap.entrySet().forEach(System.out::println);
        System.out.println("---------table--------------");
        PredictiveParsingTableFactory tableFactory = new DeterministicPredictiveParsingTableFactory(MATCHER_FACTORY);
        PredictiveParsingTable predictiveParsingTable = tableFactory.produce(eliminated, firstMap, followMap);
        System.out.println(predictiveParsingTable);
        System.out.println("-----------------------");
        return predictiveParsingTable;
    }

    /**
     * 函数功能：运行预测分析表构建演示入口。
     * 输入：
     * - args：命令行参数数组。
     * 输出：无。
     */
    public static void main(String[] args) {
        buildPredictiveParsingTable("bexpr");
    }
}


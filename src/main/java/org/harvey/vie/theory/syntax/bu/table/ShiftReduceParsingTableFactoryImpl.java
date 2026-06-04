package org.harvey.vie.theory.syntax.bu.table;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.syntax.bu.item.ItemSet;
import org.harvey.vie.theory.syntax.bu.item.ItemSetFamily;
import org.harvey.vie.theory.syntax.bu.item.ProductionItem;
import org.harvey.vie.theory.syntax.bu.la.LookaheadMap;
import org.harvey.vie.theory.syntax.bu.table.element.AcceptTableElementImpl;
import org.harvey.vie.theory.syntax.bu.table.element.ActiveTableElement;
import org.harvey.vie.theory.syntax.bu.table.element.ReduceTableElementImpl;
import org.harvey.vie.theory.syntax.bu.table.element.ShiftTableElementImpl;
import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.produce.DefineSimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.produce.GrammarDefineProduction;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTagComparator;
import org.harvey.vie.theory.util.CollectionUtil;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:44
 */
public class ShiftReduceParsingTableFactoryImpl implements ShiftReduceParsingTableFactory {

    private final TerminalMatcherFactory terminalMatcherFactory;
    private final SemanticTagComparator<? super SemanticTag> tagComparator;

    /**
     * 函数功能：创建 ShiftReduceParsingTableFactoryImpl 对象。
     * 输入：
     * - terminalMatcherFactory：TerminalMatcherFactory 类型参数。
     * - tagComparator：SemanticTagComparator<? super SemanticTag> 类型参数。
     * 输出：无。
     */

    public ShiftReduceParsingTableFactoryImpl(
            TerminalMatcherFactory terminalMatcherFactory,
            SemanticTagComparator<? super SemanticTag> tagComparator) {
        this.terminalMatcherFactory = terminalMatcherFactory;
        this.tagComparator = tagComparator;
    }

    /**
     * 函数功能：构建移进规约动作表。
     * 输入：
     * - pc：ParsingContext 类型参数。
     * 输出：ActiveTable 类型返回值。
     */


    private static ActiveTable active(ParsingContext pc) {
        // ACTION 表: 行 = 状态, 列 = 终结符(包括 $)
        ActiveTableElement[][] activeTable = pc.initActive();
        Map<TerminalSymbol, Integer> terminalDict = CollectionUtil.dict(pc.terminalSymbols);
        // 2. 填充 ACTION 表, 对每个状态 I，检查其中的每个项目:
        int acceptStatus = -1;
        for (int i = 0; i < activeTable.length; i++) {
            ItemSet set = pc.itemSet(i);
            ActiveTableElement[] raw = activeTable[i];
            for (ProductionItem item : set) {
                if (item.hasNextSymbol()) {
                    // 2.1 shift
                    //  项目为 [A -> α·aβ], a 是终结符, 且 GOTO(I, a) = J, 则
                    //  ACTION[I, a] = shift J
                    GrammarUnitSymbol unitSymbol = item.nextSymbol();
                    if (unitSymbol.isTerminal()) {
                        TerminalSymbol terminal = unitSymbol.toTerminal();
                        int col = CollectionUtil.validIndex(terminalDict, terminal);
                        setWithoutConflict(raw, col, pc.shift(i, terminal), i, terminal, pc);
                    }
                } else {
                    // A -> γ·
                    HeadSymbol head = item.getHead();
                    AlterableSymbol body = item.getAlterableSymbol();
                    int production = pc.getProductionId(head, body);
                    if (pc.equalsStart(head)) {
                        // 是增广开始符
                        // 2.3 accept
                        //  若项目为 [S' -> S·](增广开始符), 则
                        //  ACTION[I, $] = accept
                        setWithoutConflict(raw, 0, pc.accept(production), i, TerminalSymbol.END_MARK_SYMBOL, pc);
                        acceptStatus = i;
                    } else {
                        // 2.2 reduce
                        //  若项目为 [A -> γ·], 且 A 不是增广开始符，
                        //  则对其 Lookahead 集合中的每个终结符 t(包括 $ 如果存在)：
                        //  ACTION[I, t] = reduce A -> γ
                        for (TerminalSymbol terminalSymbol : pc.lookahead(i, item)) {
                            int t = CollectionUtil.validIndex(terminalDict, terminalSymbol);
                            setWithoutConflict(raw, t, pc.reduce(production), i, terminalSymbol, pc);
                        }
                    }
                }
            }
        }
        return new ActiveTable(activeTable, acceptStatus);
    }

    /**
     * 函数功能：设置无冲突的动作表元素。
     * 输入：
     * - raw：ActiveTableElement[] 类型参数。
     * - col：int 类型参数。
     * - element：ActiveTableElement 类型参数。
     * - state：int 类型参数。
     * - terminal：TerminalSymbol 类型参数。
     * - pc：ParsingContext 类型参数。
     * 输出：无。
     */

    private static void setWithoutConflict(
            ActiveTableElement[] raw,
            int col,
            ActiveTableElement element,
            int state,
            TerminalSymbol terminal,
            ParsingContext pc) {
        if (raw[col] == null) {
            raw[col] = element;
            return;
        }
        if (raw[col].conflict(element)) {
            String existing = pc.describe(raw[col]);
            String incoming = pc.describe(element);
            throw new IllegalStateException("The grammar do not fix in LALR for conflict between " +
                                            existing +
                                            " and " +
                                            incoming +
                                            " at state " +
                                            state +
                                            " on terminal " +
                                            terminal);
        }

    }

    /**
     * 函数功能：构建 GOTO 状态转移表。
     * 输入：
     * - pc：ParsingContext 类型参数。
     * 输出：int[][] 类型数组。
     */

    private static int[][] gotoTable(ParsingContext pc) {
        // 1. 初始化分析表
        //  GOTO 表：行 = 状态，列 = 非终结符（除了增广开始符）
        int[][] gotoTable = pc.initGoto();
        // 3. 填充 GOTO 表
        //  对每个状态 I 和每个非终结符 B(不是增广开始符)，
        //  若 GOTO(I, B) = J，则 GOTO[I, B] = J
        for (int i = 0; i < gotoTable.length; i++) {
            ItemSet set = pc.itemSet(i);
            for (int j = 0, jEnd = gotoTable[i].length; j < jEnd; j++) {
                gotoTable[i][j] = set.gotoUnit(pc.headSymbol(j));
            }
        }
        return gotoTable;
    }

    /**
     * 函数功能：根据输入数据创建目标对象。
     * 输入：
     * - startHead：String 类型参数。
     * - context：ProductionSetContext 类型参数。
     * - firstMap：FirstMap 类型参数。
     * - family：ItemSetFamily 类型参数。
     * - lookaheadMaps：LookaheadMap[] 类型参数。
     * 输出：ShiftReduceParsingTable 类型返回值。
     */

    @Override
    public ShiftReduceParsingTable produce(
            String startHead,
            ProductionSetContext context,
            FirstMap firstMap,
            ItemSetFamily family,
            LookaheadMap[] lookaheadMaps) {
        ParsingContext pc = new ParsingContext(startHead, context, firstMap, family, lookaheadMaps, tagComparator);
        ActiveTable activeTable = active(pc);
        int[][] gotoTable = gotoTable(pc);
        return pc.build(activeTable.table, gotoTable, activeTable.accept, terminalMatcherFactory);
    }

    @AllArgsConstructor
    private static class ActiveTable {
        private final ActiveTableElement[][] table;
        private final int accept;
    }

    private static class ParsingContext {
        private final SemanticTagComparator<? super SemanticTag> tagComparator;
        private final ProductionSetContext context;
        private final FirstMap firstMap;
        private final ItemSetFamily family;
        private final LookaheadMap[] lookaheadMaps;
        private final HeadDefineSymbol start;
        private final TerminalSymbol[] terminalSymbols;
        private final HeadSymbol[] headSymbols;
        private final Map<SimpleGrammarProduction, Integer> productionDict;
        private final List<SimpleGrammarProduction> productionList;

        /**
         * 函数功能：创建 ParsingContext 对象。
         * 输入：
         * - startHead：String 类型参数。
         * - context：ProductionSetContext 类型参数。
         * - firstMap：FirstMap 类型参数。
         * - family：ItemSetFamily 类型参数。
         * - lookaheadMaps：LookaheadMap[] 类型参数。
         * - tagComparator：SemanticTagComparator<? super SemanticTag> 类型参数。
         * 输出：无。
         */

        public ParsingContext(
                String startHead,
                ProductionSetContext context,
                FirstMap firstMap,
                ItemSetFamily family,
                LookaheadMap[] lookaheadMaps, SemanticTagComparator<? super SemanticTag> tagComparator) {
            this.context = context;
            this.firstMap = firstMap;
            this.family = family;
            this.start = context.getDefinition(startHead);
            this.lookaheadMaps = lookaheadMaps;
            this.terminalSymbols = terminalSymbolsWithEndMark();
            this.headSymbols = headSymbolsFilterHead();
            this.productionDict = new HashMap<>();
            this.productionList = new ArrayList<>();
            this.tagComparator = tagComparator;
        }

        /**
         * 函数功能：获取包含结束标记的终结符数组。
         * 输入：
         * - 无。
         * 输出：TerminalSymbol[] 类型数组。
         */

        private TerminalSymbol[] terminalSymbolsWithEndMark() {
            Set<TerminalSymbol> terminalSet = firstMap.terminalSet();
            TerminalSymbol[] terminalSymbols = new TerminalSymbol[terminalSet.size() + 1];
            terminalSymbols[0] = TerminalSymbol.END_MARK_SYMBOL;
            int i = 1;
            for (TerminalSymbol terminalSymbol : terminalSet) {
                terminalSymbols[i++] = terminalSymbol;
            }
            return terminalSymbols;
        }

        /**
         * 函数功能：获取过滤后的非终结符数组。
         * 输入：
         * - 无。
         * 输出：HeadSymbol[] 类型数组。
         */

        private HeadSymbol[] headSymbolsFilterHead() {
            return firstMap.headSet().stream().filter(Predicate.not(start::equals)).toArray(HeadSymbol[]::new);
        }

        /**
         * 函数功能：构建目标对象。
         * 输入：
         * - activeTable：ActiveTableElement[][] 类型参数。
         * - gotoTable：int[][] 类型参数。
         * - accept：int 类型参数。
         * - terminalMatcherFactory：TerminalMatcherFactory 类型参数。
         * 输出：ShiftReduceParsingTable 类型返回值。
         */

        public ShiftReduceParsingTable build(
                ActiveTableElement[][] activeTable,
                int[][] gotoTable,
                int accept,
                TerminalMatcherFactory terminalMatcherFactory) {
            return new ShiftReduceParsingTableImpl(
                    family.startIndex(),
                    accept,
                    terminalSymbols,
                    headSymbols,
                    activeTable,
                    gotoTable,
                    productionArray(),
                    terminalMatcherFactory
            );
        }

        /**
         * 函数功能：获取语法产生式数组。
         * 输入：
         * - 无。
         * 输出：SimpleGrammarProduction[] 类型数组。
         */

        private SimpleGrammarProduction[] productionArray() {
            return productionDict.entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .toArray(SimpleGrammarProduction[]::new);
        }

        /**
         * 函数功能：初始化移进规约动作表。
         * 输入：
         * - 无。
         * 输出：ActiveTableElement[][] 类型数组。
         */

        public ActiveTableElement[][] initActive() {
            return new ActiveTableElement[family.size()][terminalSymbols.length];
        }

        /**
         * 函数功能：获取指定索引的项目集。
         * 输入：
         * - i：int 类型参数。
         * 输出：ItemSet 类型返回值。
         */

        public ItemSet itemSet(int i) {
            return family.get(i);
        }

        /**
         * 函数功能：判断项目是否为起始项目。
         * 输入：
         * - head：HeadSymbol 类型参数。
         * 输出：判断结果布尔值。
         */

        public boolean equalsStart(HeadSymbol head) {
            return start.equals(head);
        }

        /**
         * 函数功能：获取指定产生式。
         * 输入：
         * - head：HeadSymbol 类型参数。
         * 输出：GrammarDefineProduction 类型返回值。
         */

        public GrammarDefineProduction getProduction(HeadSymbol head) {
            if (!head.isDefine()) {
                throw new IllegalStateException("Only support definition head symbol");
            }
            return context.get(context.indexOf(head.toDefine()));
        }

        /**
         * 函数功能：获取项目的展望符集合。
         * 输入：
         * - setIndex：int 类型参数。
         * - item：ProductionItem 类型参数。
         * 输出：Set<TerminalSymbol> 类型集合或迭代结果。
         */

        public Set<TerminalSymbol> lookahead(int setIndex, ProductionItem item) {
            return lookaheadMaps[setIndex].get(item);
        }

        /**
         * 函数功能：设置移进动作。
         * 输入：
         * - i：int 类型参数。
         * - terminal：TerminalSymbol 类型参数。
         * 输出：ActiveTableElement 类型返回值。
         */

        public ActiveTableElement shift(int i, TerminalSymbol terminal) {
            return new ShiftTableElementImpl(family.get(i).gotoUnit(terminal));
        }

        /**
         * 函数功能：设置规约动作。
         * 输入：
         * - production：int 类型参数。
         * 输出：ActiveTableElement 类型返回值。
         */


        public ActiveTableElement reduce(int production) {
            return new ReduceTableElementImpl(production);
        }

        /**
         * 函数功能：判断或获取接受状态。
         * 输入：
         * - production：int 类型参数。
         * 输出：ActiveTableElement 类型返回值。
         */

        public ActiveTableElement accept(int production) {
            return new AcceptTableElementImpl(production);
        }

        /**
         * 函数功能：获取产生式编号。
         * 输入：
         * - head：HeadSymbol 类型参数。
         * - body：AlterableSymbol 类型参数。
         * 输出：整数结果。
         */

        public int getProductionId(HeadSymbol head, AlterableSymbol body) {
            if (!head.isDefine()) {
                throw new IllegalStateException("It is not allowed to build phasing table with non-define head. " +
                                                "It is not supported.");
            }

            SemanticTag[] tags = Stream.concat(Arrays.stream(head.getTags()), Arrays.stream(body.getTags()))
                    .distinct()
                    .sorted(tagComparator)
                    .toArray(SemanticTag[]::new);
            return productionDict.computeIfAbsent(
                    new DefineSimpleGrammarProduction(head.toDefine(), body, tags),
                    k -> {
                        int id = productionDict.size();
                        productionList.add(k);
                        return id;
                    }
            );
        }

        /**
         * 函数功能：获取指定产生式。
         * 输入：
         * - id：int 类型参数。
         * 输出：SimpleGrammarProduction 类型返回值。
         */

        public SimpleGrammarProduction getProduction(int id) {
            return productionList.get(id);
        }

        /**
         * 函数功能：描述动作表冲突信息。
         * 输入：
         * - element：ActiveTableElement 类型参数。
         * 输出：字符串结果。
         */

        public String describe(ActiveTableElement element) {
            if (element instanceof ReduceTableElementImpl) {
                ReduceTableElementImpl reduce = (ReduceTableElementImpl) element;
                return "reduce " + reduce.getProduction() + " (" + getProduction(reduce.getProduction()) + ")";
            }
            if (element instanceof AcceptTableElementImpl) {
                AcceptTableElementImpl accept = (AcceptTableElementImpl) element;
                return "accept " + accept.getProduction() + " (" + getProduction(accept.getProduction()) + ")";
            }
            if (element instanceof ShiftTableElementImpl) {
                ShiftTableElementImpl shift = (ShiftTableElementImpl) element;
                return "shift " + shift.nextStatus();
            }
            return String.valueOf(element);
        }

        /**
         * 函数功能：初始化 GOTO 状态转移表。
         * 输入：
         * - 无。
         * 输出：int[][] 类型数组。
         */

        public int[][] initGoto() {
            return new int[family.size()][headSymbols.length];
        }

        /**
         * 函数功能：获取指定索引的非终结符。
         * 输入：
         * - i：int 类型参数。
         * 输出：GrammarUnitSymbol 类型返回值。
         */

        public GrammarUnitSymbol headSymbol(int i) {
            return headSymbols[i];
        }

        /**
         * 函数功能：获取指定索引的终结符。
         * 输入：
         * - i：int 类型参数。
         * 输出：TerminalSymbol 类型返回值。
         */

        public TerminalSymbol terminalSymbol(int i) {
            return terminalSymbols[i];
        }


    }
}

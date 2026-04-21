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
import org.harvey.vie.theory.util.CollectionUtil;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:44
 */
public class ShiftReduceParsingTableFactoryImpl implements ShiftReduceParsingTableFactory {


    private final TerminalMatcherFactory terminalMatcherFactory;

    public ShiftReduceParsingTableFactoryImpl(TerminalMatcherFactory terminalMatcherFactory) {
        this.terminalMatcherFactory = terminalMatcherFactory;
    }

    @Override
    public ShiftReduceParsingTable produce(
            String startHead,
            ProductionSetContext context,
            FirstMap firstMap,
            ItemSetFamily family,
            LookaheadMap[] lookaheadMaps) {
        ParsingContext pc = new ParsingContext(startHead, context, firstMap, family, lookaheadMaps);
        ActiveTable activeTable = active(pc);
        int[][] gotoTable = gotoTable(pc);
        return pc.build(activeTable.table, gotoTable, activeTable.accept, terminalMatcherFactory);
    }


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
                        setWithoutConflict(raw, col, pc.shift(i, terminal));
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
                        setWithoutConflict(raw, 0, pc.accept(production));
                        acceptStatus = i;
                    } else {
                        // 2.2 reduce
                        //  若项目为 [A -> γ·], 且 A 不是增广开始符，
                        //  则对其 Lookahead 集合中的每个终结符 t(包括 $ 如果存在)：
                        //  ACTION[I, t] = reduce A -> γ
                        pc.lookahead(i, item)
                                .stream()
                                .map(t -> CollectionUtil.validIndex(terminalDict, t))
                                .forEach(t -> setWithoutConflict(raw, t, pc.reduce(production)));
                    }
                }
            }
        }
        return new ActiveTable(activeTable, acceptStatus);
    }

    @AllArgsConstructor
    private static class ActiveTable {
        private final ActiveTableElement[][] table;
        private final int accept;
    }

    private static void setWithoutConflict(ActiveTableElement[] raw, int col, ActiveTableElement element) {
        if (raw[col] == null) {
            raw[col] = element;
            return;
        }
        if (raw[col].conflict(element)) {
            throw new IllegalStateException("The grammar do not fix in LALR for conflict between " +
                                            raw[col] +
                                            " and " +
                                            element);
        }

    }

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

    private static class ParsingContext {

        private final ProductionSetContext context;
        private final FirstMap firstMap;
        private final ItemSetFamily family;
        private final LookaheadMap[] lookaheadMaps;
        private final HeadDefineSymbol start;
        private final TerminalSymbol[] terminalSymbols;
        private final HeadSymbol[] headSymbols;
        private final Map<SimpleGrammarProduction, Integer> productionDict;

        public ParsingContext(
                String startHead,
                ProductionSetContext context,
                FirstMap firstMap,
                ItemSetFamily family,
                LookaheadMap[] lookaheadMaps) {
            this.context = context;
            this.firstMap = firstMap;
            this.family = family;
            this.start = context.getDefinition(startHead);
            this.lookaheadMaps = lookaheadMaps;
            this.terminalSymbols = terminalSymbolsWithEndMark();
            this.headSymbols = headSymbolsFilterHead();
            this.productionDict = new HashMap<>();
        }

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

        private HeadSymbol[] headSymbolsFilterHead() {
            return firstMap.headSet().stream().filter(Predicate.not(start::equals)).toArray(HeadSymbol[]::new);
        }

        public ShiftReduceParsingTable build(
                ActiveTableElement[][] activeTable, int[][] gotoTable, int accept, TerminalMatcherFactory terminalMatcherFactory) {
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

        private SimpleGrammarProduction[] productionArray() {
            return productionDict.entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .toArray(SimpleGrammarProduction[]::new);
        }

        public ActiveTableElement[][] initActive() {
            return new ActiveTableElement[family.size()][terminalSymbols.length];
        }

        public ItemSet itemSet(int i) {
            return family.get(i);
        }

        public boolean equalsStart(HeadSymbol head) {
            return start.equals(head);
        }

        public GrammarDefineProduction getProduction(HeadSymbol head) {
            if (!head.isDefine()) {
                throw new IllegalStateException("Only support definition head symbol");
            }
            return context.get(context.indexOf(head.toDefine()));
        }

        public Set<TerminalSymbol> lookahead(int setIndex, ProductionItem item) {
            return lookaheadMaps[setIndex].get(item);
        }

        public ActiveTableElement shift(int i, TerminalSymbol terminal) {
            return new ShiftTableElementImpl(family.get(i).gotoUnit(terminal));
        }


        public ActiveTableElement reduce(int production) {
            return new ReduceTableElementImpl(production);
        }

        public ActiveTableElement accept(int production) {
            return new AcceptTableElementImpl(production);
        }

        public int getProductionId(HeadSymbol head, AlterableSymbol body) {
            if (!head.isDefine()) {
                throw new IllegalStateException("It is not allowed to build phasing table with non-define head. " +
                                                "It is not supported.");
            }
            return productionDict.computeIfAbsent(
                    new DefineSimpleGrammarProduction(head.toDefine(), body),
                    k -> productionDict.size()
            );
        }

        public int[][] initGoto() {
            return new int[family.size()][headSymbols.length];
        }

        public GrammarUnitSymbol headSymbol(int i) {
            return headSymbols[i];
        }


    }
}

package org.harvey.vie.theory.syntax.td.table;

import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.first.FirstSet;
import org.harvey.vie.theory.syntax.grammar.follow.FollowMap;
import org.harvey.vie.theory.syntax.grammar.follow.FollowSet;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

import java.util.*;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 19:58
 */
public class DeterministicPredictiveParsingTableFactory implements PredictiveParsingTableFactory {
    private final TerminalMatcherFactory matcherFactory;

    /**
     * 函数功能：创建 DeterministicPredictiveParsingTableFactory 对象。
     * 输入：
     * - matcherFactory：TerminalMatcherFactory 类型参数。
     * 输出：无。
     */

    public DeterministicPredictiveParsingTableFactory(TerminalMatcherFactory matcherFactory) {this.matcherFactory = matcherFactory;}

    /**
     * 函数功能：生成单个预测分析表项。
     * 输入：
     * - symbol：AlterableSymbol 类型参数。
     * - builder：PredictiveParsingTableBuilder 类型参数。
     * - headIndex：int 类型参数。
     * 输出：无。
     */

    private static void produceEach(AlterableSymbol symbol, PredictiveParsingTableBuilder builder, int headIndex) {
        int rightId;
        FirstSet firstSet;
        FollowSet followSet = builder.follow(headIndex);
        if (symbol.isEpsilon()) {
            rightId = PredictiveParsingTableBuilder.EPSILON_REFERENCE;
            firstSet = FirstSet.EPSILON;
        } else if (symbol.isConcatenable()) {
            if (symbol.isConcatenation()) {
                GrammarConcatenation concatenation = symbol.toConcatenation();
                rightId = builder.addConcatenation(concatenation);
                firstSet = builder.first(concatenation);
            } else {
                throw new IllegalStateException("Illegal concatenable symbol type: " + symbol.getClass());
            }
        } else {
            throw new IllegalStateException("Illegal grammar symbol type: " + symbol.getClass());
        }
        // 步骤2
        for (int j = 1; j < builder.terminalSymbolArray.length; j++) {
            TerminalSymbol terminalSymbol = builder.terminalSymbolArray[j];
            if (firstSet.contains(terminalSymbol)) {
                builder.set(headIndex, j, rightId);
            }
        }
        // 步骤3
        if (firstSet.containsEpsilon()) {
            // 步骤3.1
            for (TerminalSymbol terminalSymbol : followSet.followExceptEndMarker()) {
                builder.set(headIndex, builder.terminalId(terminalSymbol), rightId);
            }
            // 步骤3.2
            if (followSet.containsEndMarker()) {
                builder.setEndMark(headIndex, rightId);
            }
        }
    }

    /**
     * 函数功能：根据输入数据创建目标对象。
     * 输入：
     * - context：ProductionSetContext 类型参数。
     * - firstMap：FirstMap 类型参数。
     * - followMap：FollowMap 类型参数。
     * 输出：PredictiveParsingTable 类型返回值。
     */

    @Override
    public PredictiveParsingTable produce(ProductionSetContext context, FirstMap firstMap, FollowMap followMap) {
        PredictiveParsingTableBuilder builder = new PredictiveParsingTableBuilder(context, firstMap, followMap);
        for (int i = 0; i < builder.headLength(); i++) {
            for (AlterableSymbol symbol : builder.getAlternation(i)) {
                produceEach(symbol, builder, i);
            }
        }
        return builder.build(matcherFactory);
    }

    private static class PredictiveParsingTableBuilder {
        private static final int EPSILON_REFERENCE = PredictiveParsingTable.EPSILON_REFERENCE;
        private static final int END_MARK_REFERENCE = PredictiveParsingTable.END_MARK_REFERENCE;
        private final ProductionSetContext context;
        private final FirstMap firstMap;
        private final FollowMap followMap;
        private final HeadSymbol[] headSymbolArray;
        /**
         * index {@link PredictiveParsingTableBuilder#END_MARK_REFERENCE} is for end mark
         */
        private final TerminalSymbol[] terminalSymbolArray;
        private final Map<TerminalSymbol, Integer> terminalIndexMap;
        private final List<GrammarConcatenation> concatenationList;
        private final PredictiveParsingTableElementBuilder[][] table;

        /**
         * 函数功能：创建 PredictiveParsingTableBuilder 对象。
         * 输入：
         * - context：ProductionSetContext 类型参数。
         * - firstMap：FirstMap 类型参数。
         * - followMap：FollowMap 类型参数。
         * 输出：无。
         */

        public PredictiveParsingTableBuilder(ProductionSetContext context, FirstMap firstMap, FollowMap followMap) {
            this.context = context;
            this.firstMap = firstMap;
            this.followMap = followMap;
            this.headSymbolArray = followMap.keySet().toArray(HeadSymbol[]::new);
            Set<TerminalSymbol> terminalSet = firstMap.terminalSet();
            int terminalLen = terminalSet.size() + 1; // +1 for $ ($ is on index 0)
            this.terminalSymbolArray = new TerminalSymbol[terminalLen];
            Iterator<TerminalSymbol> terminalIterator = terminalSet.iterator();
            terminalIndexMap = new HashMap<>();
            terminalSymbolArray[0] = PredictiveParsingTable.END_MARK_SYMBOL;
            for (int i = 1; i < terminalLen; i++) {
                terminalSymbolArray[i] = terminalIterator.next();
                terminalIndexMap.put(terminalSymbolArray[i], i);
            }
            concatenationList = new ArrayList<>();
            table = new PredictiveParsingTableElementBuilder[this.headSymbolArray.length][terminalLen];
            for (int i = 0; i < table.length; i++) {
                for (int j = 0; j < terminalLen; j++) {
                    table[i][j] = new PredictiveParsingTableElementBuilder();
                }
            }
        }

        /**
         * 函数功能：获取非终结符数量。
         * 输入：
         * - 无。
         * 输出：整数结果。
         */

        public int headLength() {
            return followMap.size();
        }

        /**
         * 函数功能：添加语法符号连接体。
         * 输入：
         * - concatenation：GrammarConcatenation 类型参数。
         * 输出：整数结果。
         */


        public int addConcatenation(GrammarConcatenation concatenation) {
            int rightId = concatenationList.size();
            concatenationList.add(concatenation);
            return rightId;
        }

        /**
         * 函数功能：获取终结符编号。
         * 输入：
         * - terminalSymbol：TerminalSymbol 类型参数。
         * 输出：整数结果。
         */

        public int terminalId(TerminalSymbol terminalSymbol) {
            Integer index = terminalIndexMap.get(terminalSymbol);
            if (index == null) {
                throw new IllegalStateException("Can not find terminal: " + terminalSymbol.hint());
            }
            return index;
        }

        /**
         * 函数功能：获取 FIRST 集合。
         * 输入：
         * - concatenation：GrammarConcatenation 类型参数。
         * 输出：FirstSet 类型返回值。
         */

        public FirstSet first(GrammarConcatenation concatenation) {
            return firstMap.first(concatenation);
        }

        /**
         * 函数功能：获取 FOLLOW 集合。
         * 输入：
         * - headId：int 类型参数。
         * 输出：FollowSet 类型返回值。
         */

        public FollowSet follow(int headId) {
            return followMap.get(headSymbolArray[headId]);
        }

        /**
         * 函数功能：设置指定位置的元素。
         * 输入：
         * - headIndex：int 类型参数。
         * - terminalIndex：int 类型参数。
         * - rightId：int 类型参数。
         * 输出：无。
         */

        public void set(int headIndex, int terminalIndex, int rightId) {
            table[headIndex][terminalIndex].set(rightId);
        }

        /**
         * 函数功能：设置结束标记对应的表项。
         * 输入：
         * - i：int 类型参数。
         * - rightId：int 类型参数。
         * 输出：无。
         */

        public void setEndMark(int i, int rightId) {
            set(i, END_MARK_REFERENCE, rightId);
        }

        /**
         * 函数功能：获取指定头部符号的候选式集合。
         * 输入：
         * - i：int 类型参数。
         * 输出：GrammarAlternation 类型返回值。
         */

        public GrammarAlternation getAlternation(int i) {
            return context.getAlternation(headSymbolArray[i]);
        }

        /**
         * 函数功能：构建目标对象。
         * 输入：
         * - matcherFactory：TerminalMatcherFactory 类型参数。
         * 输出：PredictiveParsingTable 类型返回值。
         */


        public PredictiveParsingTable build(TerminalMatcherFactory matcherFactory) {
            return new DeterministicPredictiveParsingTable(
                    headSymbolArray,
                    terminalSymbolArray,
                    concatenationList.toArray(GrammarConcatenation[]::new),
                    Arrays.stream(table)
                            .map(a -> Arrays.stream(a)
                                    .map(PredictiveParsingTableElementBuilder::build)
                                    .toArray(PredictiveParsingTableElement[]::new))
                            .toArray(PredictiveParsingTableElement[][]::new),
                    firstMap,
                    followMap,
                    matcherFactory.produce(terminalSymbolArray)
            );
        }
    }


    private static class PredictiveParsingTableElementBuilder {
        private Integer rightId;

        /**
         * 函数功能：设置指定位置的元素。
         * 输入：
         * - rightId：int 类型参数。
         * 输出：无。
         */


        public void set(int rightId) {
            if (this.rightId != null) {
                throw new IllegalStateException(
                        "Deterministic phasing table do not allowed right production body conflict.");
            }
            this.rightId = rightId;
        }

        /**
         * 函数功能：构建目标对象。
         * 输入：
         * - 无。
         * 输出：PredictiveParsingTableElement 类型返回值。
         */

        public PredictiveParsingTableElement build() {
            return new DeterministicPredictiveParsingTableElement(rightId);
        }
    }
}

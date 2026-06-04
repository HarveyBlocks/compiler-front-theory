package org.harvey.vie.theory.syntax.bu.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.io.Loaders;
import org.harvey.vie.theory.io.Storages;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.syntax.bu.table.element.ActiveTableElement;
import org.harvey.vie.theory.syntax.bu.table.element.ActiveTableSerializer;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.util.CollectionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 23:07
 */
@Getter
public class ShiftReduceParsingTableImpl implements ShiftReduceParsingTable {
    private final int start;
    private final int accept;
    // 第一个是end_mark
    private final TerminalSymbol[] terminalSymbols;
    private final HeadSymbol[] headSymbols;
    private final Map<HeadSymbol, Integer> headDict;
    private final ActiveTableElement[][] activeTable;
    private final int[][] gotoTable;
    private final Map<SimpleGrammarProduction, Integer> productionDict;
    private final SimpleGrammarProduction[] productionPool;
    private final TerminalMatcher terminalMatcher;
/**
 * 函数功能：创建 ShiftReduceParsingTableImpl 对象。
 * 输入：
 * - start：int 类型参数。
 * - accept：int 类型参数。
 * - terminalSymbols：TerminalSymbol[] 类型参数。
 * - headSymbols：HeadSymbol[] 类型参数。
 * - activeTable：ActiveTableElement[][] 类型参数。
 * - gotoTable：int[][] 类型参数。
 * - productionPool：SimpleGrammarProduction[] 类型参数。
 * - matcherFactory：TerminalMatcherFactory 类型参数。
 * 输出：无。
 */

    public ShiftReduceParsingTableImpl(
            int start,
            int accept, TerminalSymbol[] terminalSymbols,
            HeadSymbol[] headSymbols,
            ActiveTableElement[][] activeTable,
            int[][] gotoTable,
            SimpleGrammarProduction[] productionPool,
            TerminalMatcherFactory matcherFactory) {
        this.start = start;
        this.accept = accept;
        this.terminalSymbols = terminalSymbols;
        this.headSymbols = headSymbols;
        this.activeTable = activeTable;
        this.gotoTable = gotoTable;
        this.headDict = CollectionUtil.dict(headSymbols);
        this.productionDict = CollectionUtil.dict(productionPool);
        this.productionPool = productionPool;
        this.terminalMatcher = matcherFactory.produce(terminalSymbols);
    }
/**
 * 函数功能：获取规约后跳转的状态。
 * 输入：
 * - originStatus：int 类型参数。
 * - head：HeadSymbol 类型参数。
 * 输出：整数结果。
 */


    @Override
    public int gotoNext(int originStatus, HeadSymbol head) {
        return gotoTable[originStatus][CollectionUtil.validIndex(headDict, head)];
    }
/**
 * 函数功能：查询动作表中的下一动作。
 * 输入：
 * - originStatus：int 类型参数。
 * - terminal：int 类型参数。
 * 输出：ActiveTableElement 类型返回值。
 */

    @Override
    public ActiveTableElement activeNext(int originStatus, int terminal) {
        return activeTable[originStatus][terminal];
    }
/**
 * 函数功能：获取指定产生式。
 * 输入：
 * - i：int 类型参数。
 * 输出：SimpleGrammarProduction 类型返回值。
 */

    @Override
    public SimpleGrammarProduction getProduction(int i) {
        return productionPool[i];
    }
/**
 * 函数功能：判断词法单元是否匹配终结符。
 * 输入：
 * - token：SourceToken 类型参数。
 * 输出：整数结果。
 */

    @Override
    public int matchTerminal(SourceToken token) {
        return terminalMatcher.indexOf(token);
    }
/**
 * 函数功能：获取产生式编号。
 * 输入：
 * - production：SimpleGrammarProduction 类型参数。
 * 输出：整数结果。
 */

    @Override
    public Integer getProductionId(SimpleGrammarProduction production) {
        return productionDict.get(production);
    }
/**
 * 函数功能：返回当前对象的字符串表示。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    @Override
    public String toString() {
        return ShiftReduceParsingTableImplToString.toString(this);
    }
/**
 * 函数功能：将对象写入输出流。
 * 输入：
 * - os：OutputStream 类型参数。
 * 输出：整数结果。
 */

    @Override
    public int store(OutputStream os) throws IOException {
        // int start;
        // 第一个是end_mark TerminalSymbol[] terminalSymbols;
        // `HeadSymbol[] headSymbols;
        // SimpleGrammarProduction[] productionPool;
        // ActiveTableElement[][] activeTable;
        // int[][] gotoTable;`
        int start = this.start;
        int accept = this.accept;
        int statusCnt = activeTable.length;
        int terminalCnt = terminalSymbols.length;
        int headCnt = headSymbols.length;
        int productionCnt = productionPool.length;
        int len = 0;
        len += Storages.storeInteger(os, start);
        len += Storages.storeInteger(os, accept);
        len += Storages.storeInteger(os, statusCnt);
        len += Storages.storeInteger(os, terminalCnt);
        len += Storages.storeInteger(os, headCnt);
        len += Storages.storeInteger(os, productionCnt);
        for (int i = 1; i < terminalCnt; i++) {
            len += terminalSymbols[i].store(os);
        }
        for (HeadSymbol headSymbol : headSymbols) {
            if (headSymbol.isDefine()) {
                len += headSymbol.toDefine().store(os);
            } else {
                throw new IllegalStateException("It is not supported to store head of type: " + headSymbol.getClass());
            }
        }
        for (SimpleGrammarProduction production : productionPool) {
            len += production.store(os);
        }
        len += ActiveTableSerializer.store(activeTable, os);
        for (int i = 0; i < statusCnt; i++) {
            for (int j = 0; j < headCnt; j++) {
                len += Storages.storeInteger(os, gotoTable[i][j]);
            }
        }
        return len;
    }

    @AllArgsConstructor
    public static class Loader implements ShiftReduceParsingTable.Loader<ShiftReduceParsingTableImpl> {
        private final TerminalSymbol.Loader<?> terminalSymbolLoader;
        private final HeadDefineSymbol.Loader<?> headSymbolLoader;
        private final SimpleGrammarProduction.Loader<?> productionLoader;
        private final TerminalMatcherFactory matcherFactory;
/**
 * 函数功能：从输入流加载对象。
 * 输入：
 * - is：InputStream 类型参数。
 * 输出：ShiftReduceParsingTableImpl 类型返回值。
 */

        @Override
        public ShiftReduceParsingTableImpl load(InputStream is) throws IOException {
            int start = Loaders.loadInteger(is);
            int accept = Loaders.loadInteger(is);
            int statusCnt = Loaders.loadInteger(is);
            int terminalCnt = Loaders.loadInteger(is);
            int headCnt = Loaders.loadInteger(is);
            int productionCnt = Loaders.loadInteger(is);
            TerminalSymbol[] terminalSymbols = loadTerminalSymbols(is, terminalCnt);
            HeadSymbol[] headSymbols = loadHeadSymbols(is, headCnt);
            SimpleGrammarProduction[] productionPool = loadProductionPool(is, productionCnt);
            ActiveTableElement[][] activeTable = loadActiveTable(is, accept, statusCnt, terminalCnt);
            int[][] gotoTable = loadGotoTable(is, statusCnt, headCnt);
            return new ShiftReduceParsingTableImpl(start,
                    accept, terminalSymbols,
                    headSymbols,
                    activeTable,
                    gotoTable,
                    productionPool,
                    matcherFactory
            );
        }
/**
 * 函数功能：从输入流加载 GOTO 状态转移表。
 * 输入：
 * - is：InputStream 类型参数。
 * - statusCnt：int 类型参数。
 * - headCnt：int 类型参数。
 * 输出：int[][] 类型数组。
 */

        private static int[][] loadGotoTable(InputStream is, int statusCnt, int headCnt) throws IOException {
            int[][] gotoTable = new int[statusCnt][headCnt];
            for (int i = 0; i < statusCnt; i++) {
                for (int j = 0; j < headCnt; j++) {
                    gotoTable[i][j] = Loaders.loadInteger(is);
                }
            }
            return gotoTable;
        }
/**
 * 函数功能：从输入流加载移进规约动作表。
 * 输入：
 * - is：InputStream 类型参数。
 * - accept：int 类型参数。
 * - statusCnt：int 类型参数。
 * - terminalCnt：int 类型参数。
 * 输出：ActiveTableElement[][] 类型数组。
 */

        private static ActiveTableElement[][] loadActiveTable(
                InputStream is, int accept, int statusCnt, int terminalCnt) throws IOException {
            ActiveTableElement[][] activeTable = ActiveTableSerializer.load(accept,
                    0,
                    () -> new ActiveTableElement[statusCnt][terminalCnt],
                    is
            );
            return activeTable;
        }
/**
 * 函数功能：从输入流加载产生式池。
 * 输入：
 * - is：InputStream 类型参数。
 * - productionCnt：int 类型参数。
 * 输出：SimpleGrammarProduction[] 类型数组。
 */

        private SimpleGrammarProduction[] loadProductionPool(InputStream is, int productionCnt) throws IOException {
            SimpleGrammarProduction[] productionPool = new SimpleGrammarProduction[productionCnt];
            for (int i = 0; i < productionCnt; i++) {
                productionPool[i] = productionLoader.load(is);
            }
            return productionPool;
        }
/**
 * 函数功能：从输入流加载非终结符数组。
 * 输入：
 * - is：InputStream 类型参数。
 * - headCnt：int 类型参数。
 * 输出：HeadSymbol[] 类型数组。
 */

        private HeadSymbol[] loadHeadSymbols(InputStream is, int headCnt) throws IOException {
            HeadSymbol[] headSymbols = new HeadSymbol[headCnt];
            for (int i = 0; i < headCnt; i++) {
                headSymbols[i] = headSymbolLoader.load(is);
            }
            return headSymbols;
        }
/**
 * 函数功能：从输入流加载终结符数组。
 * 输入：
 * - is：InputStream 类型参数。
 * - terminalCnt：int 类型参数。
 * 输出：TerminalSymbol[] 类型数组。
 */

        private TerminalSymbol[] loadTerminalSymbols(InputStream is, int terminalCnt) throws IOException {
            TerminalSymbol[] terminalSymbols = new TerminalSymbol[terminalCnt];
            terminalSymbols[0] = TerminalSymbol.END_MARK_SYMBOL;
            for (int i = 1; i < terminalCnt; i++) {
                terminalSymbols[i] = terminalSymbolLoader.load(is);
            }
            return terminalSymbols;
        }
    }
}

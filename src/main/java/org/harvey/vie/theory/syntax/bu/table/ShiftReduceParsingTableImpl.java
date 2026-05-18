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


    @Override
    public int gotoNext(int originStatus, HeadSymbol head) {
        return gotoTable[originStatus][CollectionUtil.validIndex(headDict, head)];
    }

    @Override
    public ActiveTableElement activeNext(int originStatus, int terminal) {
        return activeTable[originStatus][terminal];
    }

    @Override
    public SimpleGrammarProduction getProduction(int i) {
        return productionPool[i];
    }

    @Override
    public int matchTerminal(SourceToken token) {
        return terminalMatcher.indexOf(token);
    }

    @Override
    public Integer getProductionId(SimpleGrammarProduction production) {
        return productionDict.get(production);
    }

    @Override
    public String toString() {
        return ShiftReduceParsingTableImplToString.toString(this);
    }

    @Override
    public int store(OutputStream os) throws IOException {
        // int start;
        // 第一个是end_mark TerminalSymbol[] terminalSymbols;
        // HeadSymbol[] headSymbols;
        // SimpleGrammarProduction[] productionPool;
        // ActiveTableElement[][] activeTable;
        // int[][] gotoTable;
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

        private static int[][] loadGotoTable(InputStream is, int statusCnt, int headCnt) throws IOException {
            int[][] gotoTable = new int[statusCnt][headCnt];
            for (int i = 0; i < statusCnt; i++) {
                for (int j = 0; j < headCnt; j++) {
                    gotoTable[i][j] = Loaders.loadInteger(is);
                }
            }
            return gotoTable;
        }

        private static ActiveTableElement[][] loadActiveTable(
                InputStream is, int accept, int statusCnt, int terminalCnt) throws IOException {
            ActiveTableElement[][] activeTable = ActiveTableSerializer.load(accept,
                    0,
                    () -> new ActiveTableElement[statusCnt][terminalCnt],
                    is
            );
            return activeTable;
        }

        private SimpleGrammarProduction[] loadProductionPool(InputStream is, int productionCnt) throws IOException {
            SimpleGrammarProduction[] productionPool = new SimpleGrammarProduction[productionCnt];
            for (int i = 0; i < productionCnt; i++) {
                productionPool[i] = productionLoader.load(is);
            }
            return productionPool;
        }

        private HeadSymbol[] loadHeadSymbols(InputStream is, int headCnt) throws IOException {
            HeadSymbol[] headSymbols = new HeadSymbol[headCnt];
            for (int i = 0; i < headCnt; i++) {
                headSymbols[i] = headSymbolLoader.load(is);
            }
            return headSymbols;
        }

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

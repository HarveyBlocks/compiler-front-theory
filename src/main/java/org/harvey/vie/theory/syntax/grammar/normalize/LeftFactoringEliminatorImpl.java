package org.harvey.vie.theory.syntax.grammar.normalize;

import org.harvey.vie.theory.syntax.grammar.normalize.trie.ProductionBodyTrie;
import org.harvey.vie.theory.syntax.grammar.normalize.trie.ProductionBodyTrieFactory;
import org.harvey.vie.theory.syntax.grammar.normalize.trie.ProductionBodyTrieFactoryImpl;
import org.harvey.vie.theory.syntax.grammar.normalize.trie.ProductionBodyTrieNode;
import org.harvey.vie.theory.syntax.grammar.produce.*;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.util.IdGenerator;

import java.util.Iterator;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 20:50
 */
public class LeftFactoringEliminatorImpl implements LeftFactoringEliminator {
    private final DefineNameFactory nameFactory;
    private final ProductionBodyTrieFactory trieFactory;

    /**
     * 函数功能：创建 LeftFactoringEliminatorImpl 对象。
     * 输入：
     * - factory：DefineNameFactory 类型参数。
     * 输出：无。
     */

    public LeftFactoringEliminatorImpl(DefineNameFactory factory) {
        this.nameFactory = factory;
        trieFactory = new ProductionBodyTrieFactoryImpl();
    }

    /**
     * 函数功能：消除语法中的指定结构。
     * 输入：
     * - context：ProductionSetContext 类型参数。
     * 输出：ProductionSetContext 类型返回值。
     */

    @Override
    public ProductionSetContext eliminate(ProductionSetContext context) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(context.getTerminalFactory());
        for (GrammarDefineProduction production : context) {
            String originName = ((HeadDefineSymbol) production.getHead()).getName();
            ProductionBodyTrie productionTrie = trieFactory.create(production.getBody());
            GrammarProductionBuilder defineBuilder = contextBuilder.define(originName);
            TrieContext trieContext = new TrieContext(
                    originName,
                    contextBuilder,
                    new IdGenerator(),
                    productionTrie.getRoot(),
                    defineBuilder
            );
            trieContext.deal();
            if (production.getBody().alternatedEpsilon()) {
                defineBuilder.alternateEpsilon();
            }
        }
        return contextBuilder.build();
    }

    class TrieContext {
        private final ProductionBodyTrieNode cur;
        private final GrammarProductionBuilder defineBuilder;
        private final String baseName;
        private final ProductionSetContextBuilder contextBuilder;
        private final IdGenerator counter;

        /**
         * 函数功能：创建 TrieContext 对象。
         * 输入：
         * - baseName：String 类型参数。
         * - contextBuilder：ProductionSetContextBuilder 类型参数。
         * - counter：IdGenerator 类型参数。
         * - cur：ProductionBodyTrieNode 类型参数。
         * - defineBuilder：GrammarProductionBuilder 类型参数。
         * 输出：无。
         */

        public TrieContext(
                String baseName,
                ProductionSetContextBuilder contextBuilder,
                IdGenerator counter,
                ProductionBodyTrieNode cur,
                GrammarProductionBuilder defineBuilder) {
            this.baseName = baseName;
            this.contextBuilder = contextBuilder;
            this.counter = counter;
            this.cur = cur;
            this.defineBuilder = defineBuilder;
        }

        /**
         * 函数功能：转换为子节点候选式。
         * 输入：
         * - child：ProductionBodyTrieNode 类型参数。
         * - childDefineBuilder：GrammarProductionBuilder 类型参数。
         * 输出：TrieContext 类型返回值。
         */

        public TrieContext toChild(ProductionBodyTrieNode child, GrammarProductionBuilder childDefineBuilder) {
            return new TrieContext(baseName, contextBuilder, counter, child, childDefineBuilder);
        }

        /**
         * 函数功能：处理产生式体前缀树节点。
         * 输入：
         * - 无。
         * 输出：无。
         */


        private void deal() {
            concatenateLast();
            int size = cur.childrenSize();
            if (size == 1) {
                // 单分支?
                singleChild();
            } else if (size > 1) {
                // 多分枝? 就or起来
                multipleChildren();
            }
        }

        /**
         * 函数功能：在最后一个候选式末尾连接语法符号。
         * 输入：
         * - 无。
         * 输出：无。
         */

        private void concatenateLast() {
            GrammarUnitSymbol value = cur.getValue();
            if (value == null) {
                // 根节点
                return;
            }
            if (value.isTerminal()) {
                defineBuilder.concatenateTerminalLast(value.toTerminal().getFactor());
            } else {
                HeadSymbol head = value.toHead();
                if (head.isDefine()) {
                    defineBuilder.concatenateDefinitionLast(head.toDefine().getName());
                } else {
                    // 未预料到的类型构成了Trie
                    throw new IllegalStateException("Unexpected types make up Trie");
                }
            }
        }

        /**
         * 函数功能：处理单子节点的左公因子。
         * 输入：
         * - 无。
         * 输出：无。
         */

        private void singleChild() {
            // 孩子有accept
            ProductionBodyTrieNode child = cur.childrenIterator().next();
            if (cur.accept()) {
                // 唯一的孩子, 自己还accept了
                // 引入epsilon
                String defineName = nextDefineName();
                GrammarProductionBuilder childDefineBuilder = contextBuilder.define(defineName);
                toChild(child, childDefineBuilder).deal();
                childDefineBuilder.alternateEpsilon();
                defineBuilder.concatenateDefinitionLast(defineName);
            } else {
                // 唯一的孩子, 自己没有accept, 说明还能往下
                toChild(child, defineBuilder).deal();
            }

        }

        /**
         * 函数功能：处理多子节点的左公因子。
         * 输入：
         * - 无。
         * 输出：无。
         */

        private void multipleChildren() {
            String defineName = nextDefineName();
            GrammarProductionBuilder childDefineBuilder = contextBuilder.define(defineName);
            Iterator<ProductionBodyTrieNode> iter = cur.childrenIterator();
            while (iter.hasNext()) {
                ProductionBodyTrieNode child = iter.next();
                childDefineBuilder.alternatePlaceholder(); // TODO
                toChild(child, childDefineBuilder).deal();
            }
            defineBuilder.concatenateDefinitionLast(defineName);
        }

        /**
         * 函数功能：生成下一个非终结符定义名称。
         * 输入：
         * - 无。
         * 输出：字符串结果。
         */


        private String nextDefineName() {
            return nameFactory.create(baseName, counter.next());
        }

    }
}

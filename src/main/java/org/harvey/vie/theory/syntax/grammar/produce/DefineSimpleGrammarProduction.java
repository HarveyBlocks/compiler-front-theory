package org.harvey.vie.theory.syntax.grammar.produce;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.harvey.vie.theory.io.Loaders;
import org.harvey.vie.theory.io.Storages;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.BitSet;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 01:00
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class DefineSimpleGrammarProduction implements SimpleGrammarProduction {

    private final HeadDefineSymbol head;
    private final AlterableSymbol body;
    /**
     * 要求升序
     */
    private final SemanticTag[] tags;
/**
 * 函数功能：获取非终结符定义。
 * 输入：
 * - 无。
 * 输出：HeadDefineSymbol 类型返回值。
 */

    public HeadDefineSymbol getDefine() {
        return head;
    }
/**
 * 函数功能：返回当前对象的字符串表示。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    @Override
    public String toString() {
        return head + "->" + body;
    }
/**
 * 函数功能：将对象写入输出流。
 * 输入：
 * - os：OutputStream 类型参数。
 * 输出：整数结果。
 */

    @Override
    public int store(OutputStream os) throws IOException {
        int len = head.store(os);
        if (body.isEpsilon()) {
            len += Storages.storeInteger(os, 0);
        } else {
            GrammarConcatenation concatenation = body.toConcatenation();
            len += storeConcatenation(os, concatenation);
        }
        len += storeTags(os);
        return len;
    }
/**
 * 函数功能：存储语义标签数组。
 * 输入：
 * - os：OutputStream 类型参数。
 * 输出：整数结果。
 */

    private int storeTags(OutputStream os) throws IOException {
        int len = Storages.storeInteger(os, tags.length);
        for (SemanticTag tag : tags) {
            len += tag.store(os);
        }
        return len;
    }
/**
 * 函数功能：存储语法符号连接体。
 * 输入：
 * - os：OutputStream 类型参数。
 * - concatenation：GrammarConcatenation 类型参数。
 * 输出：整数结果。
 */

    private static int storeConcatenation(OutputStream os, GrammarConcatenation concatenation) throws IOException {
        int len = Storages.storeInteger(os, concatenation.size());
        BitSet bitSet = new BitSet(concatenation.size());
        for (int i = 0; i < concatenation.size(); i++) {
            bitSet.set(i, concatenation.get(i).isTerminal());
        }
        len += Storages.storeBitSet(os, bitSet);
        for (GrammarUnitSymbol unitSymbol : concatenation) {
            if (unitSymbol.isTerminal()) {
                TerminalSymbol terminal = unitSymbol.toTerminal();
                len += terminal.store(os);
            } else {
                if (unitSymbol.toHead().isDefine()) {
                    HeadDefineSymbol define = unitSymbol.toHead().toDefine();
                    len += define.store(os);
                } else {
                    throw new IllegalStateException("It is not allowed to store: " + unitSymbol.getClass());
                }
            }
        }
        return len;
    }
/**
 * 函数功能：判断是否包含指定语义标签。
 * 输入：
 * - tag：SemanticTag 类型参数。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean containsTag(SemanticTag tag) {
        return Arrays.binarySearch(this.tags, tag) >= 0;
    }
/**
 * 函数功能：判断语义标签是否匹配。
 * 输入：
 * - expected：SemanticTag... 类型参数。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean matchTags(SemanticTag... expected) {
        if (expected == null || expected.length == 0) {
            return true;
        }
        return Arrays.stream(expected).allMatch(this::containsTag);
    }

    @AllArgsConstructor
    public static class Loader implements SimpleGrammarProduction.Loader<DefineSimpleGrammarProduction> {
        private final HeadDefineSymbol.Loader<?> headLoader;
        private final TerminalSymbol.Loader<?> terminalLoader;
        private final SemanticTag.Loader<?> tagLoader;
/**
 * 函数功能：从输入流加载对象。
 * 输入：
 * - is：InputStream 类型参数。
 * 输出：DefineSimpleGrammarProduction 类型返回值。
 */

        @Override
        public DefineSimpleGrammarProduction load(InputStream is) throws IOException {
            HeadDefineSymbol head = headLoader.load(is);
            int size = Loaders.loadInteger(is);
            AlterableSymbol body =
                    size == 0 ? GrammarSymbol.epsilon() : new GrammarConcatenationImpl(loadConcatenation(is, size));
            SemanticTag[] tags = loadTags(is);
            return new DefineSimpleGrammarProduction(head, body, tags);
        }
/**
 * 函数功能：加载语义标签数组。
 * 输入：
 * - is：InputStream 类型参数。
 * 输出：SemanticTag[] 类型数组。
 */

        private SemanticTag[] loadTags(InputStream is) throws IOException {
            int length = Loaders.loadInteger(is);
            SemanticTag[] result = new SemanticTag[length];
            for (int i = 0; i < length; i++) {
                result[i] = tagLoader.load(is);
            }
            return result;
        }
/**
 * 函数功能：加载语法符号连接体。
 * 输入：
 * - is：InputStream 类型参数。
 * - size：int 类型参数。
 * 输出：GrammarUnitSymbol[] 类型数组。
 */

        private GrammarUnitSymbol[] loadConcatenation(InputStream is, int size) throws IOException {
            BitSet bitSet = Loaders.loadBitSet(is);
            GrammarUnitSymbol[] result = new GrammarUnitSymbol[size];
            for (int i = 0; i < size; i++) {
                boolean terminal = bitSet.get(i);
                result[i] = terminal ? terminalLoader.load(is) : headLoader.load(is);
            }
            return result;
        }
    }
}

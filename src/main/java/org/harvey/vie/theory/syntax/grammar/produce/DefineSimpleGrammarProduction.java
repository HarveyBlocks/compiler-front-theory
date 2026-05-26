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

    public HeadDefineSymbol getDefine() {
        return head;
    }

    @Override
    public String toString() {
        return head + "->" + body;
    }

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

    private int storeTags(OutputStream os) throws IOException {
        int len = Storages.storeInteger(os, tags.length);
        for (SemanticTag tag : tags) {
            len += tag.store(os);
        }
        return len;
    }

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

    @Override
    public boolean containsTag(SemanticTag tag) {
        for (SemanticTag current : tags) {
            if (current == tag) {
                return true;
            }
        }
        return false;
    }

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

        @Override
        public DefineSimpleGrammarProduction load(InputStream is) throws IOException {
            HeadDefineSymbol head = headLoader.load(is);
            int size = Loaders.loadInteger(is);
            AlterableSymbol body =
                    size == 0 ? GrammarSymbol.epsilon() : new GrammarConcatenationImpl(loadConcatenation(is, size));
            SemanticTag[] tags = loadTags(is);
            return new DefineSimpleGrammarProduction(head, body, tags);
        }

        private SemanticTag[] loadTags(InputStream is) throws IOException {
            int length = Loaders.loadInteger(is);
            SemanticTag[] result = new SemanticTag[length];
            for (int i = 0; i < length; i++) {
                result[i] = tagLoader.load(is);
            }
            return result;
        }

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

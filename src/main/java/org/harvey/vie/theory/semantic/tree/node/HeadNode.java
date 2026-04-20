package org.harvey.vie.theory.semantic.tree.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.util.IRandomAccess;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.function.Function;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 01:02
 */
@AllArgsConstructor
@Getter
public class HeadNode implements ShiftReduceSyntaxTreeNode, IRandomAccess<ShiftReduceSyntaxTreeNode> {
    private final HeadSymbol symbol;
    private final ShiftReduceSyntaxTreeNode[] children;

    @Override
    public boolean isHead() {
        return true;
    }

    @Override
    public String toString() {
        return "SPECIAL[`" + symbol + "`]";
    }

    // region child collection
    @Override
    public ShiftReduceSyntaxTreeNode get(int index) {
        return children[index];
    }

    @Override
    public ListIterator<ShiftReduceSyntaxTreeNode> listIterator(int index) {
        return Arrays.asList(children).listIterator(index);
    }

    @Override
    public ListIterator<ShiftReduceSyntaxTreeNode> listIterator() {
        return Arrays.asList(children).listIterator();
    }

    @Override
    public int size() {
        return children.length;
    }

    @Override
    public Iterator<ShiftReduceSyntaxTreeNode> iterator() {
        return Arrays.asList(children).iterator();
    }
    // endregion

    // region hook
    public IdentifierRecord[] getScope(){
        return null;
    }
    // endregion

    public ShiftReduceSyntaxTreeNode instanceBlock(IdentifierRecord[] scope) {
        return new BlockNode(this.symbol, this.children, scope);
    }

    public void set(int i, Function<ShiftReduceSyntaxTreeNode, ShiftReduceSyntaxTreeNode> mapper) {
        children[i] = mapper.apply(children[i]);
    }
}

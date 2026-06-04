package org.harvey.vie.theory.semantic.tree.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;
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
    private final SimpleGrammarProduction production;
    private final ShiftReduceSyntaxTreeNode[] children;

    /**
     * 函数功能：判断节点是否为头节点。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean isHead() {
        return true;
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */


    @Override
    public String toString() {
        return "SPECIAL[`" + symbol + "`]";
    }

    // region child collection

    /**
     * 函数功能：获取指定键或索引对应的对象。
     * 输入：
     * - index：int 类型参数。
     * 输出：ShiftReduceSyntaxTreeNode 类型返回值。
     */
    @Override
    public ShiftReduceSyntaxTreeNode get(int index) {
        return children[index];
    }

    /**
     * 函数功能：获取列表迭代器。
     * 输入：
     * - index：int 类型参数。
     * 输出：ListIterator<ShiftReduceSyntaxTreeNode> 类型集合或迭代结果。
     */

    @Override
    public ListIterator<ShiftReduceSyntaxTreeNode> listIterator(int index) {
        return Arrays.asList(children).listIterator(index);
    }

    /**
     * 函数功能：获取列表迭代器。
     * 输入：
     * - 无。
     * 输出：ListIterator<ShiftReduceSyntaxTreeNode> 类型集合或迭代结果。
     */

    @Override
    public ListIterator<ShiftReduceSyntaxTreeNode> listIterator() {
        return Arrays.asList(children).listIterator();
    }

    /**
     * 函数功能：获取元素数量。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    @Override
    public int size() {
        return children.length;
    }

    /**
     * 函数功能：获取当前对象的迭代器。
     * 输入：
     * - 无。
     * 输出：Iterator<ShiftReduceSyntaxTreeNode> 类型集合或迭代结果。
     */

    @Override
    public Iterator<ShiftReduceSyntaxTreeNode> iterator() {
        return Arrays.asList(children).iterator();
    }
    // endregion

    // region hook

    /**
     * 函数功能：获取作用域编号。
     * 输入：
     * - 无。
     * 输出：IdentifierRecord[] 类型数组。
     */
    public IdentifierRecord[] getScope() {
        return null;
    }
    // endregion

    /**
     * 函数功能：创建代码块头节点。
     * 输入：
     * - scope：IdentifierRecord[] 类型参数。
     * 输出：ShiftReduceSyntaxTreeNode 类型返回值。
     */

    public ShiftReduceSyntaxTreeNode instanceBlock(IdentifierRecord[] scope) {
        return new BlockNode(this.symbol, this.production, this.children, scope);
    }

    /**
     * 函数功能：设置指定位置的对象。
     * 输入：
     * - i：int 类型参数。
     * - mapper：Function<ShiftReduceSyntaxTreeNode, ShiftReduceSyntaxTreeNode> 类型参数。
     * 输出：无。
     */

    public void set(int i, Function<ShiftReduceSyntaxTreeNode, ShiftReduceSyntaxTreeNode> mapper) {
        children[i] = mapper.apply(children[i]);
    }

    /**
     * 函数功能：判断节点语义标签是否匹配。
     * 输入：
     * - expected：SemanticTag... 类型参数。
     * 输出：判断结果布尔值。
     */

    public boolean matchTags(SemanticTag... expected) {
        return production.matchTags(expected);
    }

    /**
     * 函数功能：判断节点是否包含指定语义标签。
     * 输入：
     * - tag：SemanticTag 类型参数。
     * 输出：判断结果布尔值。
     */

    public boolean containsTag(SemanticTag tag) {
        return production.containsTag(tag);
    }

}

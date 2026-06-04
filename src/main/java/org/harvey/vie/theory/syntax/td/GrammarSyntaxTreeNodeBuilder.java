package org.harvey.vie.theory.syntax.td;

import org.harvey.vie.theory.demo.semantic.node.GrammarSyntaxTreeNode;
import org.harvey.vie.theory.demo.semantic.node.HeadNodeImpl;
import org.harvey.vie.theory.demo.semantic.node.TerminalNodeImpl;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 13:06
 */
public class GrammarSyntaxTreeNodeBuilder {
    private final GrammarUnitSymbol symbol;
    /**
     * 倒序
     */
    private final List<GrammarSyntaxTreeNodeBuilder> children = new ArrayList<>();

    private SourceToken token;

    /**
     * 函数功能：创建 GrammarSyntaxTreeNodeBuilder 对象。
     * 输入：
     * - symbol：GrammarUnitSymbol 类型参数。
     * 输出：无。
     */

    public GrammarSyntaxTreeNodeBuilder(GrammarUnitSymbol symbol) {this.symbol = symbol;}

    /**
     * 函数功能：构建并添加子节点。
     * 输入：
     * - child：GrammarUnitSymbol 类型参数。
     * 输出：GrammarSyntaxTreeNodeBuilder 类型返回值。
     */


    public GrammarSyntaxTreeNodeBuilder buildChild(GrammarUnitSymbol child) {
        GrammarSyntaxTreeNodeBuilder childBuilder = new GrammarSyntaxTreeNodeBuilder(child);
        // 倒叙填入
        children.add(childBuilder);
        return childBuilder;
    }

    /**
     * 函数功能：转换为终结符。
     * 输入：
     * - 无。
     * 输出：TerminalSymbol 类型返回值。
     */

    public TerminalSymbol toTerminal() {
        return symbol.toTerminal();
    }

    /**
     * 函数功能：转换为非终结符。
     * 输入：
     * - 无。
     * 输出：HeadSymbol 类型返回值。
     */

    public HeadSymbol toHead() {
        return symbol.toHead();
    }

    /**
     * 函数功能：构建目标对象。
     * 输入：
     * - 无。
     * 输出：GrammarSyntaxTreeNode 类型返回值。
     */

    public GrammarSyntaxTreeNode build() {
        // 可能会导致递归构建这棵树
        // 是否需要shrink? shrink 指
        //         E
        //   T          E'
        // T'  E'    +  T  E'
        // id  ε        id  ε
        // ->
        //         E
        //   id       T
        //          +    id
        // 规则:
        // 1. 所有的孩子都是epsilon的, 父节点标记为epsilon
        // 2. 删除所有为epsilon的孩子
        // 3. 只有一个孩子的, 孩子顶替父亲的位置
        return simplify();
    }

    /**
     * 函数功能：构建简化后的语法树节点。
     * 输入：
     * - 无。
     * 输出：GrammarSyntaxTreeNode 类型返回值。
     */

    private GrammarSyntaxTreeNode simplify() {
        List<GrammarSyntaxTreeNode> validChildren = new ArrayList<>();
        for (int i = children.size() - 1; i >= 0; i--) { // 再次倒序
            GrammarSyntaxTreeNodeBuilder child = children.get(i);
            if (child == null) {
                continue;
            }
            GrammarSyntaxTreeNode simplified = child.simplify();
            if (simplified != null) {
                validChildren.add(simplified);
            }
        }
        // 1. 所有的孩子都是epsilon的, 父节点标记为epsilon
        // 2. 删除所有为epsilon的孩子
        if (!children.isEmpty() && validChildren.isEmpty()) {
            return null;
        }
        // 只有一个孩子的, 孩子顶替父亲的位置
        if (validChildren.size() == 1) {
            return validChildren.get(0);
        }
        if (children.isEmpty()) {
            return buildTerminal();
        }
        GrammarSyntaxTreeNode[] array = validChildren.toArray(GrammarSyntaxTreeNode[]::new);
        return buildHead(array);
    }

    /**
     * 函数功能：构建终结符语法树节点。
     * 输入：
     * - 无。
     * 输出：GrammarSyntaxTreeNode 类型返回值。
     */

    private GrammarSyntaxTreeNode buildTerminal() {
        if (!symbol.isTerminal() || token == null) {
            throw new IllegalStateException("This method can only construct terminator nodes");
        }
        return new TerminalNodeImpl(symbol.toTerminal(), token);
    }

    /**
     * 函数功能：构建非终结符语法树节点。
     * 输入：
     * - children：GrammarSyntaxTreeNode[] 类型参数。
     * 输出：GrammarSyntaxTreeNode 类型返回值。
     */

    private GrammarSyntaxTreeNode buildHead(GrammarSyntaxTreeNode[] children) {
        if (symbol.isTerminal() || token != null) {
            throw new IllegalStateException("Impossible structure, only non-terminal's builder should have children");
        }
        return new HeadNodeImpl(children);
    }

    /**
     * 函数功能：设置子节点为空串节点。
     * 输入：
     * - 无。
     * 输出：无。
     */

    public void setChildEpsilon() {
        children.add(null); // null for epsilon
    }

    /**
     * 函数功能：设置当前节点的词法单元。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：无。
     */

    public void setToken(SourceToken token) {
        this.token = token;
    }

    /**
     * 函数功能：获取当前语法符号。
     * 输入：
     * - 无。
     * 输出：GrammarUnitSymbol 类型返回值。
     */


    public GrammarUnitSymbol getGrammarSymbol() {
        return symbol;
    }
}

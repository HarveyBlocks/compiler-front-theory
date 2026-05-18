package org.harvey.vie.theory.syntax.td;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.demo.semantic.node.GrammarSyntaxTreeNode;
import org.harvey.vie.theory.demo.semantic.node.HeadNodeImpl;
import org.harvey.vie.theory.demo.semantic.node.TerminalNodeImpl;
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

    public GrammarSyntaxTreeNodeBuilder(GrammarUnitSymbol symbol) {this.symbol = symbol;}


    public GrammarSyntaxTreeNodeBuilder buildChild(GrammarUnitSymbol child) {
        GrammarSyntaxTreeNodeBuilder childBuilder = new GrammarSyntaxTreeNodeBuilder(child);
        // 倒叙填入
        children.add(childBuilder);
        return childBuilder;
    }

    public TerminalSymbol toTerminal() {
        return symbol.toTerminal();
    }

    public HeadSymbol toHead() {
        return symbol.toHead();
    }

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

    private GrammarSyntaxTreeNode buildTerminal() {
        if (!symbol.isTerminal() || token == null) {
            throw new IllegalStateException("This method can only construct terminator nodes");
        }
        return new TerminalNodeImpl(symbol.toTerminal(), token);
    }

    private GrammarSyntaxTreeNode buildHead(GrammarSyntaxTreeNode[] children) {
        if (symbol.isTerminal() || token != null) {
            throw new IllegalStateException("Impossible structure, only non-terminal's builder should have children");
        }
        return new HeadNodeImpl(children);
    }

    public void setChildEpsilon() {
        children.add(null); // null for epsilon
    }

    public void setToken(SourceToken token) {
        this.token = token;
    }


    public GrammarUnitSymbol getGrammarSymbol() {
        return symbol;
    }
}

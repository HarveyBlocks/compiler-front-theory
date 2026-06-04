package org.harvey.vie.theory.demo.semantic.callable;

import org.harvey.vie.theory.demo.semantic.node.GrammarSyntaxTreeNode;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.context.PredictiveSemanticContext;
import org.harvey.vie.theory.semantic.context.SemanticResult;
import org.harvey.vie.theory.semantic.callback.td.PredicativeErrorType;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallback;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.td.GrammarSyntaxTreeNodeBuilder;
import org.harvey.vie.theory.syntax.td.conflict.LexicalConflictResolver;
import org.harvey.vie.theory.syntax.td.table.PredictiveParsingTable;

import java.util.Iterator;
import java.util.Stack;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 16:42
 */
public class TreeBuilderPredictiveCallback implements PredictiveCallback {
    private final LexicalConflictResolver lexicalConflictResolver;

    /**
     * 函数功能：创建预测分析语法树构建回调。
     * 输入：
     * - lexicalConflictResolver：用于处理词法冲突的解析器。
     * 输出：无。
     */
    public TreeBuilderPredictiveCallback(LexicalConflictResolver lexicalConflictResolver) {
        this.lexicalConflictResolver = lexicalConflictResolver;
    }


    /**
     * 函数功能：初始化预测分析语法树构建上下文。
     * 输入：
     * - ctx：预测分析语义上下文。
     * 输出：无。
     */
    @Override
    public void onStart(PredictiveSemanticContext ctx) {
        TreeContext treeContext = new TreeContext();
        treeContext.start(ctx.getStart());
        ctx.setResult(treeContext);
        ctx.onStart();
    }

    /**
     * 函数功能：处理终结符匹配并写入对应词法单元。
     * 输入：
     * - ctx：预测分析语义上下文。
     * - terminal：当前匹配的终结符号。
     * 输出：无。
     */
    @Override
    public void onTerminal(PredictiveSemanticContext ctx, TerminalSymbol terminal) {
        SourceToken token = ctx.currentToken();
        ctx.onTerminal(terminal);
        GrammarSyntaxTreeNodeBuilder nodeBuilder = getTreeContext(ctx).popBuilder();
        nodeBuilder.setToken(token);

    }

    /**
     * 函数功能：处理空产生式并写入空子节点。
     * 输入：
     * - ctx：预测分析语义上下文。
     * - head：空产生式所属的非终结符号。
     * 输出：无。
     */
    @Override
    public void onEpsilonProduction(PredictiveSemanticContext ctx, HeadSymbol head) {
        ctx.onEpsilonProduction(head);
        GrammarSyntaxTreeNodeBuilder nodeBuilder = getTreeContext(ctx).popBuilder();
        // 表项产生 X -> ε
        nodeBuilder.setChildEpsilon();
    }

    /**
     * 函数功能：处理普通产生式并创建对应子节点构建器。
     * 输入：
     * - ctx：预测分析语义上下文。
     * - concatenation：当前使用的文法符号串。
     * 输出：无。
     */
    @Override
    public void onProduction(PredictiveSemanticContext ctx, GrammarConcatenation concatenation) {
        GrammarSyntaxTreeNodeBuilder nodeBuilder = getTreeContext(ctx).popBuilder();
        Iterator<GrammarUnitSymbol> iter = concatenation.reverseIterator();
        while (iter.hasNext()) {
            GrammarUnitSymbol next = iter.next();
            GrammarSyntaxTreeNodeBuilder childBuilder = nodeBuilder.buildChild(next);
            getTreeContext(ctx).pushBuilder(childBuilder);
        }
        ctx.onProduction(concatenation);
    }

    /**
     * 函数功能：处理预测分析接受事件并构建最终语法树。
     * 输入：
     * - ctx：预测分析语义上下文。
     * 输出：无。
     */
    @Override
    public void onAccept(PredictiveSemanticContext ctx) {
        ctx.onAccept();
        getTreeContext(ctx).buildTree();
    }

    /**
     * 函数功能：根据预测分析错误类型触发相应的冲突处理。
     * 输入：
     * - ctx：预测分析语义上下文。
     * - errorType：预测分析错误类型。
     * 输出：无。
     */
    @Override
    public void onError(PredictiveSemanticContext ctx, PredicativeErrorType errorType) {
        SourceToken token;
        switch (errorType) {
            case UNDEFINED_PRODUCTION:
                // 未在表中找到合适的"产生式"
                token = ctx.currentToken();
                resolveEmptyProduction(ctx, token);
                break;
            case STACK_UNDERFLOW:
                // Stack 内容不足
                break;
            case TRAILING_INPUT_AFTER_ACCEPT:
                // accept 了, 但是还有输入
                token = ctx.currentToken();
                resolveTrailingInput(token);
                break;
            case INVALID_ACCEPTING_STATE:
                // accept 了, 但是stack还有未处理的状态
                break;
            case TERMINAL_CONFLICT:
                // terminal 冲突
                token = ctx.currentToken();
                resolveTerminalConflict(ctx, token);
                break;
        }
        ctx.onError(errorType);
    }

    /**
     * 函数功能：处理预测分析接受前事件。
     * 输入：
     * - ctx：预测分析语义上下文。
     * 输出：无。
     */
    @Override
    public void beforeAccept(PredictiveSemanticContext ctx) {
        ctx.beforeAccept();
    }

    /**
     * 函数功能：处理接受后仍存在输入的错误。
     * 输入：
     * - token：导致尾随输入错误的源词法单元。
     * 输出：无。
     */
    private void resolveTrailingInput(SourceToken token) {
        try {
            throw new CompileException("Unexpected token at: " + token.hintString());
        } catch (CompileException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 函数功能：处理预测分析中的终结符冲突。
     * 输入：
     * - ctx：预测分析语义上下文。
     * - token：当前冲突的源词法单元。
     * 输出：无。
     */
    private void resolveTerminalConflict(PredictiveSemanticContext ctx, SourceToken token) {
        GrammarSyntaxTreeNodeBuilder nodeBuilder = getTreeContext(ctx).popBuilder();
        // 冲突,是否进行修复?
        boolean success = lexicalConflictResolver.resolveTerminalConflict(token, nodeBuilder, null);
        if (success) {
            return;
        }
        System.err.println("expected: " + nodeBuilder.getGrammarSymbol().toTerminal().hint());
    }

    /**
     * 函数功能：处理预测分析中缺少产生式的冲突。
     * 输入：
     * - ctx：预测分析语义上下文。
     * - token：当前导致冲突的源词法单元。
     * 输出：无。
     */
    private void resolveEmptyProduction(PredictiveSemanticContext ctx, SourceToken token) {
        GrammarSyntaxTreeNodeBuilder nodeBuilder = getTreeContext(ctx).popBuilder();
        boolean success = lexicalConflictResolver.resolveEmptyProduction(token, nodeBuilder, null);
        if (success) {
            return;
        }
        System.err.println("Situations that cannot be found in the phasing table.");
    }

    /**
     * 函数功能：从预测分析语义上下文中取得语法树构建上下文。
     * 输入：
     * - ctx：预测分析语义上下文。
     * 输出：TreeContext 构建上下文。
     */
    private static TreeContext getTreeContext(PredictiveSemanticContext ctx) {
        return (TreeContext) ctx.getResult();
    }


    private static class TreeContext implements SemanticResult {
        public static final TerminalSymbol END_MARK = PredictiveParsingTable.END_MARK_SYMBOL;
        private final GrammarSyntaxTreeNodeBuilder root;
        private final Stack<GrammarSyntaxTreeNodeBuilder> nodeBuilderStack;
        private GrammarSyntaxTreeNode result;

        /**
         * 函数功能：创建语法树构建上下文。
         * 输入：
         * - 无。
         * 输出：无。
         */
        public TreeContext() {
            // --tree--
            this.nodeBuilderStack = new Stack<>();
            this.root = new GrammarSyntaxTreeNodeBuilder(END_MARK);
            nodeBuilderStack.push(root);
        }

        /**
         * 函数功能：以指定开始符号初始化语法树构建栈。
         * 输入：
         * - start：预测分析开始文法符号。
         * 输出：无。
         */
        public void start(GrammarUnitSymbol start) {
            nodeBuilderStack.push(root.buildChild(start));
        }

        /**
         * 函数功能：弹出当前语法树节点构建器。
         * 输入：
         * - 无。
         * 输出：栈顶 GrammarSyntaxTreeNodeBuilder。
         */
        public GrammarSyntaxTreeNodeBuilder popBuilder() {
            return nodeBuilderStack.pop();
        }

        /**
         * 函数功能：压入新的语法树节点构建器。
         * 输入：
         * - builder：待压入的节点构建器。
         * 输出：无。
         */
        public void pushBuilder(GrammarSyntaxTreeNodeBuilder builder) {
            nodeBuilderStack.push(builder);
        }

        /**
         * 函数功能：完成语法树构建并保存结果。
         * 输入：
         * - 无。
         * 输出：无。
         */
        public void buildTree() {
            if (nodeBuilderStack.size() != 1 || nodeBuilderStack.pop().getGrammarSymbol() != END_MARK) {
                throw new IllegalStateException("In the case of impossible, the stack must be empty");
            }
            this.result = root.build();
        }

        /**
         * 函数功能：获取已构建语法树的字符串表示。
         * 输入：
         * - 无。
         * 输出：语法树字符串。
         */
        @Override
        public String toString() {
            return result.toString();
        }
    }
}

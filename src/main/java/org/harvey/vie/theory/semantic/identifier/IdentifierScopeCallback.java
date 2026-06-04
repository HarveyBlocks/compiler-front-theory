package org.harvey.vie.theory.semantic.identifier;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.bu.ReducePredicate;
import org.harvey.vie.theory.semantic.callback.bu.ShiftPredicate;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.function.FunctionBodyState;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.tag.TagReducePredicateFactory;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.tree.node.TreeContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-18 16:25
 */
@AllArgsConstructor
public class IdentifierScopeCallback implements ShiftReduceCallback {
    private final ShiftPredicate scopeIntoPredicate;
    private final ReducePredicate scopeExistPredicate;
    private final ReducePredicate functionBodyBlockPredicate = TagReducePredicateFactory.predicate(
            ProgramSemanticTag.BLOCK,
            ProgramSemanticTag.COMMAND
    );

    /**
     * 函数功能：处理移进事件。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - nextStatus：int 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：无。
     */

    @Override
    public void onShift(ShiftReduceSemanticContext context, int nextStatus, SourceToken token) {
        if (token.getType() == ProgramTokenType.KEYWORD_STRUCT) {
            context.markPendingStructBody();
        }
        // token
        if (scopeIntoPredicate.test(token)) {
            if (context.consumePendingStructBody()) {
                ShiftReduceCallback.super.onShift(context, nextStatus, token);
                return;
            }
            context.scopeIntoBlock();
        }
        ShiftReduceCallback.super.onShift(context, nextStatus, token);
    }

    /**
     * 函数功能：处理规约事件。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：无。
     */

    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        try {
            onReduce0(context, production);
        } catch (CompileException e) {
            // semantic
            throw new RuntimeException(e);
        }
        ShiftReduceCallback.super.onReduce(context, production);
    }

    /**
     * 函数功能：执行规约事件的内部处理。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：无。
     */

    private void onReduce0(ShiftReduceSemanticContext context, SimpleGrammarProduction production)
            throws CompileException {
        // 是需要从符号表中查询
        if (!scopeExistPredicate.test(production)) {
            return;
        }
        TreeContext treeContext = context.getTreeContext();
        if (treeContext.isEmpty() || !treeContext.peek().isHead()) {
            return;
        }
        IdentifierRecord[] scope = context.scopeExistBlock();
        if (functionBodyBlockPredicate.test(production)
            && context.isCurrentBlockFunctionBody()) {
            FunctionBodyState bodyState = context.currentFunctionBodyState();
            if (bodyState != null
                && !context.getFunctionManager().guaranteesReturn(context, treeContext.peek())
                && !bodyState.getFunction().getSignature().getReturnType().isVoidScalar()) {
                throw new CompileException("non-void function must return a value.");
            }
        }
        context.finishBlockScope();
        treeContext.resetTop(top -> {
            ShiftReduceSyntaxTreeNode replaced = top.toHead().instanceBlock(scope);
            context.moveTypeBinding(top, replaced);
            return replaced;
        });
    }

}

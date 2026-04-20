package org.harvey.vie.theory.semantic.identifier;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.bu.ReducePredicate;
import org.harvey.vie.theory.semantic.callback.bu.ShiftPredicate;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
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

    @Override
    public void onShift(ShiftReduceSemanticContext context, int nextStatus, SourceToken token) {
        // token
        if (scopeIntoPredicate.test(token)) {
            context.scopeInto();
        }
        ShiftReduceCallback.super.onShift(context, nextStatus, token);
    }

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
        IdentifierRecord[] scope = context.scopeExist();
        treeContext.resetTop(top->top.toHead().instanceBlock(scope));
    }

}

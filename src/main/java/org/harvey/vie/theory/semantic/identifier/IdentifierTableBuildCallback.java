package org.harvey.vie.theory.semantic.identifier;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.bu.ReducePredicate;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.TreeContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-18 14:38
 */
@AllArgsConstructor
public class IdentifierTableBuildCallback implements ShiftReduceCallback {
    private final ReducePredicate usingPredicate;
    private final ReducePredicate declaringPredicate ;
    private final UsingIdentifierSupplier usingIdentifierSupplier;
    private final DeclarationRecordSupplier declarationRecordSupplier;

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
        TreeContext treeContext = context.getTreeContext();
        if (treeContext.isEmpty() || !treeContext.peek().isHead()) {
            return;
        }
        HeadNode headNode = treeContext.peek().toHead();
        // 是需要从符号表中查询
        if (usingPredicate.test(production)) {
            existIdentifier(context, headNode);
        } else if (declaringPredicate.test(production)) {
            registerIdentifier(context, headNode);
        }
    }

    private void existIdentifier(ShiftReduceSemanticContext context, HeadNode headNode) throws CompileException {
        SourceToken identifierToken = usingIdentifierSupplier.identifier(headNode);
        IdentifierRecord record = context.getIdentifier(identifierToken);
        if (record == null) {
            context.addError(identifierToken.getOffset(), "TODO");
            throw new CompileException("TODO");
        }
        int no = record.getNo();
        headNode.set(0, n -> n.toToken().instanceIdentifier(no));
    }

    private void registerIdentifier(ShiftReduceSemanticContext context, HeadNode headNode)
            throws CompileException {
        // 是需要注册到符号表
        SourceToken identifierToken = declarationRecordSupplier.identifier(headNode);
        boolean existedIdentifier = context.existIdentifier(identifierToken);
        if (existedIdentifier) {
            context.addError(identifierToken.getOffset(), "TODO");
            throw new CompileException("TODO");
        }
        HeadNode typeHeadNode = declarationRecordSupplier.typeHeadNode(headNode);
        boolean initialized = declarationRecordSupplier.initialized(headNode);
        context.registerIdentifier(typeHeadNode, identifierToken, initialized);
    }

    @FunctionalInterface
    public interface UsingIdentifierSupplier {
        SourceToken identifier(HeadNode usingIdentifierReducedNode);
    }

    public interface DeclarationRecordSupplier {
        SourceToken identifier(HeadNode declarationReducedNode);

        boolean initialized(HeadNode declarationReducedNode);

        HeadNode typeHeadNode(HeadNode declarationReducedNode);
    }
}

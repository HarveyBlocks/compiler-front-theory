package org.harvey.vie.theory.semantic.value;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.bu.ReducePredicate;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tag.TagReducePredicateFactory;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * Keeps identifier constant state conservative.
 *
 * <p>Declarations may introduce constant values, but any ordinary assignment invalidates the
 * variable's propagated compile-time constant, because path-sensitive reassignment is not tracked yet.</p>
 *
 * @author Temper
 */
public class IdentifierConstantStateCallback implements ShiftReduceCallback {
    private final ReducePredicate assignmentPredicate =
            TagReducePredicateFactory.predicate(ProgramSemanticTag.ASSIGNMENT);

    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        if (assignmentPredicate.test(production)) {
            invalidateAssignedIdentifier(context);
        }
        ShiftReduceCallback.super.onReduce(context, production);
    }

    private void invalidateAssignedIdentifier(ShiftReduceSemanticContext context) {
        HeadNode head = currentReducedHead(context);
        SourceToken identifier = simpleIdentifier(head.get(0));
        if (identifier != null) {
            context.updateIdentifierConstant(identifier, null);
        }
    }

    private SourceToken simpleIdentifier(ShiftReduceSyntaxTreeNode locNode) {
        if (!locNode.isHead()) {
            return null;
        }
        HeadNode head = locNode.toHead();
        if (!head.matchTags(ProgramSemanticTag.IDENTIFIER, ProgramSemanticTag.USE)) {
            return null;
        }
        ShiftReduceSyntaxTreeNode tokenNode = head.get(0);
        if (!tokenNode.isToken()) {
            throw new IllegalStateException("identifier use production does not start with token.");
        }
        return tokenNode.toToken().getSource();
    }

    private HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new IllegalStateException("current reduced head is not available");
        }
        return context.getTreeContext().peek().toHead();
    }
}


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
 * 在赋值语句归约后维护标识符的常量传播状态。
 * <p>
 * 作用：
 * <p>
 * IdentifierConstantStateCallback 负责处理一个很关键的常量传播规则：
 * <p>
 * 如果某个变量被重新赋值，那么它之前在符号表中保存的编译期常量值就不再可靠，
 * 必须清除。
 * <p>
 * 例如：
 * <p>
 * int32 a = 1;
 * b = a + 2;      // 此时 a 可以作为常量 1 传播
 * a = input;      // 此处之后 a 的常量状态应失效
 * <p>
 * 这保证后续中间代码生成不会错误地把已经被修改过的变量继续当作常量使用。
 */
public class IdentifierConstantStateCallback implements ShiftReduceCallback {
    /**
     * 判断当前归约产生式是否是赋值语句或赋值表达式。
     * <p>
     * 注意：
     * <p>
     * 该 predicate 只根据 ProgramSemanticTag.ASSIGNMENT 匹配，
     * 具体产生式结构由语法定义和 tag 标注决定。
     */
    private final ReducePredicate assignmentPredicate =
            TagReducePredicateFactory.predicate(ProgramSemanticTag.ASSIGNMENT);

    /**
     * 函数功能：处理规约事件。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：无。
     */
    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        if (assignmentPredicate.test(production)) {
            invalidateAssignedIdentifier(context);
        }
        ShiftReduceCallback.super.onReduce(context, production);
    }

    /**
     * 函数功能：使被赋值标识符的常量状态失效。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * 输出：无。
     */
    private void invalidateAssignedIdentifier(ShiftReduceSemanticContext context) {
        HeadNode head = currentReducedHead(context);
        SourceToken identifier = simpleIdentifier(head.get(0));
        if (identifier != null) {
            context.updateIdentifierConstant(identifier, null);
        }
    }

    /**
     * 函数功能：获取简单标识符词法单元。
     * 输入：
     * - locNode：ShiftReduceSyntaxTreeNode 类型参数。
     * 输出：SourceToken 类型返回值。
     */
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

    /**
     * 函数功能：获取当前规约头节点。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * 输出：HeadNode 类型返回值。
     */
    private HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new IllegalStateException("current reduced head is not available");
        }
        return context.getTreeContext().peek().toHead();
    }
}


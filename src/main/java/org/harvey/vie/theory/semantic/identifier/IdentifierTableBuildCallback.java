package org.harvey.vie.theory.semantic.identifier;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.callback.bu.ReducePredicate;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.type.TypeRegister;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.TreeContext;
import org.harvey.vie.theory.semantic.value.ConstantValue;
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
/**
 * 函数功能：判断标识符是否存在。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - headNode：HeadNode 类型参数。
 * 输出：无。
 */

    private void existIdentifier(ShiftReduceSemanticContext context, HeadNode headNode) throws CompileException {
        SourceToken identifierToken = usingIdentifierSupplier.identifier(headNode);
        IdentifierRecord record = context.getIdentifier(identifierToken);
        if (record == null) {
            context.addError(identifierToken.getOffset(), "identifier is not declared in current visible scopes.");
            throw new CompileException("identifier is not declared in current visible scopes.");
        }
    }
/**
 * 函数功能：注册标识符记录。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - headNode：HeadNode 类型参数。
 * 输出：无。
 */

    private void registerIdentifier(ShiftReduceSemanticContext context, HeadNode headNode)
            throws CompileException {
        // 是需要注册到符号表
        SourceToken identifierToken = declarationRecordSupplier.identifier(headNode);
        boolean existedIdentifier = context.existIdentifier(identifierToken);
        if (existedIdentifier) {
            context.addError(identifierToken.getOffset(), "duplicate identifier declaration is not allowed.");
            throw new CompileException("duplicate identifier declaration is not allowed.");
        }
        HeadNode typeHeadNode = declarationRecordSupplier.typeHeadNode(headNode);
        TypeRegister typeRegister = context.getType(typeHeadNode);
        if (typeRegister == null) {
            throw new CompileException("declared type is missing on type node.");
        }
        SemanticType declaredType = typeRegister
                .requireType("declared type is required for identifier declaration.");
        boolean initialized = declarationRecordSupplier.initialized(headNode);
        ConstantValue constantValue = declarationRecordSupplier.initializerValue(context, headNode);
        context.registerIdentifier(typeHeadNode, declaredType, identifierToken, initialized, constantValue);
    }

    @FunctionalInterface
    public interface UsingIdentifierSupplier {
        /**
         * 函数功能：获取标识符词法单元。
         * 输入：
         * - usingIdentifierReducedNode：HeadNode 类型参数。
         * 输出：SourceToken 类型返回值。
         */
        SourceToken identifier(HeadNode usingIdentifierReducedNode);
    }

    public interface DeclarationRecordSupplier {
        /**
         * 函数功能：获取标识符词法单元。
         * 输入：
         * - declarationReducedNode：HeadNode 类型参数。
         * 输出：SourceToken 类型返回值。
         */
        SourceToken identifier(HeadNode declarationReducedNode);
/**
 * 函数功能：判断声明是否带初始化。
 * 输入：
 * - declarationReducedNode：HeadNode 类型参数。
 * 输出：判断结果布尔值。
 */

        boolean initialized(HeadNode declarationReducedNode);
/**
 * 函数功能：获取类型头节点。
 * 输入：
 * - declarationReducedNode：HeadNode 类型参数。
 * 输出：HeadNode 类型返回值。
 */

        HeadNode typeHeadNode(HeadNode declarationReducedNode);
/**
 * 函数功能：获取初始化常量值。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - declarationReducedNode：HeadNode 类型参数。
 * 输出：ConstantValue 类型返回值。
 */

        default ConstantValue initializerValue(ShiftReduceSemanticContext context, HeadNode declarationReducedNode) {
            return null;
        }
    }

}

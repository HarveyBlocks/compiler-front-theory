package org.harvey.vie.theory.semantic.array;

import lombok.Getter;
import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.TypeRegister;

import java.util.ArrayDeque;

public final class ArrayCreationDimensions {
    /**
     * 函数功能：创建 ArrayCreationDimensions 对象。
     * 输入：
     * - 无。
     * 输出：无。
     */
    private ArrayCreationDimensions() {
    }
/**
 * 函数功能：汇总数组创建维度信息。
 * 输入：
 * - node：ShiftReduceSyntaxTreeNode 类型参数。
 * 输出：Summary 类型返回值。
 */

    public static Summary summarize(ShiftReduceSyntaxTreeNode node) {
        Summary summary = new Summary();
        visitDimensions(node, dimension -> {
            summary.totalDimensions++;
            if (dimension.containsTag(ProgramSemanticTag.VALUE)) {
                if (summary.trailingOmittedDimensions > 0) {
                    throw new CompilerException("array creation dimensions with length must precede omitted dimensions.");
                }
                summary.specifiedDimensions++;
            } else {
                summary.trailingOmittedDimensions++;
            }
        });
        if (summary.specifiedDimensions <= 0) {
            throw new CompilerException("array creation requires at least one specified length.");
        }
        return summary;
    }
/**
 * 函数功能：汇总并校验数组创建维度信息。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - node：ShiftReduceSyntaxTreeNode 类型参数。
 * 输出：Summary 类型返回值。
 */

    public static Summary summarizeAndValidate(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        Summary summary = new Summary();
        visitDimensions(node, dimension -> {
            summary.totalDimensions++;
            if (dimension.containsTag(ProgramSemanticTag.VALUE)) {
                if (summary.trailingOmittedDimensions > 0) {
                    SourceToken token = ShiftReduceSyntaxTreeNode.anchor(dimension);
                    SemanticDiagnostics.reject(
                            context,
                            token,
                            "array creation dimensions with length must precede omitted dimensions."
                    );
                }
                TypeRegister register = context.getType(dimension.get(1));
                if (register == null) {
                    throw new CompilerException("array length expression type is missing.");
                }
                SemanticType type = register.requireType("array length expression type is required.");
                if (!SemanticType.scalar(SemanticType.Kind.INT32).equals(type)) {
                    SemanticDiagnostics.reject(
                            context,
                            ShiftReduceSyntaxTreeNode.anchor(dimension.get(1)),
                            "array length expression must be int32."
                    );
                }
                summary.specifiedDimensions++;
            } else {
                summary.trailingOmittedDimensions++;
            }
        });
        if (summary.specifiedDimensions <= 0) {
            SourceToken token = ShiftReduceSyntaxTreeNode.anchor(node);
            SemanticDiagnostics.reject(context, token, "array creation requires at least one specified length.");
        }
        return summary;
    }
/**
 * 函数功能：遍历数组创建维度节点。
 * 输入：
 * - node：ShiftReduceSyntaxTreeNode 类型参数。
 * - consumer：java.util.function.Consumer<HeadNode> 类型参数。
 * 输出：无。
 */

    private static void visitDimensions(ShiftReduceSyntaxTreeNode node, java.util.function.Consumer<HeadNode> consumer) {
        if (node == null || !node.isHead()) {
            return;
        }
        ArrayDeque<HeadNode> stack = new ArrayDeque<>();
        HeadNode cursor = node.toHead();
        while (true) {
            if (cursor.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.ARRAY_CREATION_DIM, ProgramSemanticTag.SEQUENCE)) {
                stack.push(cursor.get(1).toHead());
                cursor = cursor.get(0).toHead();
                continue;
            }
            if (cursor.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.ARRAY_CREATION_DIM, ProgramSemanticTag.FORWARD)) {
                stack.push(cursor.get(0).toHead());
                break;
            }
            if (cursor.containsTag(ProgramSemanticTag.ARRAY_CREATION_DIM)) {
                stack.push(cursor);
            }
            break;
        }
        while (!stack.isEmpty()) {
            consumer.accept(stack.pop());
        }
    }

    @Getter
    public static final class Summary {
        private int totalDimensions;
        private int specifiedDimensions;
        private int trailingOmittedDimensions;
    }
}

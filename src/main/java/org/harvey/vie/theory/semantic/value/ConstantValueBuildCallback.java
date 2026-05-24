package org.harvey.vie.theory.semantic.value;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tag.ProductionTagStrategy;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.nio.charset.StandardCharsets;

public class ConstantValueBuildCallback implements ShiftReduceCallback {
    private static final ProductionTagStrategy<ConstantRule> RULES = new ProductionTagStrategy<>(ConstantRule.NONE)
            .when(ConstantRule.NONE, ProgramSemanticTag.PROGRAM)
            .when(ConstantRule.NONE, ProgramSemanticTag.NOOP)
            .when(ConstantRule.NONE, ProgramSemanticTag.DECLARATION)
            .when(ConstantRule.NONE, ProgramSemanticTag.RETURN)
            .when(ConstantRule.NONE, ProgramSemanticTag.LOOP)
            .when(ConstantRule.NONE, ProgramSemanticTag.CONDITIONAL)
            .when(ConstantRule.NONE, ProgramSemanticTag.BLOCK, ProgramSemanticTag.COMMAND)
            .when(ConstantRule.NONE, ProgramSemanticTag.BLOCK, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
            .when(ConstantRule.NONE, ProgramSemanticTag.BLOCK, ProgramSemanticTag.LIST, ProgramSemanticTag.SEQUENCE)
            .when(ConstantRule.NONE, ProgramSemanticTag.PARAMETER)
            .when(ConstantRule.NONE, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.HEAD)
            .when(ConstantRule.NONE, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.CALL)
            .when(ConstantRule.NONE, ProgramSemanticTag.ACCESS)
            .when(ConstantRule.NONE, ProgramSemanticTag.ASSIGNMENT)
            .when(ConstantRule.NONE, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
            .when(ConstantRule.NONE, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.LIST, ProgramSemanticTag.SEQUENCE)
            .when(ConstantRule.FORWARD, ProgramSemanticTag.FORWARD)
            .when(ConstantRule.IDENTIFIER, ProgramSemanticTag.IDENTIFIER, ProgramSemanticTag.USE)
            .when(ConstantRule.LEFT_VALUE, ProgramSemanticTag.LEFT_VALUE)
            .when(ConstantRule.LITERAL, ProgramSemanticTag.LITERAL)
            .when(ConstantRule.PARENTHESIZED, ProgramSemanticTag.PARENTHESIZED)
            .when(ConstantRule.UNARY_MINUS, ProgramSemanticTag.NEGATE)
            .when(ConstantRule.UNARY_NOT, ProgramSemanticTag.LOGICAL_NOT)
            .when(ConstantRule.ARITHMETIC, ProgramSemanticTag.PLUS)
            .when(ConstantRule.ARITHMETIC, ProgramSemanticTag.MINUS)
            .when(ConstantRule.ARITHMETIC, ProgramSemanticTag.MULTIPLY)
            .when(ConstantRule.ARITHMETIC, ProgramSemanticTag.DIVIDE)
            .when(ConstantRule.LOGICAL, ProgramSemanticTag.OR)
            .when(ConstantRule.LOGICAL, ProgramSemanticTag.AND)
            .when(ConstantRule.EQUALITY, ProgramSemanticTag.EQUAL)
            .when(ConstantRule.EQUALITY, ProgramSemanticTag.NOT_EQUAL)
            .when(ConstantRule.RELATION, ProgramSemanticTag.LESS)
            .when(ConstantRule.RELATION, ProgramSemanticTag.LESS_EQUAL)
            .when(ConstantRule.RELATION, ProgramSemanticTag.GREATER)
            .when(ConstantRule.RELATION, ProgramSemanticTag.GREATER_EQUAL)
            .when(ConstantRule.ARGUMENT_VALUE, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.VALUE, ProgramSemanticTag.FORWARD);

    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        HeadNode head = currentReducedHead(context);
        ConstantValue value = RULES.resolve(production).build(context, head);
        if (value != null) {
            context.bindConstantValue(head, value);
        }
        ShiftReduceCallback.super.onReduce(context, production);
    }

    private static ConstantValue forward(ShiftReduceSemanticContext context, HeadNode head) {
        for (int i = 0; i < head.size(); i++) {
            ConstantValue child = child(context, head, i);
            if (child != null || context.hasConstantValue(head.get(i))) {
                return child;
            }
        }
        return null;
    }

    private static ConstantValue literal(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = head.get(0).toToken().getSource();
        SemanticType type = context.literalType(token);
        String lexeme = new String(token.getLexeme(), StandardCharsets.UTF_8);
        switch (type.getKind()) {
            case BOOLEAN:
                return new ConstantValue(type, Boolean.parseBoolean(lexeme));
            case INT32:
                return new ConstantValue(type, Integer.parseInt(lexeme));
            case FLOAT64:
                return new ConstantValue(type, Double.parseDouble(lexeme));
            default:
                return null;
        }
    }

    private static ConstantValue unaryMinus(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue operand = child(context, head, 1);
        if (operand == null || !operand.getType().isNumericScalar()) {
            return null;
        }
        if (operand.getType().getKind() == SemanticType.Kind.FLOAT64) {
            return new ConstantValue(operand.getType(), -operand.float64());
        }
        return new ConstantValue(operand.getType(), -operand.int32());
    }

    private static ConstantValue unaryNot(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue operand = child(context, head, 1);
        if (operand == null || !operand.getType().isBooleanScalar()) {
            return null;
        }
        return new ConstantValue(operand.getType(), !operand.bool());
    }

    private static ConstantValue arithmetic(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue left = child(context, head, 0);
        ConstantValue right = child(context, head, 2);
        if (left == null || right == null || !left.getType().isNumericScalar() || !right.getType().isNumericScalar()) {
            return null;
        }
        SemanticType resultType = context.commonBinaryType(left.getType(), right.getType());
        double leftValue = left.getType().getKind() == SemanticType.Kind.FLOAT64 ? left.float64() : left.int32();
        double rightValue = right.getType().getKind() == SemanticType.Kind.FLOAT64 ? right.float64() : right.int32();
        if (head.containsTag(ProgramSemanticTag.PLUS)) {
            return numericResult(resultType, leftValue + rightValue);
        }
        if (head.containsTag(ProgramSemanticTag.MINUS)) {
            return numericResult(resultType, leftValue - rightValue);
        }
        if (head.containsTag(ProgramSemanticTag.MULTIPLY)) {
            return numericResult(resultType, leftValue * rightValue);
        }
        if (head.containsTag(ProgramSemanticTag.DIVIDE)) {
            return numericResult(resultType, leftValue / rightValue);
        }
        throw new CompilerException("unsupported arithmetic operator.");
    }

    private static ConstantValue logical(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue left = child(context, head, 0);
        ConstantValue right = child(context, head, 2);
        if (left == null || right == null || !left.getType().isBooleanScalar() || !right.getType().isBooleanScalar()) {
            return null;
        }
        boolean result = head.containsTag(ProgramSemanticTag.OR) ? left.bool() || right.bool() : left.bool() && right.bool();
        return new ConstantValue(SemanticType.scalar(SemanticType.Kind.BOOLEAN), result);
    }

    private static ConstantValue equality(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue left = child(context, head, 0);
        ConstantValue right = child(context, head, 2);
        if (left == null || right == null) {
            return null;
        }
        boolean result;
        if (left.getType().isNumericScalar() && right.getType().isNumericScalar()) {
            double leftValue = left.getType().getKind() == SemanticType.Kind.FLOAT64 ? left.float64() : left.int32();
            double rightValue = right.getType().getKind() == SemanticType.Kind.FLOAT64 ? right.float64() : right.int32();
            result = leftValue == rightValue;
        } else if (left.getType().equals(right.getType())) {
            result = left.getValue().equals(right.getValue());
        } else {
            return null;
        }
        return new ConstantValue(
                SemanticType.scalar(SemanticType.Kind.BOOLEAN),
                head.containsTag(ProgramSemanticTag.EQUAL) == result
        );
    }

    private static ConstantValue relation(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue left = child(context, head, 0);
        ConstantValue right = child(context, head, 2);
        if (left == null || right == null || !left.getType().isNumericScalar() || !right.getType().isNumericScalar()) {
            return null;
        }
        double leftValue = left.getType().getKind() == SemanticType.Kind.FLOAT64 ? left.float64() : left.int32();
        double rightValue = right.getType().getKind() == SemanticType.Kind.FLOAT64 ? right.float64() : right.int32();
        boolean result;
        if (head.containsTag(ProgramSemanticTag.LESS)) {
            result = leftValue < rightValue;
        } else if (head.containsTag(ProgramSemanticTag.LESS_EQUAL)) {
            result = leftValue <= rightValue;
        } else if (head.containsTag(ProgramSemanticTag.GREATER)) {
            result = leftValue > rightValue;
        } else if (head.containsTag(ProgramSemanticTag.GREATER_EQUAL)) {
            result = leftValue >= rightValue;
        } else {
            throw new CompilerException("unsupported relational operator.");
        }
        return new ConstantValue(SemanticType.scalar(SemanticType.Kind.BOOLEAN), result);
    }

    private static ConstantValue numericResult(SemanticType type, double value) {
        if (type.getKind() == SemanticType.Kind.FLOAT64) {
            return new ConstantValue(type, value);
        }
        return new ConstantValue(type, (int) value);
    }

    private static ConstantValue identifierConstant(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = head.get(0).toToken().getSource();
        var record = context.getIdentifier(token);
        return record == null ? null : record.getConstantValue();
    }

    private static ConstantValue child(ShiftReduceSemanticContext context, HeadNode head, int index) {
        return context.getConstantValue(head.get(index));
    }

    private static HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new IllegalStateException("current reduced head is not available");
        }
        return context.getTreeContext().peek().toHead();
    }

    @FunctionalInterface
    private interface ConstantRule {
        ConstantRule NONE = (context, head) -> null;
        ConstantRule FORWARD = ConstantValueBuildCallback::forward;
        ConstantRule IDENTIFIER = ConstantValueBuildCallback::identifierConstant;
        ConstantRule LEFT_VALUE = (context, head) -> child(context, head, 0);
        ConstantRule LITERAL = ConstantValueBuildCallback::literal;
        ConstantRule PARENTHESIZED = (context, head) -> child(context, head, 1);
        ConstantRule UNARY_MINUS = ConstantValueBuildCallback::unaryMinus;
        ConstantRule UNARY_NOT = ConstantValueBuildCallback::unaryNot;
        ConstantRule ARITHMETIC = ConstantValueBuildCallback::arithmetic;
        ConstantRule LOGICAL = ConstantValueBuildCallback::logical;
        ConstantRule EQUALITY = ConstantValueBuildCallback::equality;
        ConstantRule RELATION = ConstantValueBuildCallback::relation;
        ConstantRule ARGUMENT_VALUE = ConstantValueBuildCallback::forward;

        ConstantValue build(ShiftReduceSemanticContext context, HeadNode head);
    }
}

package org.harvey.vie.theory.semantic.value;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.nio.charset.StandardCharsets;

public class ConstantValueBuildCallback implements ShiftReduceCallback {
    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        HeadNode head = currentReducedHead(context);
        ConstantValue value = build(context, normalizeKey(production.toString().trim()), head);
        if (value != null) {
            context.bindConstantValue(head, value);
        }
        ShiftReduceCallback.super.onReduce(context, production);
    }

    private ConstantValue build(ShiftReduceSemanticContext context, String key, HeadNode head) {
        if (isEpsilonOf(key, "param_list", "arg_list", "block_items")) {
            return null;
        }
        switch (key) {
            case "program->top_item":
            case "program->top_item program":
            case "program->block":
            case "top_item->function_decl":
            case "top_item->block_item":
            case "function_decl->function_head block":
            case "function_head->type IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE":
            case "function_head->TYPE_VOID IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE":
            case "param_list->params":
            case "params->param":
            case "params->params OPERATOR_COMMA param":
            case "param->type IDENTIFIER":
            case "arg_list->args":
            case "args->bool":
            case "args->args OPERATOR_COMMA bool":
            case "call_expr->IDENTIFIER OPERATOR_PARENTHESIS_OPEN arg_list OPERATOR_PARENTHESIS_CLOSE":
            case "expr_stmt->expr OPERATOR_SEMICOLON":
            case "matched_stmt->assign_stmt":
            case "matched_stmt->matched_while_stmt":
            case "matched_stmt->do_while_stmt":
            case "matched_stmt->expr_stmt":
            case "matched_stmt->block":
            case "matched_stmt->empty_stmt":
            case "matched_stmt->break_stmt":
            case "matched_stmt->continue_stmt":
            case "matched_stmt->matched_if_stmt":
            case "matched_stmt->return_stmt":
                return null;
            case "block_items->block_item":
            case "block_item->decl":
            case "block_item->stmt":
            case "stmt->matched_stmt":
            case "stmt->unmatched_stmt":
            case "unmatched_stmt->unmatched_if_stmt":
            case "unmatched_stmt->unmatched_while_stmt":
            case "block_items->block_items block_item":
            case "bool->join":
            case "join->equality":
            case "equality->rel":
            case "rel->expr":
            case "expr->term":
            case "term->unary":
            case "unary->factor":
            case "factor->loc":
                return child(context, head, 0);
            case "factor->OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE":
                return child(context, head, 1);
            case "loc->IDENTIFIER":
                return identifierConstant(context, head);
            case "loc->loc OPERATOR_SQUARE_OPEN bool OPERATOR_SQUARE_CLOSE":
                return null;
            case "factor->CONSTANT_INTEGER":
            case "factor->CONSTANT_FLOAT":
            case "factor->CONSTANT_BOOLEAN_TRUE":
            case "factor->CONSTANT_BOOLEAN_FALSE":
                return literal(context, head.get(0).toToken().getSource());
            case "unary->OPERATOR_MINUS unary":
                return unaryMinus(context, head);
            case "unary->OPERATOR_LOGICAL_NOT unary":
                return unaryNot(context, head);
            case "expr->expr OPERATOR_PLUS term":
                return arithmetic(context, head, "plus");
            case "expr->expr OPERATOR_MINUS term":
                return arithmetic(context, head, "minus");
            case "term->term OPERATOR_MULTIPLY unary":
                return arithmetic(context, head, "multiply");
            case "term->term OPERATOR_DIVIDE unary":
                return arithmetic(context, head, "divide");
            case "bool->bool OPERATOR_LOGICAL_OR join":
                return logical(context, head, "or");
            case "join->join OPERATOR_LOGICAL_AND equality":
                return logical(context, head, "and");
            case "equality->equality OPERATOR_EQUAL rel":
                return equality(context, head, true);
            case "equality->equality OPERATOR_NOT_EQUAL rel":
                return equality(context, head, false);
            case "rel->expr OPERATOR_LESS expr":
                return relation(context, head, "less");
            case "rel->expr OPERATOR_LESS_EQUAL expr":
                return relation(context, head, "less_equal");
            case "rel->expr OPERATOR_GREATER expr":
                return relation(context, head, "greater");
            case "rel->expr OPERATOR_GREATER_EQUAL expr":
                return relation(context, head, "greater_equal");
            default:
                return null;
        }
    }

    private String normalizeKey(String key) {
        return key.replace("return_type", "type");
    }

    private boolean isEpsilonOf(String key, String... heads) {
        int index = key.indexOf("->");
        if (index < 0) {
            return false;
        }
        String head = key.substring(0, index);
        boolean matchesHead = false;
        for (String candidate : heads) {
            if (candidate.equals(head)) {
                matchesHead = true;
                break;
            }
        }
        if (!matchesHead) {
            return false;
        }
        String body = key.substring(index + 2).trim();
        return body.isEmpty() || "蔚".equals(body) || "ε".equals(body);
    }

    private ConstantValue literal(ShiftReduceSemanticContext context, SourceToken token) {
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

    private ConstantValue unaryMinus(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue operand = child(context, head, 1);
        if (operand == null || !operand.getType().isNumericScalar()) {
            return null;
        }
        if (operand.getType().getKind() == SemanticType.Kind.FLOAT64) {
            return new ConstantValue(operand.getType(), -operand.float64());
        }
        return new ConstantValue(operand.getType(), -operand.int32());
    }

    private ConstantValue unaryNot(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue operand = child(context, head, 1);
        if (operand == null || !operand.getType().isBooleanScalar()) {
            return null;
        }
        return new ConstantValue(operand.getType(), !operand.bool());
    }

    private ConstantValue arithmetic(ShiftReduceSemanticContext context, HeadNode head, String operator) {
        ConstantValue left = child(context, head, 0);
        ConstantValue right = child(context, head, 2);
        if (left == null || right == null || !left.getType().isNumericScalar() || !right.getType().isNumericScalar()) {
            return null;
        }
        SemanticType resultType = context.commonBinaryType(left.getType(), right.getType());
        double leftValue = left.getType().getKind() == SemanticType.Kind.FLOAT64 ? left.float64() : left.int32();
        double rightValue = right.getType().getKind() == SemanticType.Kind.FLOAT64 ? right.float64() : right.int32();
        switch (operator) {
            case "plus":
                return numericResult(resultType, leftValue + rightValue);
            case "minus":
                return numericResult(resultType, leftValue - rightValue);
            case "multiply":
                return numericResult(resultType, leftValue * rightValue);
            case "divide":
                return numericResult(resultType, leftValue / rightValue);
            default:
                throw new CompilerException("unsupported arithmetic operator: " + operator);
        }
    }

    private ConstantValue logical(ShiftReduceSemanticContext context, HeadNode head, String operator) {
        ConstantValue left = child(context, head, 0);
        ConstantValue right = child(context, head, 2);
        if (left == null || right == null || !left.getType().isBooleanScalar() || !right.getType().isBooleanScalar()) {
            return null;
        }
        boolean result = "or".equals(operator) ? left.bool() || right.bool() : left.bool() && right.bool();
        return new ConstantValue(SemanticType.scalar(SemanticType.Kind.BOOLEAN), result);
    }

    private ConstantValue equality(ShiftReduceSemanticContext context, HeadNode head, boolean equal) {
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
        return new ConstantValue(SemanticType.scalar(SemanticType.Kind.BOOLEAN), equal == result);
    }

    private ConstantValue relation(ShiftReduceSemanticContext context, HeadNode head, String operator) {
        ConstantValue left = child(context, head, 0);
        ConstantValue right = child(context, head, 2);
        if (left == null || right == null || !left.getType().isNumericScalar() || !right.getType().isNumericScalar()) {
            return null;
        }
        double leftValue = left.getType().getKind() == SemanticType.Kind.FLOAT64 ? left.float64() : left.int32();
        double rightValue = right.getType().getKind() == SemanticType.Kind.FLOAT64 ? right.float64() : right.int32();
        boolean result;
        switch (operator) {
            case "less":
                result = leftValue < rightValue;
                break;
            case "less_equal":
                result = leftValue <= rightValue;
                break;
            case "greater":
                result = leftValue > rightValue;
                break;
            case "greater_equal":
                result = leftValue >= rightValue;
                break;
            default:
                throw new CompilerException("unsupported relational operator: " + operator);
        }
        return new ConstantValue(SemanticType.scalar(SemanticType.Kind.BOOLEAN), result);
    }

    private ConstantValue numericResult(SemanticType type, double value) {
        if (type.getKind() == SemanticType.Kind.FLOAT64) {
            return new ConstantValue(type, value);
        }
        return new ConstantValue(type, (int) value);
    }

    private ConstantValue identifierConstant(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = head.get(0).toToken().getSource();
        var record = context.getIdentifier(token);
        return record == null ? null : record.getConstantValue();
    }

    private ConstantValue child(ShiftReduceSemanticContext context, HeadNode head, int index) {
        return context.getConstantValue(head.get(index));
    }

    private HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new IllegalStateException("current reduced head is not available");
        }
        return context.getTreeContext().peek().toHead();
    }
}

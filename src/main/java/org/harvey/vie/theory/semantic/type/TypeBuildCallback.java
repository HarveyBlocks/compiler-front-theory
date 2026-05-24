package org.harvey.vie.theory.semantic.type;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

public class TypeBuildCallback implements ShiftReduceCallback {
    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        HeadNode head = currentReducedHead(context);
        TypeRegister result = build(context, normalizeKey(production.toString().trim()), head);
        if (result != null) {
            context.bindType(head, result);
        }
        ShiftReduceCallback.super.onReduce(context, production);
    }

    private TypeRegister build(ShiftReduceSemanticContext context, String key, HeadNode head) {
        if (isEpsilonOf(key, "param_list", "arg_list", "block_items")) {
            return null;
        }
        switch (key) {
            case "program->top_item":
            case "program->top_item program":
            case "top_item->function_decl":
            case "top_item->block_item":
            case "function_decl->function_head block":
                return null;
            case "function_head->type IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE":
            case "function_head->TYPE_VOID IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE":
                return null;
            case "param_list->params":
            case "block_items->block_item":
            case "block_item->decl":
            case "block_item->stmt":
            case "decl->decl_plain":
            case "decl->decl_init":
            case "stmt->matched_stmt":
            case "stmt->unmatched_stmt":
            case "matched_stmt->matched_while_stmt":
            case "matched_stmt->block":
            case "matched_stmt->matched_if_stmt":
            case "matched_stmt->assign_stmt":
            case "matched_stmt->expr_stmt":
            case "unmatched_stmt->unmatched_if_stmt":
            case "unmatched_stmt->unmatched_while_stmt":
            case "bool->join":
            case "join->equality":
            case "equality->rel":
            case "rel->expr":
            case "expr->term":
            case "term->unary":
            case "unary->factor":
            case "factor->loc":
            case "factor->call_expr":
                return child(context, head, 0);
            case "params->param":
            case "params->params OPERATOR_COMMA param":
            case "arg_list->args":
            case "args->bool":
            case "args->args OPERATOR_COMMA bool":
            case "expr_stmt->expr OPERATOR_SEMICOLON":
            case "matched_stmt->return_stmt":
            case "matched_stmt->break_stmt":
            case "matched_stmt->continue_stmt":
            case "matched_stmt->do_while_stmt":
            case "block_items->block_items block_item":
            case "block->OPERATOR_BRACE_OPEN block_items OPERATOR_BRACE_CLOSE":
            case "break_stmt->CONTROL_STRUCTURES_BREAK OPERATOR_SEMICOLON":
            case "continue_stmt->CONTROL_STRUCTURES_CONTINUE OPERATOR_SEMICOLON":
            case "return_stmt->CONTROL_STRUCTURES_RETURN bool OPERATOR_SEMICOLON":
            case "return_stmt->CONTROL_STRUCTURES_RETURN OPERATOR_SEMICOLON":
            case "matched_while_stmt->CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE matched_stmt":
            case "unmatched_while_stmt->CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE unmatched_stmt":
            case "matched_if_stmt->CONTROL_STRUCTURES_IF OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE matched_stmt CONTROL_STRUCTURES_ELSE matched_stmt":
            case "unmatched_if_stmt->CONTROL_STRUCTURES_IF OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE stmt":
            case "unmatched_if_stmt->CONTROL_STRUCTURES_IF OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE matched_stmt CONTROL_STRUCTURES_ELSE unmatched_stmt":
            case "do_while_stmt->CONTROL_STRUCTURES_DO stmt CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE OPERATOR_SEMICOLON":
                return null;
            case "decl_plain->type IDENTIFIER OPERATOR_SEMICOLON":
            case "param->type IDENTIFIER":
                return withAnchor(requireChild(context, head, 0), childAnchor(head, 1));
            case "decl_init->type IDENTIFIER OPERATOR_ASSIGN bool OPERATOR_SEMICOLON":
                return withAnchor(requireChild(context, head, 0), childAnchor(head, 1));
            case "type->type OPERATOR_SQUARE_OPEN CONSTANT_INTEGER OPERATOR_SQUARE_CLOSE":
                return arrayType(context, head);
            case "assign_stmt->loc OPERATOR_ASSIGN bool OPERATOR_SEMICOLON":
                return requireChild(context, head, 0);
            case "loc->IDENTIFIER":
                return identifierReference(context, childAnchor(head, 0));
            case "loc->loc OPERATOR_SQUARE_OPEN bool OPERATOR_SQUARE_CLOSE":
                return arrayElement(context, head);
            case "call_expr->IDENTIFIER OPERATOR_PARENTHESIS_OPEN arg_list OPERATOR_PARENTHESIS_CLOSE":
                return functionCall(context, head);
            case "bool->bool OPERATOR_LOGICAL_OR join":
            case "join->join OPERATOR_LOGICAL_AND equality":
                return booleanBinary(context, head);
            case "equality->equality OPERATOR_EQUAL rel":
            case "equality->equality OPERATOR_NOT_EQUAL rel":
                return equality(context, head);
            case "rel->expr OPERATOR_LESS expr":
            case "rel->expr OPERATOR_LESS_EQUAL expr":
            case "rel->expr OPERATOR_GREATER expr":
            case "rel->expr OPERATOR_GREATER_EQUAL expr":
                return relation(context, head);
            case "expr->expr OPERATOR_PLUS term":
            case "expr->expr OPERATOR_MINUS term":
            case "term->term OPERATOR_MULTIPLY unary":
            case "term->term OPERATOR_DIVIDE unary":
                return numericBinary(context, head);
            case "unary->OPERATOR_LOGICAL_NOT unary":
                return unaryBoolean(context, head);
            case "unary->OPERATOR_MINUS unary":
                return unaryNumeric(context, head);
            case "factor->OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE":
                return requireChild(context, head, 1);
            case "factor->CONSTANT_INTEGER":
            case "factor->CONSTANT_FLOAT":
            case "factor->CONSTANT_BOOLEAN_TRUE":
            case "factor->CONSTANT_BOOLEAN_FALSE":
                return literal(context, childAnchor(head, 0));
            case "type->TYPE_BOOLEAN":
            case "type->TYPE_CHARACTER":
            case "type->TYPE_INT32":
            case "type->TYPE_FLOAT64":
            case "type->TYPE_STRING":
            case "type->TYPE_VOID":
                return declaredType(context, childAnchor(head, 0));
            default:
                if (head.size() == 1 && context.hasType(head.get(0))) {
                    return child(context, head, 0);
                }
                throw new CompilerException("unhandled production in TypeBuildCallback: " + key);
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

    private static TypeRegister literal(ShiftReduceSemanticContext context, SourceToken token) {
        SemanticType type = context.literalType(token);
        return TypeRegister.simple(type, token);
    }

    private static TypeRegister declaredType(ShiftReduceSemanticContext context, SourceToken token) {
        SemanticType type = context.typeToken(token);
        return TypeRegister.simple(type, token);
    }

    private static TypeRegister arrayType(ShiftReduceSemanticContext context, HeadNode head) {
        TypeRegister base = requireChild(context, head, 0);
        int dimension = context.integerLiteral(childAnchor(head, 2));
        return TypeRegister.simple(
                base.requireType("array type declaration requires a base type.").withAppendedDimension(dimension),
                childAnchor(head, 0)
        );
    }

    private static TypeRegister arrayElement(ShiftReduceSemanticContext context, HeadNode head) {
        SemanticType baseType = requireChild(context, head, 0)
                .requireType("array indexing requires the left operand to have a type.");
        SemanticType indexType = requireChild(context, head, 2)
                .requireType("array indexing requires the index expression to have a type.");
        if (!baseType.isArray()) {
            throw new CompilerException("subscript operator requires an array operand.");
        }
        if (!SemanticType.scalar(SemanticType.Kind.INT32).equals(indexType)) {
            throw new CompilerException("array index must be int32.");
        }
        return TypeRegister.simple(baseType.arrayElementType(), childAnchor(head, 0));
    }

    private static TypeRegister booleanBinary(ShiftReduceSemanticContext context, HeadNode head) {
        SemanticType left = requireChild(context, head, 0)
                .requireType("boolean binary expression requires a typed left operand.");
        SemanticType right = requireChild(context, head, 2)
                .requireType("boolean binary expression requires a typed right operand.");
        if (left.isBooleanScalar() && right.isBooleanScalar()) {
            return TypeRegister.simple(SemanticType.scalar(SemanticType.Kind.BOOLEAN), childAnchor(head, 1));
        }
        throw new CompilerException("logical operator requires boolean operands.");
    }

    private static TypeRegister equality(ShiftReduceSemanticContext context, HeadNode head) {
        SemanticType left = requireChild(context, head, 0)
                .requireType("equality expression requires a typed left operand.");
        SemanticType right = requireChild(context, head, 2)
                .requireType("equality expression requires a typed right operand.");
        boolean sameType = left.equals(right);
        boolean numericComparable = left.isNumericScalar() && right.isNumericScalar();
        if (sameType || numericComparable) {
            SemanticType instructionType = numericComparable ? context.commonBinaryType(left, right) : left;
            return TypeRegister.typed(
                    SemanticType.scalar(SemanticType.Kind.BOOLEAN),
                    instructionType,
                    childAnchor(head, 1)
            );
        }
        throw new CompilerException("equality operator requires identical types or comparable numeric types.");
    }

    private static TypeRegister relation(ShiftReduceSemanticContext context, HeadNode head) {
        SemanticType left = requireChild(context, head, 0)
                .requireType("relational expression requires a typed left operand.");
        SemanticType right = requireChild(context, head, 2)
                .requireType("relational expression requires a typed right operand.");
        if (left.isNumericScalar() && right.isNumericScalar()) {
            return TypeRegister.typed(
                    SemanticType.scalar(SemanticType.Kind.BOOLEAN),
                    context.commonBinaryType(left, right),
                    childAnchor(head, 1)
            );
        }
        throw new CompilerException("relational operator requires numeric operands.");
    }

    private static TypeRegister numericBinary(ShiftReduceSemanticContext context, HeadNode head) {
        SemanticType left = requireChild(context, head, 0)
                .requireType("numeric binary expression requires a typed left operand.");
        SemanticType right = requireChild(context, head, 2)
                .requireType("numeric binary expression requires a typed right operand.");
        if (left.isNumericScalar() && right.isNumericScalar()) {
            SemanticType instructionType = context.commonBinaryType(left, right);
            return TypeRegister.typed(instructionType, instructionType, childAnchor(head, 1));
        }
        throw new CompilerException("arithmetic operator requires numeric operands.");
    }

    private static TypeRegister unaryBoolean(ShiftReduceSemanticContext context, HeadNode head) {
        SemanticType operandType = requireChild(context, head, 1).requireType("operator '!' requires a typed operand.");
        if (operandType.isBooleanScalar()) {
            return TypeRegister.simple(SemanticType.scalar(SemanticType.Kind.BOOLEAN), childAnchor(head, 0));
        }
        throw new CompilerException("operator '!' requires a boolean operand.");
    }

    private static TypeRegister unaryNumeric(ShiftReduceSemanticContext context, HeadNode head) {
        TypeRegister operand = requireChild(context, head, 1);
        SemanticType operandType = operand.requireType("operator '-' requires a typed operand.");
        if (operandType.isNumericScalar()) {
            return TypeRegister.typed(
                    operandType,
                    operand.requireInstructionType("operator '-' requires an instruction type."),
                    childAnchor(head, 0)
            );
        }
        throw new CompilerException("operator '-' requires a numeric operand.");
    }

    private static TypeRegister identifierReference(ShiftReduceSemanticContext context, SourceToken token) {
        IdentifierRecord record = context.getIdentifier(token);
        if (record == null) {
            throw new CompilerException("identifier is not declared in current visible scopes.");
        }
        return TypeRegister.simple(record.getDeclaredType(), token);
    }

    private static TypeRegister functionCall(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = childAnchor(head, 0);
        String name = new String(token.getLexeme(), StandardCharsets.UTF_8);
        var function = context.getFunction(name);
        if (function == null) {
            throw new CompilerException("function is not declared in current visible scope.");
        }
        return TypeRegister.simple(function.getSignature().getReturnType(), token);
    }

    private static HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new IllegalStateException("current reduced head is not available");
        }
        return context.getTreeContext().peek().toHead();
    }

    private static TypeRegister child(ShiftReduceSemanticContext context, HeadNode head, int index) {
        return context.getType(head.get(index));
    }

    private static TypeRegister requireChild(ShiftReduceSemanticContext context, HeadNode head, int index) {
        TypeRegister register = child(context, head, index);
        if (register == null) {
            throw new CompilerException("semantic type is absent for child #" + index);
        }
        return register;
    }

    private static SourceToken childAnchor(HeadNode head, int index) {
        return anchorOf(head.get(index));
    }

    private static SourceToken anchorOf(ShiftReduceSyntaxTreeNode node) {
        Queue<ShiftReduceSyntaxTreeNode> queue = new ArrayDeque<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            ShiftReduceSyntaxTreeNode current = queue.remove();
            if (current.isToken()) {
                return current.toToken().getSource();
            }
            if (!current.isHead()) {
                continue;
            }
            for (ShiftReduceSyntaxTreeNode child : current.toHead()) {
                queue.add(child);
            }
        }
        return null;
    }

    private static TypeRegister withAnchor(TypeRegister register, SourceToken anchor) {
        return new TypeRegister(register.getType(), register.getInstructionType(), anchor);
    }
}

package org.harvey.vie.theory.semantic.type;

import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.callback.bu.BuildStackContextCallback;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.Stack;

/**
 * Builds a dedicated type-attribute stack alongside syntax reduction.
 */
public class TypeBuildCallback extends BuildStackContextCallback<TypeRegister> implements ShiftReduceCallback {
    public TypeBuildCallback() {
        super(new SupplierImpl());
    }

    private static final class SupplierImpl implements Supplier<TypeRegister> {
        @Override
        public Stack<TypeRegister> getStackContext(ShiftReduceSemanticContext context) {
            return context.getTypeContext();
        }

        @Override
        public TypeRegister[] instanceChildrenArray(int n) {
            return new TypeRegister[n];
        }

        @Override
        public TypeRegister instanceNodeOnReduce(
                ShiftReduceSemanticContext context,
                SimpleGrammarProduction production,
                TypeRegister[] children) {
            // TODO 糟糕的设计,
            //  解析字符串基于底层的具体实现, 你怎么知道字符串是这样的呢?
            //  字符串稍微变一变你又怎么办呢?
            String key = production.toString().trim();
            TypeRegister result;
            switch (key) {
                case "decls->ε":
                case "stmts->ε":
                case "decls->decls decl":
                case "stmts->stmts stmt":
                case "block->OPERATOR_BRACE_OPEN decls stmts OPERATOR_BRACE_CLOSE":
                case "matched_stmt->CONTROL_STRUCTURES_BREAK OPERATOR_SEMICOLON":
                    result = TypeRegister.noType(firstAnchor(children));
                    break;
                case "program->block":
                case "stmt->matched_stmt":
                case "stmt->unmatched_stmt":
                case "matched_stmt->matched_while_stmt":
                case "matched_stmt->block":
                case "matched_stmt->matched_if_stmt":
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
                    result = passthrough(children, 0);
                    break;
                case "decl->type IDENTIFIER OPERATOR_SEMICOLON":
                    result = new TypeRegister(
                            children[0].getType(),
                            children[0].getInstructionType(),
                            children[1].getAnchorToken()
                    );
                    break;
                case "type->type OPERATOR_SQUARE_OPEN CONSTANT_INTEGER OPERATOR_SQUARE_CLOSE":
                    result = arrayType(context, children);
                    break;
                case "matched_stmt->loc OPERATOR_ASSIGN bool OPERATOR_SEMICOLON":
                    result = passthrough(children, 0);
                    break;
                case "matched_while_stmt->CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE matched_stmt":
                case "unmatched_while_stmt->CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE unmatched_stmt":
                case "matched_if_stmt->CONTROL_STRUCTURES_IF OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE matched_stmt CONTROL_STRUCTURES_ELSE matched_stmt":
                case "unmatched_if_stmt->CONTROL_STRUCTURES_IF OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE stmt":
                case "unmatched_if_stmt->CONTROL_STRUCTURES_IF OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE matched_stmt CONTROL_STRUCTURES_ELSE unmatched_stmt":
                case "matched_stmt->CONTROL_STRUCTURES_DO stmt CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE OPERATOR_SEMICOLON":
                    result = TypeRegister.noType(children[0].getAnchorToken());
                    break;
                case "loc->IDENTIFIER":
                    result = identifierReference(context, children[0].getAnchorToken());
                    break;
                case "loc->loc OPERATOR_SQUARE_OPEN bool OPERATOR_SQUARE_CLOSE":
                    result = arrayElement(children);
                    break;
                case "bool->bool OPERATOR_LOGICAL_OR join":
                case "join->join OPERATOR_LOGICAL_AND equality":
                    result = booleanBinary(children);
                    break;
                case "equality->equality OPERATOR_EQUAL rel":
                case "equality->equality OPERATOR_NOT_EQUAL rel":
                    result = equality(children, context);
                    break;
                case "rel->expr OPERATOR_LESS expr":
                case "rel->expr OPERATOR_LESS_EQUAL expr":
                case "rel->expr OPERATOR_GREATER expr":
                case "rel->expr OPERATOR_GREATER_EQUAL expr":
                    result = relation(children, context);
                    break;
                case "expr->expr OPERATOR_PLUS term":
                case "expr->expr OPERATOR_MINUS term":
                case "term->term OPERATOR_MULTIPLY unary":
                case "term->term OPERATOR_DIVIDE unary":
                    result = numericBinary(children, context);
                    break;
                case "unary->OPERATOR_LOGICAL_NOT unary":
                    result = unaryBoolean(children);
                    break;
                case "unary->OPERATOR_MINUS unary":
                    result = unaryNumeric(children);
                    break;
                case "factor->OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE":
                    result = passthrough(children, 1);
                    break;
                default:
                    if (children.length == 1) {
                        result = passthrough(children, 0);
                        break;
                    }
                    throw new CompilerException("unhandled production in TypeBuildCallback: " + key);
            }
            context.setCurrentTypeReductionFrame(new TypeReductionFrame(children, result));
            return result;
        }

        @Override
        public TypeRegister instanceNodeOnShift(ShiftReduceSemanticContext context, SourceToken token) {
            ProgramTokenType type = (ProgramTokenType) token.getType();
            switch (type) {
                case IDENTIFIER:
                    return TypeRegister.noType(token);
                case TYPE_BOOLEAN:
                case TYPE_CHARACTER:
                case TYPE_INT32:
                case TYPE_FLOAT64:
                case TYPE_STRING:
                case CONSTANT_INTEGER:
                case CONSTANT_FLOAT:
                case CONSTANT_BOOLEAN_TRUE:
                case CONSTANT_BOOLEAN_FALSE:
                case CONSTANT_CHARACTER:
                case CONSTANT_STRING:
                    return TypeRegister.simple(resolveTokenType(context, token), token);
                default:
                    return TypeRegister.noType(token);
            }
        }

        private static SemanticType resolveTokenType(ShiftReduceSemanticContext context, SourceToken token) {
            ProgramTokenType type = (ProgramTokenType) token.getType();
            switch (type) {
                case TYPE_BOOLEAN:
                case TYPE_CHARACTER:
                case TYPE_INT32:
                case TYPE_FLOAT64:
                case TYPE_STRING:
                    return context.getTypeSystem().typeToken(token);
                default:
                    return context.getTypeSystem().literalType(token);
            }
        }

        private static TypeRegister arrayType(ShiftReduceSemanticContext context, TypeRegister[] children) {
            SemanticType baseType = children[0].requireType("array type declaration requires a base type.");
            int dimension = context.getTypeSystem().integerLiteral(children[2].getAnchorToken());
            return TypeRegister.simple(baseType.withAppendedDimension(dimension), children[0].getAnchorToken());
        }

        private static TypeRegister arrayElement(TypeRegister[] children) {
            SemanticType baseType = children[0].requireType("array indexing requires the left operand to have a type.");
            SemanticType indexType = children[2].requireType("array indexing requires the index expression to have a type.");
            if (!baseType.isArray()) {
                throw new CompilerException("subscript operator requires an array operand.");
            }
            if (!SemanticType.scalar(SemanticType.Kind.INT32).equals(indexType)) {
                throw new CompilerException("array index must be int32.");
            }
            return TypeRegister.simple(baseType.arrayElementType(), children[0].getAnchorToken());
        }

        private static TypeRegister booleanBinary(TypeRegister[] children) {
            SemanticType left = children[0].requireType("boolean binary expression requires a typed left operand.");
            SemanticType right = children[2].requireType("boolean binary expression requires a typed right operand.");
            if (left.isBooleanScalar() && right.isBooleanScalar()) {
                return TypeRegister.simple(SemanticType.scalar(SemanticType.Kind.BOOLEAN), children[1].getAnchorToken());
            }
            throw new CompilerException("logical operator requires boolean operands.");
        }

        private static TypeRegister equality(TypeRegister[] children, ShiftReduceSemanticContext context) {
            SemanticType left = children[0].requireType("equality expression requires a typed left operand.");
            SemanticType right = children[2].requireType("equality expression requires a typed right operand.");
            boolean sameType = left.equals(right);
            boolean numericComparable = left.isNumericScalar() && right.isNumericScalar();
            if (sameType || numericComparable) {
                SemanticType instructionType = numericComparable
                        ? context.getTypeSystem().commonBinaryType(left, right)
                        : left;
                return TypeRegister.typed(
                        SemanticType.scalar(SemanticType.Kind.BOOLEAN),
                        instructionType,
                        children[1].getAnchorToken()
                );
            }
            throw new CompilerException("equality operator requires identical types or comparable numeric types.");
        }

        private static TypeRegister relation(TypeRegister[] children, ShiftReduceSemanticContext context) {
            SemanticType left = children[0].requireType("relational expression requires a typed left operand.");
            SemanticType right = children[2].requireType("relational expression requires a typed right operand.");
            if (left.isNumericScalar() && right.isNumericScalar()) {
                return TypeRegister.typed(
                        SemanticType.scalar(SemanticType.Kind.BOOLEAN),
                        context.getTypeSystem().commonBinaryType(left, right),
                        children[1].getAnchorToken()
                );
            }
            throw new CompilerException("relational operator requires numeric operands.");
        }

        private static TypeRegister numericBinary(TypeRegister[] children, ShiftReduceSemanticContext context) {
            SemanticType left = children[0].requireType("numeric binary expression requires a typed left operand.");
            SemanticType right = children[2].requireType("numeric binary expression requires a typed right operand.");
            if (left.isNumericScalar() && right.isNumericScalar()) {
                SemanticType instructionType = context.getTypeSystem().commonBinaryType(left, right);
                return TypeRegister.typed(instructionType, instructionType, children[1].getAnchorToken());
            }
            throw new CompilerException("arithmetic operator requires numeric operands.");
        }

        private static TypeRegister unaryBoolean(TypeRegister[] children) {
            SemanticType operandType = children[1].requireType("operator '!' requires a typed operand.");
            return operandType.isBooleanScalar()
                    ? TypeRegister.simple(SemanticType.scalar(SemanticType.Kind.BOOLEAN), children[0].getAnchorToken())
                    : fail("operator '!' requires a boolean operand.");
        }

        private static TypeRegister unaryNumeric(TypeRegister[] children) {
            SemanticType operandType = children[1].requireType("operator '-' requires a typed operand.");
            return operandType.isNumericScalar()
                    ? TypeRegister.typed(
                            operandType,
                            children[1].requireInstructionType("operator '-' requires an instruction type."),
                            children[0].getAnchorToken()
                    )
                    : fail("operator '-' requires a numeric operand.");
        }

        private static TypeRegister passthrough(TypeRegister[] children, int index) {
            return children[index];
        }

        private static SourceToken firstAnchor(TypeRegister[] children) {
            for (TypeRegister child : children) {
                if (child != null && child.getAnchorToken() != null) {
                    return child.getAnchorToken();
                }
            }
            return null;
        }

        private static TypeRegister identifierReference(ShiftReduceSemanticContext context, SourceToken token) {
            IdentifierRecord record = context.getIdentifier(token);
            if (record == null) {
                throw new CompilerException("identifier is not declared in current visible scopes.");
            }
            return TypeRegister.simple(record.getDeclaredType(), token);
        }

        private static TypeRegister fail(String message) {
            throw new CompilerException(message);
        }
    }
}

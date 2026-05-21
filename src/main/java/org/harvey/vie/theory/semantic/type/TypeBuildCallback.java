package org.harvey.vie.theory.semantic.type;

import org.harvey.vie.theory.demo.program.ProgramTokenType;
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
            String key = production.toString().trim();
            switch (key) {
                case "decls->ε":
                case "stmts->ε":
                case "decls->decls decl":
                case "stmts->stmts stmt":
                case "block->OPERATOR_BRACE_OPEN decls stmts OPERATOR_BRACE_CLOSE":
                case "matched_stmt->CONTROL_STRUCTURES_BREAK OPERATOR_SEMICOLON":
                    return TypeRegister.unknown(firstAnchor(children));
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
                    return passthrough(children, 0);
                case "decl->type IDENTIFIER OPERATOR_SEMICOLON":
                    return new TypeRegister(children[0].getType(), children[1].getAnchorToken());
                case "type->type OPERATOR_SQUARE_OPEN CONSTANT_INTEGER OPERATOR_SQUARE_CLOSE":
                    return arrayType(context, children);
                case "matched_stmt->loc OPERATOR_ASSIGN bool OPERATOR_SEMICOLON":
                    return passthrough(children, 0);
                case "matched_while_stmt->CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE matched_stmt":
                case "unmatched_while_stmt->CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE unmatched_stmt":
                case "matched_if_stmt->CONTROL_STRUCTURES_IF OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE matched_stmt CONTROL_STRUCTURES_ELSE matched_stmt":
                case "unmatched_if_stmt->CONTROL_STRUCTURES_IF OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE stmt":
                case "unmatched_if_stmt->CONTROL_STRUCTURES_IF OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE matched_stmt CONTROL_STRUCTURES_ELSE unmatched_stmt":
                case "matched_stmt->CONTROL_STRUCTURES_DO stmt CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE OPERATOR_SEMICOLON":
                    return TypeRegister.unknown(children[0].getAnchorToken());
                case "loc->loc OPERATOR_SQUARE_OPEN bool OPERATOR_SQUARE_CLOSE":
                    return arrayElement(children);
                case "bool->bool OPERATOR_LOGICAL_OR join":
                case "join->join OPERATOR_LOGICAL_AND equality":
                    return booleanBinary(children);
                case "equality->equality OPERATOR_EQUAL rel":
                case "equality->equality OPERATOR_NOT_EQUAL rel":
                    return equality(children, context);
                case "rel->expr OPERATOR_LESS expr":
                case "rel->expr OPERATOR_LESS_EQUAL expr":
                case "rel->expr OPERATOR_GREATER expr":
                case "rel->expr OPERATOR_GREATER_EQUAL expr":
                    return relation(children, context);
                case "expr->expr OPERATOR_PLUS term":
                case "expr->expr OPERATOR_MINUS term":
                case "term->term OPERATOR_MULTIPLY unary":
                case "term->term OPERATOR_DIVIDE unary":
                    return numericBinary(children, context);
                case "unary->OPERATOR_LOGICAL_NOT unary":
                    return unaryBoolean(children);
                case "unary->OPERATOR_MINUS unary":
                    return unaryNumeric(children);
                case "factor->OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE":
                    return passthrough(children, 1);
                default:
                    if (children.length == 1) {
                        return passthrough(children, 0);
                    }
                    return TypeRegister.unknown(firstAnchor(children));
            }
        }

        @Override
        public TypeRegister instanceNodeOnShift(ShiftReduceSemanticContext context, SourceToken token) {
            ProgramTokenType type = (ProgramTokenType) token.getType();
            if (type == ProgramTokenType.IDENTIFIER) {
                IdentifierRecord record = context.getIdentifier(token);
                return new TypeRegister(record == null ? SemanticType.unknown() : record.getDeclaredType(), token);
            }
            switch (type) {
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
                    return new TypeRegister(resolveTokenType(context, token), token);
                default:
                    return TypeRegister.unknown(token);
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
            SemanticType baseType = children[0].getType();
            if (baseType.isUnknown()) {
                return TypeRegister.unknown(children[0].getAnchorToken());
            }
            int dimension = context.getTypeSystem().integerLiteral(children[2].getAnchorToken());
            return new TypeRegister(baseType.withAppendedDimension(dimension), children[0].getAnchorToken());
        }

        private static TypeRegister arrayElement(TypeRegister[] children) {
            SemanticType baseType = children[0].getType();
            SemanticType indexType = children[2].getType();
            if (!baseType.isUnknown() &&
                baseType.isArray() &&
                SemanticType.scalar(SemanticType.Kind.INT32).equals(indexType)) {
                return new TypeRegister(baseType.arrayElementType(), children[0].getAnchorToken());
            }
            return TypeRegister.unknown(children[0].getAnchorToken());
        }

        private static TypeRegister booleanBinary(TypeRegister[] children) {
            SemanticType left = children[0].getType();
            SemanticType right = children[2].getType();
            if (left.isBooleanScalar() && right.isBooleanScalar()) {
                return new TypeRegister(SemanticType.scalar(SemanticType.Kind.BOOLEAN), children[1].getAnchorToken());
            }
            return TypeRegister.unknown(children[1].getAnchorToken());
        }

        private static TypeRegister equality(TypeRegister[] children, ShiftReduceSemanticContext context) {
            SemanticType left = children[0].getType();
            SemanticType right = children[2].getType();
            boolean sameType = !left.isUnknown() && left.equals(right);
            boolean numericComparable = left.isNumericScalar() && right.isNumericScalar();
            if (sameType || numericComparable) {
                return new TypeRegister(SemanticType.scalar(SemanticType.Kind.BOOLEAN), children[1].getAnchorToken());
            }
            if (left.isUnknown() || right.isUnknown()) {
                return TypeRegister.unknown(children[1].getAnchorToken());
            }
            return new TypeRegister(context.getTypeSystem().commonBinaryType(left, right), children[1].getAnchorToken());
        }

        private static TypeRegister relation(TypeRegister[] children, ShiftReduceSemanticContext context) {
            SemanticType left = children[0].getType();
            SemanticType right = children[2].getType();
            if (left.isNumericScalar() && right.isNumericScalar()) {
                return new TypeRegister(SemanticType.scalar(SemanticType.Kind.BOOLEAN), children[1].getAnchorToken());
            }
            if (left.isUnknown() || right.isUnknown()) {
                return TypeRegister.unknown(children[1].getAnchorToken());
            }
            return new TypeRegister(context.getTypeSystem().commonBinaryType(left, right), children[1].getAnchorToken());
        }

        private static TypeRegister numericBinary(TypeRegister[] children, ShiftReduceSemanticContext context) {
            SemanticType left = children[0].getType();
            SemanticType right = children[2].getType();
            if (left.isNumericScalar() && right.isNumericScalar()) {
                return new TypeRegister(context.getTypeSystem().commonBinaryType(left, right), children[1].getAnchorToken());
            }
            return TypeRegister.unknown(children[1].getAnchorToken());
        }

        private static TypeRegister unaryBoolean(TypeRegister[] children) {
            return children[1].getType().isBooleanScalar()
                    ? new TypeRegister(SemanticType.scalar(SemanticType.Kind.BOOLEAN), children[0].getAnchorToken())
                    : TypeRegister.unknown(children[0].getAnchorToken());
        }

        private static TypeRegister unaryNumeric(TypeRegister[] children) {
            return children[1].getType().isNumericScalar()
                    ? new TypeRegister(children[1].getType(), children[0].getAnchorToken())
                    : TypeRegister.unknown(children[0].getAnchorToken());
        }

        private static TypeRegister passthrough(TypeRegister[] children, int index) {
            return new TypeRegister(children[index].getType(), children[index].getAnchorToken());
        }

        private static SourceToken firstAnchor(TypeRegister[] children) {
            for (TypeRegister child : children) {
                if (child != null && child.getAnchorToken() != null) {
                    return child.getAnchorToken();
                }
            }
            return null;
        }
    }
}

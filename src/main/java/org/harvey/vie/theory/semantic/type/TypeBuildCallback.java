package org.harvey.vie.theory.semantic.type;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.array.ArrayCreationDimensions;
import org.harvey.vie.theory.semantic.command.LocationKind;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tag.ProductionTagStrategy;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * @author Temper
 */
public class TypeBuildCallback implements ShiftReduceCallback {
    private static final ProductionTagStrategy<TypeRule> RULES = new ProductionTagStrategy<>(TypeRule.UNHANDLED)
            .when(TypeRule.NONE, ProgramSemanticTag.PROGRAM)
            .when(TypeRule.NONE, ProgramSemanticTag.NOOP)
            .when(TypeRule.NONE, ProgramSemanticTag.RETURN)
            .when(TypeRule.NONE, ProgramSemanticTag.LOOP)
            .when(TypeRule.NONE, ProgramSemanticTag.CONDITIONAL)
            .when(TypeRule.NONE, ProgramSemanticTag.EMPTY)
            .when(TypeRule.NONE, ProgramSemanticTag.BLOCK, ProgramSemanticTag.COMMAND)
            .when(TypeRule.NONE, ProgramSemanticTag.BLOCK, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
            .when(TypeRule.NONE, ProgramSemanticTag.BLOCK, ProgramSemanticTag.LIST, ProgramSemanticTag.SEQUENCE)
            .when(TypeRule.FORWARD, ProgramSemanticTag.ITEM)
            .when(TypeRule.FORWARD, ProgramSemanticTag.STATEMENT, ProgramSemanticTag.FORWARD)
            .when(TypeRule.NONE, ProgramSemanticTag.PARAMETER, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
            .when(TypeRule.NONE, ProgramSemanticTag.PARAMETER, ProgramSemanticTag.LIST, ProgramSemanticTag.SEQUENCE)
            .when(TypeRule.NONE, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
            .when(TypeRule.NONE, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.LIST, ProgramSemanticTag.SEQUENCE)
            .when(TypeRule.NONE, ProgramSemanticTag.STRUCT_DECL)
            .when(TypeRule.NONE, ProgramSemanticTag.LIST, ProgramSemanticTag.STRUCT_FIELD, ProgramSemanticTag.EMPTY)
            .when(TypeRule.NONE, ProgramSemanticTag.LIST, ProgramSemanticTag.STRUCT_FIELD, ProgramSemanticTag.FORWARD)
            .when(TypeRule.NONE, ProgramSemanticTag.LIST, ProgramSemanticTag.STRUCT_FIELD, ProgramSemanticTag.SEQUENCE)
            .when(TypeRule.FORWARD, ProgramSemanticTag.FORWARD)
            .when(TypeRule.ARRAY_TYPE, ProgramSemanticTag.TYPE, ProgramSemanticTag.ARRAY)
            .when(TypeRule.NAMED_STRUCT_TYPE, ProgramSemanticTag.TYPE, ProgramSemanticTag.STRUCT_TYPE)
            .when(TypeRule.ARRAY_CREATION_BASE, ProgramSemanticTag.ARRAY_CREATION_BASE, ProgramSemanticTag.STRUCT_TYPE)
            .when(TypeRule.ARRAY_CREATION_BASE, ProgramSemanticTag.ARRAY_CREATION_BASE)
            .when(TypeRule.TYPE_DECLARATION, ProgramSemanticTag.TYPE)
            .when(TypeRule.DECLARED_IDENTIFIER, ProgramSemanticTag.DECLARATION, ProgramSemanticTag.IDENTIFIER)
            .when(TypeRule.DECLARED_IDENTIFIER, ProgramSemanticTag.STRUCT_FIELD, ProgramSemanticTag.IDENTIFIER)
            .when(TypeRule.DECLARED_IDENTIFIER, ProgramSemanticTag.PARAMETER, ProgramSemanticTag.IDENTIFIER)
            .when(TypeRule.IDENTIFIER_REFERENCE, ProgramSemanticTag.IDENTIFIER, ProgramSemanticTag.USE)
            .when(TypeRule.ARRAY_ACCESS, ProgramSemanticTag.ACCESS)
            .when(TypeRule.MEMBER_ACCESS, ProgramSemanticTag.MEMBER_ACCESS)
            .when(TypeRule.LEFT_VALUE, ProgramSemanticTag.LEFT_VALUE)
            .when(TypeRule.FUNCTION_CALL, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.CALL)
            .when(TypeRule.LITERAL, ProgramSemanticTag.LITERAL)
            .when(TypeRule.LITERAL, ProgramSemanticTag.FORWARD, ProgramSemanticTag.LITERAL)
            .when(TypeRule.NULL_LITERAL, ProgramSemanticTag.NULL_LITERAL)
            .when(TypeRule.NULL_LITERAL, ProgramSemanticTag.FORWARD, ProgramSemanticTag.NULL_LITERAL)
            .when(TypeRule.NEW_STRUCT, ProgramSemanticTag.NEW_STRUCT)
            .when(TypeRule.NEW_ARRAY, ProgramSemanticTag.NEW_ARRAY)
            .when(TypeRule.PARENTHESIZED, ProgramSemanticTag.PARENTHESIZED)
            .when(TypeRule.PARENTHESIZED, ProgramSemanticTag.FORWARD, ProgramSemanticTag.PARENTHESIZED)
            .when(TypeRule.UNARY_BOOLEAN, ProgramSemanticTag.LOGICAL_NOT)
            .when(TypeRule.UNARY_NUMERIC, ProgramSemanticTag.NEGATE)
            .when(TypeRule.BOOLEAN_BINARY, ProgramSemanticTag.OR)
            .when(TypeRule.BOOLEAN_BINARY, ProgramSemanticTag.AND)
            .when(TypeRule.EQUALITY, ProgramSemanticTag.EQUAL)
            .when(TypeRule.EQUALITY, ProgramSemanticTag.NOT_EQUAL)
            .when(TypeRule.RELATION, ProgramSemanticTag.LESS)
            .when(TypeRule.RELATION, ProgramSemanticTag.LESS_EQUAL)
            .when(TypeRule.RELATION, ProgramSemanticTag.GREATER)
            .when(TypeRule.RELATION, ProgramSemanticTag.GREATER_EQUAL)
            .when(TypeRule.NUMERIC_BINARY, ProgramSemanticTag.PLUS)
            .when(TypeRule.NUMERIC_BINARY, ProgramSemanticTag.MINUS)
            .when(TypeRule.NUMERIC_BINARY, ProgramSemanticTag.MULTIPLY)
            .when(TypeRule.NUMERIC_BINARY, ProgramSemanticTag.DIVIDE)
            .when(TypeRule.ASSIGNMENT, ProgramSemanticTag.ASSIGNMENT)
            .when(TypeRule.VOID_FUNCTION_HEAD, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.HEAD)
            ;

    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        HeadNode head = currentReducedHead(context);
        if (head.getSymbol().isDefine()) {
            String defineName = head.getSymbol().toDefine().getName();
            if ("loc".equals(defineName) && head.size() == 1 && head.get(0).isToken()) {
                TypeRegister result = identifierReference(context, head);
                context.bindType(head, result);
                ShiftReduceCallback.super.onReduce(context, production);
                return;
            }
        }
        TypeRegister result = RULES.resolve(production).build(context, head, production);
        if (result != null) {
            context.bindType(head, result);
        }
        ShiftReduceCallback.super.onReduce(context, production);
    }

    private static TypeRegister forward(ShiftReduceSemanticContext context, HeadNode head) {
        for (ShiftReduceSyntaxTreeNode child : head) {
            TypeRegister register = context.getType(child);
            if (register != null) {
                return register;
            }
        }
        return null;
    }

    private static TypeRegister typeDeclaration(ShiftReduceSemanticContext context, HeadNode head) {
        return declaredType(context, childAnchor(head, 0));
    }

    private static TypeRegister declaredIdentifier(ShiftReduceSemanticContext context, HeadNode head) {
        return withAnchor(requireChild(context, head, 0), childAnchor(head, 1), LocationKind.ADDRESS);
    }

    private static TypeRegister literal(ShiftReduceSemanticContext context, HeadNode head) {
        return literal(context, childAnchor(head, 0));
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
        return TypeRegister.simple(
                base.requireType("array type declaration requires a base type.").withAppendedDimension(),
                childAnchor(head, 0)
        );
    }

    private static TypeRegister namedStructType(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = childAnchor(head, 0);
        return TypeRegister.simple(SemanticType.struct(token), token);
    }

    private static TypeRegister arrayCreationBase(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = childAnchor(head, 0);
        if (head.containsTag(ProgramSemanticTag.STRUCT_TYPE)) {
            return TypeRegister.simple(SemanticType.struct(token), token);
        }
        return TypeRegister.simple(context.typeToken(token), token);
    }

    private static TypeRegister assignment(ShiftReduceSemanticContext context, HeadNode head) {
        return requireChild(context, head, 0);
    }

    private static TypeRegister identifierReference(ShiftReduceSemanticContext context, HeadNode head) {
        return identifierReference(context, childAnchor(head, 0));
    }

    private static TypeRegister identifierReference(ShiftReduceSemanticContext context, SourceToken token) {
        var record = context.getIdentifier(token);
        if (record == null) {
            throw new CompilerException("identifier is not declared in current visible scopes.");
        }
        return TypeRegister.located(record.getDeclaredType(), record.getDeclaredType(), token, LocationKind.ADDRESS);
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
        return TypeRegister.located(baseType.arrayElementType(), baseType.arrayElementType(), childAnchor(head, 0), LocationKind.REFERENCE);
    }

    private static TypeRegister memberAccess(ShiftReduceSemanticContext context, HeadNode head) {
        SemanticType baseType = requireChild(context, head, 0)
                .requireType("member access requires a typed left operand.");
        if (!baseType.isStruct()) {
            throw new CompilerException("member access requires a struct operand.");
        }
        var struct = context.getStruct(baseType);
        if (struct == null) {
            throw new CompilerException("struct type is not declared.");
        }
        SourceToken fieldToken = childAnchor(head, 2);
        var field = struct.field(fieldToken);
        if (field == null) {
            throw new CompilerException("struct field does not exist.");
        }
        return TypeRegister.located(field.getType(), field.getType(), fieldToken, LocationKind.REFERENCE);
    }

    private static TypeRegister functionCall(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = childAnchor(head, 0);
        var function = context.getFunction(token);
        if (function == null) {
            throw new CompilerException("function is not declared in current visible scope.");
        }
        return TypeRegister.simple(function.getSignature().getReturnType(), token);
    }

    private static TypeRegister newStruct(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = childAnchor(head, 1);
        SemanticType type = SemanticType.struct(token);
        context.requireDeclaredType(type, token, "struct type is not declared.");
        return TypeRegister.simple(type, token);
    }

    private static TypeRegister newArray(ShiftReduceSemanticContext context, HeadNode head) {
        TypeRegister base = requireChild(context, head, 1);
        SemanticType type = base.requireType("array creation requires a declared base type.");
        if (type.isVoidScalar()) {
            throw new CompilerException("void cannot be used as array element type.");
        }
        ArrayCreationDimensions.Summary summary = ArrayCreationDimensions.summarizeAndValidate(context, head.get(2));
        SemanticType resultType = type;
        for (int i = 0; i < summary.getTotalDimensions(); i++) {
            resultType = resultType.withAppendedDimension();
        }
        return TypeRegister.located(resultType, resultType, base.getAnchorToken(), LocationKind.REFERENCE);
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
        SemanticType operandType = requireChild(context, head, 1)
                .requireType("operator '!' requires a typed operand.");
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

    private static TypeRegister voidFunctionHead(ShiftReduceSemanticContext context, HeadNode head, SimpleGrammarProduction production) {
        ShiftReduceSyntaxTreeNode first = head.get(0);
        if (!first.isToken()) {
            return null;
        }
        SemanticType type = context.typeToken(first.toToken().getSource());
        if (type == null || !type.isVoidScalar()) {
            return null;
        }
        return TypeRegister.simple(type, first.toToken().getSource());
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
        return ShiftReduceSyntaxTreeNode.anchor(head.get(index));
    }

    private static TypeRegister withAnchor(TypeRegister register, SourceToken anchor, LocationKind locationKind) {
        return new TypeRegister(register.getType(), register.getInstructionType(), anchor, locationKind);
    }

    @FunctionalInterface
    private interface TypeRule {
        TypeRule UNHANDLED = (context, head, production) -> {
            throw new CompilerException("unhandled production in TypeBuildCallback: " + production);
        };
        TypeRule NONE = (context, head, production) -> null;
        TypeRule FORWARD = (context, head, production) -> forward(context, head);
        TypeRule TYPE_DECLARATION = (context, head, production) -> typeDeclaration(context, head);
        TypeRule ARRAY_TYPE = (context, head, production) -> arrayType(context, head);
        TypeRule NAMED_STRUCT_TYPE = (context, head, production) -> namedStructType(context, head);
        TypeRule ARRAY_CREATION_BASE = (context, head, production) -> arrayCreationBase(context, head);
        TypeRule DECLARED_IDENTIFIER = (context, head, production) -> declaredIdentifier(context, head);
        TypeRule IDENTIFIER_REFERENCE = (context, head, production) -> identifierReference(context, head);
        TypeRule ARRAY_ACCESS = (context, head, production) -> arrayElement(context, head);
        TypeRule MEMBER_ACCESS = (context, head, production) -> memberAccess(context, head);
        TypeRule FUNCTION_CALL = (context, head, production) -> functionCall(context, head);
        TypeRule LEFT_VALUE = (context, head, production) -> requireChild(context, head, 0);
        TypeRule LITERAL = (context, head, production) -> literal(context, head);
        TypeRule NULL_LITERAL = (context, head, production) -> literal(context, head);
        TypeRule NEW_STRUCT = (context, head, production) -> newStruct(context, head);
        TypeRule NEW_ARRAY = (context, head, production) -> newArray(context, head);
        TypeRule PARENTHESIZED = (context, head, production) -> requireChild(context, head, 1);
        TypeRule UNARY_BOOLEAN = (context, head, production) -> unaryBoolean(context, head);
        TypeRule UNARY_NUMERIC = (context, head, production) -> unaryNumeric(context, head);
        TypeRule BOOLEAN_BINARY = (context, head, production) -> booleanBinary(context, head);
        TypeRule EQUALITY = (context, head, production) -> equality(context, head);
        TypeRule RELATION = (context, head, production) -> relation(context, head);
        TypeRule NUMERIC_BINARY = (context, head, production) -> numericBinary(context, head);
        TypeRule ASSIGNMENT = (context, head, production) -> assignment(context, head);
        TypeRule VOID_FUNCTION_HEAD = TypeBuildCallback::voidFunctionHead;

        TypeRegister build(ShiftReduceSemanticContext context, HeadNode head, SimpleGrammarProduction production);
    }
}


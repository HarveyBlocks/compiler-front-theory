package org.harvey.vie.theory.semantic.type;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.array.ArrayCreationDimensions;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.command.LocationKind;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tag.ProductionTagStrategy;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 在 LR 归约过程中构造并校验类型属性的语义回调。
 * <p>
 * 作用：
 * <p>
 * TypeBuildCallback 是 semantic/type 包中最核心的规则执行器。
 * 每当语法分析器完成一次 reduce，它会：
 * <p>
 * 1. 取得当前刚归约出的 HeadNode。
 * 2. 根据 production 上的语义 tag 选择 TypeRule。
 * 3. 从子节点读取已有 TypeRegister。
 * 4. 计算当前节点的 TypeRegister。
 * 5. 将结果绑定回 TypeContext。
 * <p>
 * 注意：
 * <p>
 * 这个类只负责“类型属性的构造和一部分类型错误检查”。
 * 函数参数个数、函数返回值、结构体声明登记、符号表登记等检查由其他 callback 完成。
 */
public class TypeBuildCallback implements ShiftReduceCallback {
    /**
     * production tag 到类型构造规则的映射表。
     * <p>
     * 输入：
     * <p>
     * 由语法产生式携带的 ProgramSemanticTag 组合。
     * <p>
     * 输出：
     * <p>
     * 解析出一个 TypeRule，用于当前 reduce 的类型构造。
     * <p>
     * 注意：
     * <p>
     * 没有显式匹配到的产生式会使用 TypeRule.UNHANDLED，
     * 从而尽早暴露遗漏的类型规则。
     */
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
            .when(
                    TypeRule.ARRAY_CREATION_DIMENSION_VALUE,
                    ProgramSemanticTag.ARRAY_CREATION_DIM,
                    ProgramSemanticTag.VALUE
            )
            .when(TypeRule.NONE, ProgramSemanticTag.ARRAY_CREATION_DIM)
            .when(
                    TypeRule.FORWARD,
                    ProgramSemanticTag.LIST,
                    ProgramSemanticTag.ARRAY_CREATION_DIM,
                    ProgramSemanticTag.FORWARD
            )
            .when(
                    TypeRule.NONE,
                    ProgramSemanticTag.LIST,
                    ProgramSemanticTag.ARRAY_CREATION_DIM,
                    ProgramSemanticTag.SEQUENCE
            )
            .when(TypeRule.TYPE_DECLARATION, ProgramSemanticTag.TYPE)
            .when(TypeRule.DECLARED_IDENTIFIER, ProgramSemanticTag.DECLARATION, ProgramSemanticTag.IDENTIFIER)
            .when(TypeRule.DECLARED_IDENTIFIER, ProgramSemanticTag.STRUCT_FIELD, ProgramSemanticTag.IDENTIFIER)
            .when(TypeRule.DECLARED_IDENTIFIER, ProgramSemanticTag.PARAMETER, ProgramSemanticTag.IDENTIFIER)
            .when(TypeRule.ARRAY_ACCESS, ProgramSemanticTag.ACCESS)
            .when(TypeRule.MEMBER_ACCESS, ProgramSemanticTag.MEMBER_ACCESS)
            .when(TypeRule.IDENTIFIER_REFERENCE, ProgramSemanticTag.IDENTIFIER, ProgramSemanticTag.USE)
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
            .when(TypeRule.VOID_FUNCTION_HEAD, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.HEAD);

    /**
     * 函数功能：转发子节点属性。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister forward(ShiftReduceSemanticContext context, HeadNode head) {
        for (ShiftReduceSyntaxTreeNode child : head) {
            TypeRegister register = context.getType(child);
            if (register != null) {
                return register;
            }
        }
        return null;
    }

    /**
     * 函数功能：处理类型声明。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister typeDeclaration(ShiftReduceSemanticContext context, HeadNode head) {
        return declaredType(context, childAnchor(head, 0));
    }

    /**
     * 函数功能：获取已声明的标识符记录。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister declaredIdentifier(ShiftReduceSemanticContext context, HeadNode head) {
        return withAnchor(requireChild(context, head, 0), childAnchor(head, 1), LocationKind.ADDRESS);
    }

    /**
     * 函数功能：创建字面量常量值。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister literal(ShiftReduceSemanticContext context, HeadNode head) {
        return literal(context, childAnchor(head, 0));
    }

    /**
     * 函数功能：创建字面量常量值。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister literal(ShiftReduceSemanticContext context, SourceToken token) {
        SemanticType type = context.literalType(token);
        return TypeRegister.simple(type, token);
    }

    /**
     * 函数功能：解析声明语义类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister declaredType(ShiftReduceSemanticContext context, SourceToken token) {
        SemanticType type = context.typeToken(token);
        return TypeRegister.simple(type, token);
    }

    /**
     * 函数功能：解析数组类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister arrayType(ShiftReduceSemanticContext context, HeadNode head) {
        TypeRegister base = requireChild(context, head, 0);
        return TypeRegister.simple(
                base.requireType("array type declaration requires a base type.").withAppendedDimension(),
                childAnchor(head, 0)
        );
    }

    /**
     * 函数功能：解析命名结构体类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister namedStructType(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = childAnchor(head, 0);
        return TypeRegister.simple(SemanticType.struct(token), token);
    }

    /**
     * 函数功能：绑定数组创建的基础类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister arrayCreationBase(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = childAnchor(head, 0);
        if (head.containsTag(ProgramSemanticTag.STRUCT_TYPE)) {
            return TypeRegister.simple(SemanticType.struct(token), token);
        }
        return TypeRegister.simple(context.typeToken(token), token);
    }

    /**
     * 函数功能：校验数组创建维度值。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister arrayCreationDimensionValue(ShiftReduceSemanticContext context, HeadNode head) {
        return requireChild(context, head, 1);
    }

    /**
     * 函数功能：处理赋值表达式类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister assignment(ShiftReduceSemanticContext context, HeadNode head) {
        return requireChild(context, head, 0);
    }

    /**
     * 函数功能：解析标识符引用类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister identifierReference(ShiftReduceSemanticContext context, HeadNode head) {
        return identifierReference(context, childAnchor(head, 0));
    }

    /**
     * 函数功能：解析标识符引用类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister identifierReference(ShiftReduceSemanticContext context, SourceToken token) {
        var record = context.getIdentifier(token);
        if (record == null) {
            throw new CompilerException("identifier is not declared in current visible scopes.");
        }
        return TypeRegister.located(record.getDeclaredType(), record.getDeclaredType(), token, LocationKind.ADDRESS);
    }

    /**
     * 函数功能：解析数组元素访问类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
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
        return TypeRegister.located(
                baseType.arrayElementType(),
                baseType.arrayElementType(),
                childAnchor(head, 0),
                LocationKind.REFERENCE
        );
    }

    /**
     * 函数功能：解析成员访问类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
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

    /**
     * 函数功能：解析函数调用类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister functionCall(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = childAnchor(head, 0);
        var function = context.getFunction(token);
        if (function == null) {
            throw new CompilerException("function is not declared in current visible scope.");
        }
        return TypeRegister.simple(function.getSignature().getReturnType(), token);
    }

    /**
     * 函数功能：生成新建结构体命令。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister newStruct(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = childAnchor(head, 1);
        SemanticType type = SemanticType.struct(token);
        context.requireDeclaredType(type, token, "struct type is not declared.");
        return TypeRegister.simple(type, token);
    }

    /**
     * 函数功能：生成新建数组命令。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
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

    /**
     * 函数功能：处理布尔二元表达式类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
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

    /**
     * 函数功能：处理相等性表达式。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
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

    /**
     * 函数功能：处理关系表达式。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
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

    /**
     * 函数功能：处理数值二元表达式类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
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

    /**
     * 函数功能：处理布尔一元表达式。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister unaryBoolean(ShiftReduceSemanticContext context, HeadNode head) {
        SemanticType operandType = requireChild(context, head, 1)
                .requireType("operator '!' requires a typed operand.");
        if (operandType.isBooleanScalar()) {
            return TypeRegister.simple(SemanticType.scalar(SemanticType.Kind.BOOLEAN), childAnchor(head, 0));
        }
        throw new CompilerException("operator '!' requires a boolean operand.");
    }

    /**
     * 函数功能：处理数值一元表达式。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
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

    /**
     * 函数功能：处理 void 函数头。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister voidFunctionHead(
            ShiftReduceSemanticContext context,
            HeadNode head,
            SimpleGrammarProduction production) {
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

    /**
     * 函数功能：获取当前规约头节点。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * 输出：HeadNode 类型返回值。
     */
    private static HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new IllegalStateException("current reduced head is not available");
        }
        return context.getTreeContext().peek().toHead();
    }

    /**
     * 函数功能：获取指定子节点。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * - index：int 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister child(ShiftReduceSemanticContext context, HeadNode head, int index) {
        return context.getType(head.get(index));
    }

    /**
     * 函数功能：获取并校验指定子节点。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * - index：int 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister requireChild(ShiftReduceSemanticContext context, HeadNode head, int index) {
        TypeRegister register = child(context, head, index);
        if (register == null) {
            throw new CompilerException("semantic type is absent for child #" + index);
        }
        return register;
    }

    /**
     * 函数功能：获取子节点锚点。
     * 输入：
     * - head：HeadNode 类型参数。
     * - index：int 类型参数。
     * 输出：SourceToken 类型返回值。
     */
    private static SourceToken childAnchor(HeadNode head, int index) {
        return ShiftReduceSyntaxTreeNode.anchor(head.get(index));
    }

    /**
     * 函数功能：创建带锚点的语义类型。
     * 输入：
     * - register：TypeRegister 类型参数。
     * - anchor：SourceToken 类型参数。
     * - locationKind：LocationKind 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    private static TypeRegister withAnchor(TypeRegister register, SourceToken anchor, LocationKind locationKind) {
        return new TypeRegister(register.getType(), register.getInstructionType(), anchor, locationKind);
    }

    /**
     * 函数功能：处理规约事件。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：无。
     */
    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        HeadNode head = currentReducedHead(context);
        TypeRegister result = RULES.resolve(production).build(context, head, production);
        if (result != null) {
            context.bindType(head, result);
        }
        ShiftReduceCallback.super.onReduce(context, production);
    }

    /**
     * 单个产生式对应的类型构造规则。
     * <p>
     * 作用：
     * <p>
     * TypeRule 是一个函数式接口。
     * RULES 表根据 production tag 选出具体 TypeRule，
     * 再调用 build 方法完成当前 reduce 的类型构造。
     */
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
        TypeRule ARRAY_CREATION_DIMENSION_VALUE = (context, head, production) -> arrayCreationDimensionValue(
                context,
                head
        );
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

        /**
         * 函数功能：构建目标对象。
         * 输入：
         * - context：ShiftReduceSemanticContext 类型参数。
         * - head：HeadNode 类型参数。
         * - production：SimpleGrammarProduction 类型参数。
         * 输出：TypeRegister 类型返回值。
         */
        TypeRegister build(ShiftReduceSemanticContext context, HeadNode head, SimpleGrammarProduction production);
    }
}


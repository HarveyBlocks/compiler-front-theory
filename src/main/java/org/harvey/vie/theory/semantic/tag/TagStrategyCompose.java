package org.harvey.vie.theory.semantic.tag;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.semantic.command.translator.CommandTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.command.*;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorCategory;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-25 11:00
 */
public class TagStrategyCompose {
    /**
     * 函数功能：创建字符串命令标签策略。
     * 输入：
     * - 无。
     * 输出：ProductionTagStrategy<CommandTranslator> 类型返回值。
     */
    public static ProductionTagStrategy<CommandTranslator> stringCommand() {
        CommandTranslator doNothing = new DoNotingTranslator();
        CommandTranslator simpleShrink = new SimpleShrinkTranslator();
        CommandTranslator programTranslator = new ProgramCommandTranslator();
        CommandTranslator functionDefinitionTranslator = new FunctionDefinitionTranslator();
        CommandTranslator functionHeadTranslator = new FunctionHeadTranslator();
        CommandTranslator returnTranslator = new FunctionReturnTranslator();
        CommandTranslator declarationWithoutInitializationTranslator =
                new DeclarationWithoutInitializationTranslator();
        CommandTranslator declarationWithInitializationTranslator =
                new DeclarationWithInitializationTranslator();
        CommandTranslator arrayTypeTranslator = new ArrayTypeTranslator();
        CommandTranslator identifierUseTranslator = new IdentifierUseTranslator();
        CommandTranslator parenthesizedExpressionTranslator = new ParenthesizedExpressionTranslator();
        CommandTranslator primaryProduceLeftValueTranslator = new PrimaryProduceLeftValueTranslator();
        CommandTranslator assignStatementTranslator = new AssignStatementTranslator();
        CommandTranslator arrayAtExpressionTranslator = new ArrayAtExpressionTranslator();
        CommandTranslator memberAccessTranslator = new MemberAccessTranslator();
        CommandTranslator newStructTranslator = new NewStructTranslator();
        CommandTranslator newArrayTranslator = new NewArrayTranslator();
        CommandTranslator functionCallTranslator = new FunctionCallTranslator();
        CommandTranslator ifStatementTranslator = new IfStatementTranslator();
        CommandTranslator ifElseStatementTranslator = new IfElseStatementTranslator();
        CommandTranslator whileStatementTranslator = new WhileStatementTranslator();
        CommandTranslator doWhileStatementTranslator = new DoWhileStatementTranslator();
        CommandTranslator statementListTranslator = new StatementListTranslator();
        CommandTranslator logicalNotTranslator =
                new UnaryExpressionTranslator(operator("logical_not", OperatorCategory.UNARY), ProgramTokenType.OPERATOR_LOGICAL_NOT);
        CommandTranslator negateTranslator =
                new UnaryExpressionTranslator(operator("negate", OperatorCategory.UNARY), ProgramTokenType.OPERATOR_MINUS);
        CommandTranslator logicalOrTranslator = new InSuffixExpressionTranslator(operator("logical_or", OperatorCategory.LOGICAL));
        CommandTranslator logicalAndTranslator = new InSuffixExpressionTranslator(operator("logical_and", OperatorCategory.LOGICAL));
        CommandTranslator equalTranslator = new InSuffixExpressionTranslator(operator("equal", OperatorCategory.EQUALITY));
        CommandTranslator notEqualTranslator = new InSuffixExpressionTranslator(operator("not_equal", OperatorCategory.EQUALITY));
        CommandTranslator lessTranslator = new InSuffixExpressionTranslator(operator("less", OperatorCategory.RELATIONAL));
        CommandTranslator lessEqualTranslator = new InSuffixExpressionTranslator(operator("less_equal", OperatorCategory.RELATIONAL));
        CommandTranslator greaterTranslator = new InSuffixExpressionTranslator(operator("greater", OperatorCategory.RELATIONAL));
        CommandTranslator greaterEqualTranslator = new InSuffixExpressionTranslator(operator("greater_equal", OperatorCategory.RELATIONAL));
        CommandTranslator plusTranslator = new InSuffixExpressionTranslator(operator("plus", OperatorCategory.ARITHMETIC));
        CommandTranslator minusTranslator = new InSuffixExpressionTranslator(operator("minus", OperatorCategory.ARITHMETIC));
        CommandTranslator multiplyTranslator = new InSuffixExpressionTranslator(operator("multiply", OperatorCategory.ARITHMETIC));
        CommandTranslator divideTranslator = new InSuffixExpressionTranslator(operator("divide", OperatorCategory.ARITHMETIC));

        return new ProductionTagStrategy<>(simpleShrink)
                .when(programTranslator, ProgramSemanticTag.PROGRAM)
                .when(functionDefinitionTranslator, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.DEFINITION)
                .when(functionHeadTranslator, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.HEAD)
                .when(doNothing, ProgramSemanticTag.BLOCK, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
                .when(
                        statementListTranslator,
                        ProgramSemanticTag.BLOCK,
                        ProgramSemanticTag.LIST,
                        ProgramSemanticTag.SEQUENCE
                )
                .when(doNothing, ProgramSemanticTag.PARAMETER, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
                .when(doNothing, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
                .when(
                        statementListTranslator,
                        ProgramSemanticTag.ARGUMENT,
                        ProgramSemanticTag.LIST,
                        ProgramSemanticTag.SEQUENCE
                )
                .when(
                        declarationWithInitializationTranslator,
                        ProgramSemanticTag.DECLARATION,
                        ProgramSemanticTag.INITIALIZED
                )
                .when(declarationWithoutInitializationTranslator, ProgramSemanticTag.DECLARATION)
                .when(returnTranslator, ProgramSemanticTag.RETURN)
                .when(functionCallTranslator, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.CALL)
                .when(newStructTranslator, ProgramSemanticTag.NEW_STRUCT)
                .when(newArrayTranslator, ProgramSemanticTag.NEW_ARRAY)
                .when(arrayTypeTranslator, ProgramSemanticTag.TYPE, ProgramSemanticTag.ARRAY)
                .when(identifierUseTranslator, ProgramSemanticTag.IDENTIFIER, ProgramSemanticTag.USE)
                .when(parenthesizedExpressionTranslator, ProgramSemanticTag.PARENTHESIZED)
                .when(primaryProduceLeftValueTranslator, ProgramSemanticTag.LEFT_VALUE)
                .when(logicalNotTranslator, ProgramSemanticTag.LOGICAL_NOT)
                .when(negateTranslator, ProgramSemanticTag.NEGATE)
                .when(assignStatementTranslator, ProgramSemanticTag.ASSIGNMENT)
                .when(arrayAtExpressionTranslator, ProgramSemanticTag.ACCESS)
                .when(memberAccessTranslator, ProgramSemanticTag.MEMBER_ACCESS)
                .when(logicalOrTranslator, ProgramSemanticTag.OR)
                .when(logicalAndTranslator, ProgramSemanticTag.AND)
                .when(notEqualTranslator, ProgramSemanticTag.NOT_EQUAL)
                .when(equalTranslator, ProgramSemanticTag.EQUAL)
                .when(lessEqualTranslator, ProgramSemanticTag.LESS_EQUAL)
                .when(lessTranslator, ProgramSemanticTag.LESS)
                .when(greaterEqualTranslator, ProgramSemanticTag.GREATER_EQUAL)
                .when(greaterTranslator, ProgramSemanticTag.GREATER)
                .when(plusTranslator, ProgramSemanticTag.PLUS)
                .when(minusTranslator, ProgramSemanticTag.MINUS)
                .when(multiplyTranslator, ProgramSemanticTag.MULTIPLY)
                .when(divideTranslator, ProgramSemanticTag.DIVIDE)
                .when(ifElseStatementTranslator, ProgramSemanticTag.CONDITIONAL, ProgramSemanticTag.ELSE_BRANCH)
                .when(ifStatementTranslator, ProgramSemanticTag.CONDITIONAL)
                .when(doWhileStatementTranslator, ProgramSemanticTag.LOOP, ProgramSemanticTag.DO_LOOP)
                .when(whileStatementTranslator, ProgramSemanticTag.LOOP);
    }
/**
 * 函数功能：创建精确字符串命令标签策略。
 * 输入：
 * - 无。
 * 输出：CommandTranslatorStrategy 类型返回值。
 */

    public static CommandTranslatorStrategy preciseStringCommand() {
        ProductionTagStrategy<CommandTranslator> strategy = stringCommand();
        return production -> strategy.resolve(production);
    }
/**
 * 函数功能：创建运算符标签策略。
 * 输入：
 * - mnemonic：String 类型参数。
 * - category：OperatorCategory 类型参数。
 * 输出：OperatorFactor 类型返回值。
 */

    private static OperatorFactor operator(String mnemonic, OperatorCategory category) {
        return new OperatorFactor() {
            /**
             * 函数功能：获取操作符助记符。
             * 输入：
             * - 无。
             * 输出：字符串结果。
             */
            @Override
            public String mnemonic() {
                return mnemonic;
            }
/**
 * 函数功能：获取操作符类别。
 * 输入：
 * - 无。
 * 输出：OperatorCategory 类型返回值。
 */

            @Override
            public OperatorCategory category() {
                return category;
            }
        };
    }
}

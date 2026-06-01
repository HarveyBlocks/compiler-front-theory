package org.harvey.vie.theory.semantic.tag;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.semantic.command.CommandBuildCallback;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.translator.CommandTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.command.*;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorCategory;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 讲解主线第 2 站：把“产生式上的语义标签”映射成真正负责生成命令的 {@link CommandTranslator}。
 * <p>
 * {@link CommandBuildCallback} 在规约时只知道当前产生式 {@link SimpleGrammarProduction}，它会调用
 * {@link #preciseStringCommand()} 拿到的策略；本类再根据 {@link ProgramSemanticTag} 选择具体翻译器。
 * 所以这张映射表就是“语法制导翻译规则表”：声明、赋值、表达式、if/while/do-while、函数、数组、结构体
 * 都从这里进入各自的翻译支路。
 * <p>
 * 讲解路线建议：
 * 先看顶层 {@link ProgramCommandTranslator}，再看通用拼接 {@link SimpleShrinkTranslator} 与
 * {@link StatementListTranslator}；表达式支路看 {@link InSuffixExpressionTranslator} 和
 * {@link UnaryExpressionTranslator}；控制流支路看 {@link IfStatementTranslator}、
 * {@link IfElseStatementTranslator}、{@link WhileStatementTranslator}、{@link DoWhileStatementTranslator}。
 * 支路讲完都回到 {@link CommandNodeRegister}，因为所有翻译器最终都返回注册器。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-25 11:00
 */
public class TagStrategyCompose {
    /**
     * 构造“语义标签 -> 命令翻译器”的完整映射表。
     * <p>
     * 默认值是 {@link SimpleShrinkTranslator}，表示没有特殊规则的产生式只把子节点命令顺序拼起来；
     * 带有关键语义的产生式会被替换成更专门的翻译器。例如 {@link ProgramSemanticTag#ASSIGNMENT}
     * 进入 {@link AssignStatementTranslator}，{@link ProgramSemanticTag#LOOP} 进入循环翻译器。
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
     * 提供给 {@link CommandBuildCallback} 的规约策略入口。
     * <p>
     * {@link ProductionTagStrategy#resolve(SimpleGrammarProduction)} 会按当前产生式携带的标签精确匹配上面的规则；
     * 找不到专用规则时回退到 {@link SimpleShrinkTranslator}。
     */
    public static CommandTranslatorStrategy preciseStringCommand() {
        ProductionTagStrategy<CommandTranslator> strategy = stringCommand();
        return production -> strategy.resolve(production);
    }

    private static OperatorFactor operator(String mnemonic, OperatorCategory category) {
        return new OperatorFactor() {
            @Override
            public String mnemonic() {
                return mnemonic;
            }

            @Override
            public OperatorCategory category() {
                return category;
            }
        };
    }
}

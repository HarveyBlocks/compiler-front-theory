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
 * 讲解主线第 2 站：语义动作分发表，也就是“产生式标签 -> 命令翻译器”的映射表。
 * <p>
 * {@link CommandBuildCallback} 在规约时拿到的是 {@link SimpleGrammarProduction}。在编译原理术语里，
 * 这个产生式代表“刚刚规约出了某个非终结符”，而产生式上的 {@link ProgramSemanticTag}
 * 就相当于给语义动作做分类。本类根据这些标签选择具体的 {@link CommandTranslator}。
 * <p>
 * 可以把本类理解成一张语法制导翻译规则表：声明进入 {@link DeclarationWithInitializationTranslator}
 * 或 {@link DeclarationWithoutInitializationTranslator}，赋值进入 {@link AssignStatementTranslator}，
 * 表达式进入 {@link InSuffixExpressionTranslator}/{@link UnaryExpressionTranslator}，
 * 控制流进入 {@link IfStatementTranslator}/{@link IfElseStatementTranslator}/
 * {@link WhileStatementTranslator}/{@link DoWhileStatementTranslator}。
 * <p>
 * 主线下一站：{@link ProgramCommandTranslator}。下一站会讲最外层 program 规约后如何兜底检查
 * {@code break}/{@code continue}，并把支路最终带回 {@link CommandNodeRegister}。
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
     * {@link ProductionTagStrategy#resolve(Object)} 会按当前产生式携带的标签精确匹配上面的规则；
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

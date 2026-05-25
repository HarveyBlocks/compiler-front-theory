package org.harvey.vie.theory.semantic.tag;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.semantic.command.translator.command.*;
import org.harvey.vie.theory.semantic.command.translator.CommandTranslatorStrategy;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-25 11:00
 */
public class TagStrategyCompose {
    public static ProductionTagStrategy<CommandTranslator> stringCommand(){
        CommandTranslator doNothing = new DoNotingTranslator();
        CommandTranslator simpleShrink =  new SimpleShrinkTranslator();
        CommandTranslator programTranslator = new ProgramCommandTranslator();
        CommandTranslator functionHeadTranslator = new FunctionHeadTranslator();
        CommandTranslator returnTranslator = new FunctionReturnTranslator();
        CommandTranslator declarationWithoutInitializationTranslator =
                new DeclarationWithoutInitializationTranslator();
        CommandTranslator declarationWithInitializationTranslator =
                new DeclarationWithInitializationTranslator();
        CommandTranslator arrayTypeTranslator = new ArrayTypeTranslator();
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
                new UnaryExpressionTranslator(operator("logical_not"), ProgramTokenType.OPERATOR_LOGICAL_NOT);
        CommandTranslator negateTranslator =
                new UnaryExpressionTranslator(operator("negate"), ProgramTokenType.OPERATOR_MINUS);
        CommandTranslator logicalOrTranslator = new InSuffixExpressionTranslator(operator("logical_or"));
        CommandTranslator logicalAndTranslator = new InSuffixExpressionTranslator(operator("logical_and"));
        CommandTranslator equalTranslator = new InSuffixExpressionTranslator(operator("equal"));
        CommandTranslator notEqualTranslator = new InSuffixExpressionTranslator(operator("not_equal"));
        CommandTranslator lessTranslator = new InSuffixExpressionTranslator(operator("less"));
        CommandTranslator lessEqualTranslator = new InSuffixExpressionTranslator(operator("less_equal"));
        CommandTranslator greaterTranslator = new InSuffixExpressionTranslator(operator("greater"));
        CommandTranslator greaterEqualTranslator = new InSuffixExpressionTranslator(operator("greater_equal"));
        CommandTranslator plusTranslator = new InSuffixExpressionTranslator(operator("plus"));
        CommandTranslator minusTranslator = new InSuffixExpressionTranslator(operator("minus"));
        CommandTranslator multiplyTranslator = new InSuffixExpressionTranslator(operator("multiply"));
        CommandTranslator divideTranslator = new InSuffixExpressionTranslator(operator("divide"));

        return new ProductionTagStrategy<>(simpleShrink)
                .when(programTranslator, ProgramSemanticTag.PROGRAM)
                .when(functionHeadTranslator, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.HEAD)
                .when(doNothing, ProgramSemanticTag.BLOCK, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
                .when(statementListTranslator, ProgramSemanticTag.BLOCK, ProgramSemanticTag.LIST, ProgramSemanticTag.SEQUENCE)
                .when(doNothing, ProgramSemanticTag.PARAMETER, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
                .when(doNothing, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
                .when(statementListTranslator, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.LIST, ProgramSemanticTag.SEQUENCE)
                .when(declarationWithInitializationTranslator, ProgramSemanticTag.DECLARATION, ProgramSemanticTag.INITIALIZED)
                .when(declarationWithoutInitializationTranslator, ProgramSemanticTag.DECLARATION)
                .when(returnTranslator, ProgramSemanticTag.RETURN)
                .when(functionCallTranslator, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.CALL)
                .when(newStructTranslator, ProgramSemanticTag.NEW_STRUCT)
                .when(newArrayTranslator, ProgramSemanticTag.NEW_ARRAY)
                .when(arrayTypeTranslator, ProgramSemanticTag.TYPE, ProgramSemanticTag.ARRAY)
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

    public static CommandTranslatorStrategy preciseStringCommand() {
        CommandTranslator doNothing = new DoNotingTranslator();
        CommandTranslator simpleShrink = new SimpleShrinkTranslator();
        CommandTranslator primaryProduceLeftValueTranslator = new PrimaryProduceLeftValueTranslator();
        CommandTranslator arrayAtExpressionTranslator = new ArrayAtExpressionTranslator();
        CommandTranslator memberAccessTranslator = new MemberAccessTranslator();
        CommandTranslator parenthesizedExpressionTranslator = new ParenthesizedExpressionTranslator();
        Map<String, CommandTranslator> exact = new HashMap<>();
        exact.put("program", new ProgramCommandTranslator());
        exact.put("function_head", new FunctionHeadTranslator());
        exact.put("decl_plain", new DeclarationWithoutInitializationTranslator());
        exact.put("decl_init", new DeclarationWithInitializationTranslator());
        exact.put("return_stmt", new FunctionReturnTranslator());
        exact.put("call_expr", new FunctionCallTranslator());
        exact.put("new_struct_expr", new NewStructTranslator());
        exact.put("new_array_expr", new NewArrayTranslator());
        exact.put("type", new ArrayTypeTranslator());
        exact.put("factor", parenthesizedExpressionTranslator);
        exact.put("assign_stmt", new AssignStatementTranslator());
        exact.put("loc", memberAccessTranslator);
        exact.put("matched_if_stmt", new IfElseStatementTranslator());
        exact.put("unmatched_if_stmt", new IfStatementTranslator());
        exact.put("do_while_stmt", new DoWhileStatementTranslator());
        exact.put("matched_while_stmt", new WhileStatementTranslator());
        exact.put("unmatched_while_stmt", new WhileStatementTranslator());
        exact.put("block_items", new StatementListTranslator());
        exact.put("args", new StatementListTranslator());
        exact.put("break_stmt", simpleShrink);
        exact.put("continue_stmt", simpleShrink);
        exact.put("param_list", doNothing);
        exact.put("arg_list", doNothing);
        return production -> {
            String defineName = production.getHead().isDefine() ? production.getHead().toDefine().getName() : "";
            CommandTranslator translator = exact.get(defineName);
            if (translator != null) {
                if ("type".equals(defineName) && !production.containsTag(ProgramSemanticTag.ARRAY)) {
                    return simpleShrink;
                }
                if ("factor".equals(defineName)) {
                    if (production.containsTag(ProgramSemanticTag.PARENTHESIZED)) {
                        return exact.get("factor");
                    }
                    if (production.containsTag(ProgramSemanticTag.LEFT_VALUE)) {
                        return primaryProduceLeftValueTranslator;
                    }
                    return simpleShrink;
                }
                if ("loc".equals(defineName)) {
                    if (production.containsTag(ProgramSemanticTag.ACCESS)) {
                        return arrayAtExpressionTranslator;
                    }
                    if (production.containsTag(ProgramSemanticTag.MEMBER_ACCESS)) {
                        return exact.get("loc");
                    }
                    if (production.containsTag(ProgramSemanticTag.IDENTIFIER)) {
                        return primaryProduceLeftValueTranslator;
                    }
                    return simpleShrink;
                }
                if ("block_items".equals(defineName) && production.containsTag(ProgramSemanticTag.EMPTY)) {
                    return doNothing;
                }
                if ("args".equals(defineName) && production.containsTag(ProgramSemanticTag.FORWARD)) {
                    return simpleShrink;
                }
                if ("args".equals(defineName) && production.containsTag(ProgramSemanticTag.SEQUENCE)) {
                    return exact.get("args");
                }
                return translator;
            }
            if (production.containsTag(ProgramSemanticTag.LOGICAL_NOT)) {
                return new UnaryExpressionTranslator(operator("logical_not"), ProgramTokenType.OPERATOR_LOGICAL_NOT);
            }
            if (production.containsTag(ProgramSemanticTag.NEGATE)) {
                return new UnaryExpressionTranslator(operator("negate"), ProgramTokenType.OPERATOR_MINUS);
            }
            if (production.containsTag(ProgramSemanticTag.OR)) {
                return new InSuffixExpressionTranslator(operator("logical_or"));
            }
            if (production.containsTag(ProgramSemanticTag.AND)) {
                return new InSuffixExpressionTranslator(operator("logical_and"));
            }
            if (production.containsTag(ProgramSemanticTag.NOT_EQUAL)) {
                return new InSuffixExpressionTranslator(operator("not_equal"));
            }
            if (production.containsTag(ProgramSemanticTag.EQUAL)) {
                return new InSuffixExpressionTranslator(operator("equal"));
            }
            if (production.containsTag(ProgramSemanticTag.LESS_EQUAL)) {
                return new InSuffixExpressionTranslator(operator("less_equal"));
            }
            if (production.containsTag(ProgramSemanticTag.LESS)) {
                return new InSuffixExpressionTranslator(operator("less"));
            }
            if (production.containsTag(ProgramSemanticTag.GREATER_EQUAL)) {
                return new InSuffixExpressionTranslator(operator("greater_equal"));
            }
            if (production.containsTag(ProgramSemanticTag.GREATER)) {
                return new InSuffixExpressionTranslator(operator("greater"));
            }
            if (production.containsTag(ProgramSemanticTag.PLUS)) {
                return new InSuffixExpressionTranslator(operator("plus"));
            }
            if (production.containsTag(ProgramSemanticTag.MINUS)) {
                return new InSuffixExpressionTranslator(operator("minus"));
            }
            if (production.containsTag(ProgramSemanticTag.MULTIPLY)) {
                return new InSuffixExpressionTranslator(operator("multiply"));
            }
            if (production.containsTag(ProgramSemanticTag.DIVIDE)) {
                return new InSuffixExpressionTranslator(operator("divide"));
            }
            return simpleShrink;
        };
    }

    private static OperatorFactor operator(String name) {
        return new OperatorFactor() {
            @Override
            public String toString() {
                return name;
            }
        };
    }

}

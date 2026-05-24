package org.harvey.vie.theory.demo;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.demo.semantic.callable.TreeBuilderPredictiveCallback;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.semantic.callback.bu.ReducePredicate;
import org.harvey.vie.theory.semantic.callback.bu.ShiftPredicate;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallbackRegister;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallbackRegisterImpl;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegister;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegisterImpl;
import org.harvey.vie.theory.semantic.command.CommandBuildCallback;
import org.harvey.vie.theory.semantic.command.SemanticCommandPrintCallback;
import org.harvey.vie.theory.semantic.command.SemanticResultCallback;
import org.harvey.vie.theory.semantic.command.translator.CommandTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.TokenTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.command.ArrayAtExpressionTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.ArrayTypeTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.AssignStatementTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.CommandTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.DeclarationWithInitializationTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.DeclarationWithoutInitializationTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.DoNotingTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.DoWhileStatementTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.FunctionCallTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.FunctionHeadTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.FunctionReturnTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.IfElseStatementTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.IfStatementTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.InSuffixExpressionTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.command.translator.command.ParenthesizedExpressionTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.PrimaryProduceLeftValueTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.ProgramCommandTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.SimpleShrinkTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.StatementListTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.UnaryExpressionTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.WhileStatementTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.BreakTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.ContinueTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.DoNothingTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.LoadIdentifierReferenceTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.SimpleStringTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.TokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.TypeTokenTranslator;
import org.harvey.vie.theory.semantic.error.PassiveErrorCallback;
import org.harvey.vie.theory.semantic.function.FunctionSemanticCallback;
import org.harvey.vie.theory.semantic.identifier.IdentifierScopeCallback;
import org.harvey.vie.theory.semantic.identifier.IdentifierTableBuildCallback;
import org.harvey.vie.theory.semantic.log.TreeLogCallback;
import org.harvey.vie.theory.semantic.tree.TreeBuildCallback;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tag.ProductionTagStrategy;
import org.harvey.vie.theory.semantic.tag.TagReducePredicateFactory;
import org.harvey.vie.theory.semantic.type.TypeBuildCallback;
import org.harvey.vie.theory.semantic.value.ConstantValueBuildCallback;
import org.harvey.vie.theory.semantic.value.IdentifierConstantStateCallback;
import org.harvey.vie.theory.syntax.td.conflict.LexicalConflictResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Temper
 */
public class SemanticDemo {
    public static ShiftReduceCallbackRegister buildSimpleShiftReduceRegister() {
        ShiftReduceCallbackRegister register = new ShiftReduceCallbackRegisterImpl();
        register.add(new TreeBuildCallback());
        register.add(new TreeLogCallback());
        register.add(new PassiveErrorCallback());
        return register;
    }

    public static ShiftReduceCallbackRegister buildShiftReduceRegister() {
        ShiftReduceCallbackRegister register = new ShiftReduceCallbackRegisterImpl();
        register.add(new TreeBuildCallback());
        register.add(new TreeLogCallback());
        register.add(instanceIdentifierScopeCallback());
        register.add(new TypeBuildCallback());
        register.add(new ConstantValueBuildCallback());
        register.add(new FunctionSemanticCallback());
        register.add(instanceSemanticCommandPrintCallback());
        register.add(instanceSyntaxDirectedTranslationCallback());
        register.add(instanceIdentifierTableBuildCallback());
        register.add(new IdentifierConstantStateCallback());
        register.add(new PassiveErrorCallback());
        return register;
    }

    public static ShiftReduceCallbackRegister buildShiftReduceTestRegister() {
        ShiftReduceCallbackRegister register = new ShiftReduceCallbackRegisterImpl();
        register.add(new TreeBuildCallback());
        register.add(instanceIdentifierScopeCallback());
        register.add(new TypeBuildCallback());
        register.add(new ConstantValueBuildCallback());
        register.add(new FunctionSemanticCallback());
        register.add(new SemanticResultCallback());
        register.add(instanceSyntaxDirectedTranslationCallback());
        register.add(instanceIdentifierTableBuildCallback());
        register.add(new IdentifierConstantStateCallback());
        register.add(new PassiveErrorCallback());
        return register;
    }

    static final TokenTranslator defaultTokenTranslator = new DoNothingTokenTranslator();
    static final CommandTranslator defaultCommandTranslator = new SimpleShrinkTranslator();

    private static ShiftReduceCallback instanceSyntaxDirectedTranslationCallback() {
        return new CommandBuildCallback(shiftStrategies(), reduceStrategies0());
    }

    private static TokenTranslatorStrategy shiftStrategies() {
        Map<TokenType, TokenTranslator> shiftStrategies = new HashMap<>();
        TokenTranslator loadIdentifierReferenceTokenTranslator = new LoadIdentifierReferenceTokenTranslator();
        shiftStrategies.put(ProgramTokenType.IDENTIFIER, loadIdentifierReferenceTokenTranslator);
        TokenTranslator simpleStringTokenTranslator = new SimpleStringTokenTranslator();
        shiftStrategies.put(ProgramTokenType.CONSTANT_STRING, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_CHARACTER, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_INTEGER, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_FLOAT, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_BOOLEAN_TRUE, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_BOOLEAN_FALSE, simpleStringTokenTranslator);
        TokenTranslator typeTokenTranslator = new TypeTokenTranslator();
        shiftStrategies.put(ProgramTokenType.TYPE_BOOLEAN, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_CHARACTER, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_INT32, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_FLOAT64, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_STRING, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_BREAK, new BreakTokenTranslator());
        shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_CONTINUE, new ContinueTokenTranslator());
        return t -> shiftStrategies.getOrDefault(t, defaultTokenTranslator);
    }

    private static CommandTranslatorStrategy reduceStrategies0() {
        CommandTranslator doNothing = new DoNotingTranslator();
        CommandTranslator simpleShrink = defaultCommandTranslator;
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

        ProductionTagStrategy<CommandTranslator> strategy = new ProductionTagStrategy<>(defaultCommandTranslator)
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
                .when(arrayTypeTranslator, ProgramSemanticTag.TYPE, ProgramSemanticTag.ARRAY)
                .when(parenthesizedExpressionTranslator, ProgramSemanticTag.PARENTHESIZED)
                .when(primaryProduceLeftValueTranslator, ProgramSemanticTag.LEFT_VALUE)
                .when(logicalNotTranslator, ProgramSemanticTag.LOGICAL_NOT)
                .when(negateTranslator, ProgramSemanticTag.NEGATE)
                .when(assignStatementTranslator, ProgramSemanticTag.ASSIGNMENT)
                .when(arrayAtExpressionTranslator, ProgramSemanticTag.ACCESS)
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
        return strategy::resolve;
    }

    private static OperatorFactor operator(String name) {
        return new OperatorFactor() {
            @Override
            public String toString() {
                return name;
            }
        };
    }

    private static ShiftReduceCallback instanceSemanticCommandPrintCallback() {
        return new SemanticCommandPrintCallback();
    }

    private static ShiftReduceCallback instanceIdentifierScopeCallback() {
        ShiftPredicate scopeIntoPredicate = t -> t.getType() == ProgramTokenType.OPERATOR_BRACE_OPEN;
        ReducePredicate scopeExistPredicate = TagReducePredicateFactory.predicate(
                ProgramSemanticTag.BLOCK,
                ProgramSemanticTag.COMMAND
        );
        return new IdentifierScopeCallback(scopeIntoPredicate, scopeExistPredicate);
    }

    private static ShiftReduceCallback instanceIdentifierTableBuildCallback() {
        ReducePredicate usingPredicate = TagReducePredicateFactory.predicate(
                ProgramSemanticTag.IDENTIFIER,
                ProgramSemanticTag.USE
        );
        ReducePredicate declaringPredicate = TagReducePredicateFactory.predicate(ProgramSemanticTag.DECLARATION);
        IdentifierTableBuildCallback.UsingIdentifierSupplier usingIdentifierSupplier =
                usingIdentifierReducedNode -> usingIdentifierReducedNode.get(0).toToken().getSource();

        IdentifierTableBuildCallback.DeclarationRecordSupplier declarationRecordSupplier =
                new IdentifierTableBuildCallback.DeclarationRecordSupplier() {
                    @Override
                    public SourceToken identifier(HeadNode declarationReducedNode) {
                        return declarationReducedNode.get(1).toToken().getSource();
                    }

                    @Override
                    public boolean initialized(HeadNode declarationReducedNode) {
                        return declarationReducedNode.containsTag(ProgramSemanticTag.INITIALIZED);
                    }

                    @Override
                    public HeadNode typeHeadNode(HeadNode declarationReducedNode) {
                        return declarationReducedNode.get(0).toHead();
                    }

                    @Override
                    public org.harvey.vie.theory.semantic.value.ConstantValue initializerValue(
                            org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext context,
                            HeadNode declarationReducedNode) {
                        if (!declarationReducedNode.containsTag(ProgramSemanticTag.INITIALIZED)) {
                            return null;
                        }
                        return context.getConstantValue(declarationReducedNode.get(3).toHead());
                    }
                };
        return new IdentifierTableBuildCallback(
                usingPredicate,
                declaringPredicate,
                usingIdentifierSupplier,
                declarationRecordSupplier
        );
    }

    public static PredictiveCallbackRegister buildPredicativeRegister() {
        TreeBuilderPredictiveCallback callback = new TreeBuilderPredictiveCallback(LexicalConflictResolver.passive());
        PredictiveCallbackRegister register = new PredictiveCallbackRegisterImpl();
        register.add(callback);
        return register;
    }
}


package org.harvey.vie.theory.demo;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.demo.semantic.callable.TreeBuilderPredictiveCallback;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.semantic.callback.bu.*;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegister;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegisterImpl;
import org.harvey.vie.theory.semantic.command.CommandBuildCallback;
import org.harvey.vie.theory.semantic.command.SemanticCommandPrintCallback;
import org.harvey.vie.theory.semantic.command.SemanticResultCallback;
import org.harvey.vie.theory.semantic.command.translator.CommandTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.TokenTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.command.CommandTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.SimpleShrinkTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.*;
import org.harvey.vie.theory.semantic.error.PassiveErrorCallback;
import org.harvey.vie.theory.semantic.function.FunctionSemanticCallback;
import org.harvey.vie.theory.semantic.identifier.IdentifierScopeCallback;
import org.harvey.vie.theory.semantic.identifier.IdentifierTableBuildCallback;
import org.harvey.vie.theory.semantic.log.TreeLogCallback;
import org.harvey.vie.theory.semantic.structure.StructSemanticCallback;
import org.harvey.vie.theory.semantic.tag.TagReducePredicateFactory;
import org.harvey.vie.theory.semantic.tag.TagStrategyCompose;
import org.harvey.vie.theory.semantic.tree.TreeBuildCallback;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
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
        register.add(new StructSemanticCallback());
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
        register.add(new StructSemanticCallback());
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
        TokenTranslator loadIdentifierAddressTokenTranslator = new LoadIdentifierAddressTokenTranslator();
        shiftStrategies.put(ProgramTokenType.IDENTIFIER, loadIdentifierAddressTokenTranslator);
        TokenTranslator simpleStringTokenTranslator = new SimpleStringTokenTranslator();
        shiftStrategies.put(ProgramTokenType.CONSTANT_STRING, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_CHARACTER, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_INTEGER, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_FLOAT, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_BOOLEAN_TRUE, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_BOOLEAN_FALSE, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_NULL, simpleStringTokenTranslator);
        TokenTranslator typeTokenTranslator = new TypeTokenTranslator();
        shiftStrategies.put(ProgramTokenType.TYPE_BOOLEAN, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_CHARACTER, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_INT32, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_FLOAT64, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_STRING, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_VOID, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_IDENTIFIER, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_BREAK, new BreakTokenTranslator());
        shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_CONTINUE, new ContinueTokenTranslator());
        return t -> shiftStrategies.getOrDefault(t, defaultTokenTranslator);
    }

    private static CommandTranslatorStrategy reduceStrategies0() {
        return TagStrategyCompose.preciseStringCommand();
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
        ReducePredicate declaringPredicate = production ->
                production.matchTags(ProgramSemanticTag.DECLARATION);
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


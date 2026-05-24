package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tag.ProductionTagStrategy;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.type.TypeRegister;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Temper
 */
public class FunctionSemanticCallback implements ShiftReduceCallback {
    private final ProductionTagStrategy<ReduceAction> reduceStrategy = new ProductionTagStrategy<>(ReduceAction.NOOP).when(
                    (context, head, production) -> prepareFunction(context, head),
                    ProgramSemanticTag.FUNCTION,
                    ProgramSemanticTag.HEAD
            )
            .when((context, head, production) -> validateReturnValue(context,
                    head,
                    production.containsTag(ProgramSemanticTag.VALUE)
            ), ProgramSemanticTag.RETURN)
            .when((context, head, production) -> validateCall(context, head),
                    ProgramSemanticTag.FUNCTION,
                    ProgramSemanticTag.CALL
            );

    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        onReduce0(context, production);
        ShiftReduceCallback.super.onReduce(context, production);
    }

    private void onReduce0(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            return;
        }
        HeadNode head = context.getTreeContext().peek().toHead();
        reduceStrategy.resolve(production).accept(context, head, production);
    }

    private void prepareFunction(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken nameToken = head.get(1).toToken().getSource();
        if (context.existFunction(nameToken)) {
            SemanticDiagnostics.reject(context, nameToken, "duplicate function declaration is not allowed.");
        }
        TypeRegister returnTypeRegister = context.getType(head.get(0));
        if (returnTypeRegister == null) {
            throw new CompilerException("function return type is missing.");
        }
        SemanticType returnType = returnTypeRegister.requireType("function return type is required.");
        List<FunctionParameter> parameters = collectParameters(context, head.get(3));
        FunctionRecord record = new FunctionRecord(new FunctionSignature(nameToken, returnType, head),
                parameters,
                head
        );
        context.registerFunction(record);
        context.markPendingFunction(record);
    }

    private void validateReturnValue(ShiftReduceSemanticContext context, HeadNode head, boolean hasValue) {
        SourceToken returnToken = head.get(0).toToken().getSource();
        if (!context.insideFunction()) {
            SemanticDiagnostics.reject(context, returnToken, "return is only allowed inside function body.");
        }
        SemanticType returnType = context.currentFunctionReturnType();
        if (returnType == null) {
            throw new CompilerException("current function return type is missing.");
        }
        if (!hasValue) {
            if (!returnType.isVoidScalar()) {
                SemanticDiagnostics.reject(context, returnToken, "non-void function must return a value.");
            }
            return;
        }
        if (returnType.isVoidScalar()) {
            SemanticDiagnostics.reject(context, returnToken, "void function cannot return a value.");
        }
        TypeRegister valueType = context.getType(head.get(1));
        if (valueType == null) {
            throw new CompilerException("return value type is missing.");
        }
        SemanticDiagnostics.requireAssignable(context,
                valueType.requireType("return value type is required."),
                returnType,
                returnToken,
                "return value type does not match function return type."
        );
    }

    private void validateCall(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken nameToken = head.get(0).toToken().getSource();
        FunctionRecord record = context.getFunction(nameToken);
        if (record == null) {
            SemanticDiagnostics.reject(context, nameToken, "function must be defined before it is called.");
            return;
        }
        List<TypeRegister> args = collectArgumentTypes(context, head.get(2));
        if (args.size() != record.getParameters().size()) {
            SemanticDiagnostics.reject(context, nameToken, "function argument count does not match.");
        }
        for (int i = 0; i < args.size(); i++) {
            SemanticType sourceType = args.get(i).requireType("argument type is required.");
            SemanticType targetType = record.getParameters().get(i).getType();
            SemanticDiagnostics.requireAssignable(context,
                    sourceType,
                    targetType,
                    nameToken,
                    "function argument type does not match parameter type."
            );
        }
    }

    private List<FunctionParameter> collectParameters(
            ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        List<FunctionParameter> result = new ArrayList<>();
        collectParameters0(context, node, result);
        return result;
    }

    private void collectParameters0(
            ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node, List<FunctionParameter> result) {
        if (!node.isHead()) {
            return;
        }
        HeadNode head = node.toHead();
        if (head.matchTags(ProgramSemanticTag.PARAMETER, ProgramSemanticTag.IDENTIFIER)) {
            TypeRegister register = context.getType(head.get(0));
            if (register == null) {
                throw new CompilerException("parameter type is missing.");
            }
            SemanticType type = register.requireType("parameter type is required.");
            SourceToken nameToken = head.get(1).toToken().getSource();
            SemanticDiagnostics.requireNotVoid(context, type, nameToken, "void cannot be used as parameter type.");
            for (FunctionParameter parameter : result) {
                if (parameter.isNamed(nameToken)) {
                    SemanticDiagnostics.reject(context,
                            nameToken,
                            "duplicate parameter declaration is not allowed."
                    );
                }
            }
            result.add(new FunctionParameter(nameToken, type, head.get(0).toHead()));
            return;
        }
        for (ShiftReduceSyntaxTreeNode child : head) {
            collectParameters0(context, child, result);
        }
    }

    private List<TypeRegister> collectArgumentTypes(
            ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        List<TypeRegister> result = new ArrayList<>();
        collectArgumentTypes0(context, node, result);
        return result;
    }

    private void collectArgumentTypes0(
            ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node, List<TypeRegister> result) {
        if (!node.isHead()) {
            return;
        }
        HeadNode head = node.toHead();
        if (head.matchTags(ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.VALUE)) {
            TypeRegister register = context.getType(head);
            if (register != null) {
                result.add(register);
            }
            return;
        }
        if (head.containsTag(ProgramSemanticTag.FORWARD)) {
            for (ShiftReduceSyntaxTreeNode child : head) {
                collectArgumentTypes0(context, child, result);
            }
        }
    }

    @FunctionalInterface
    private interface ReduceAction {
        ReduceAction NOOP = (context, head, production) -> {
        };

        void accept(ShiftReduceSemanticContext context, HeadNode head, SimpleGrammarProduction production);
    }
}


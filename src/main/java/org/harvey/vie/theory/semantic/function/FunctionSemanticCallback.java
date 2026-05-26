package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.sequence.SyntaxTreeListIterator;
import org.harvey.vie.theory.semantic.tag.ProductionTagStrategy;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.TypeRegister;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Temper
 */
public class FunctionSemanticCallback implements ShiftReduceCallback {
    private static final ProductionTagStrategy<ReduceAction> REDUCE_ACTIONS = new ProductionTagStrategy<>(ReduceAction.NOOP)
            .when(ReduceAction.PREPARE_FUNCTION, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.HEAD)
            .when(ReduceAction.VALIDATE_RETURN, ProgramSemanticTag.RETURN)
            .when(ReduceAction.VALIDATE_CALL, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.CALL);

    private final ArgumentStepper argumentStepper = new ArgumentStepper();
    private final ParameterStepper parameterStepper = new ParameterStepper();

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
        REDUCE_ACTIONS.resolve(production).accept(this, context, head);
    }

    private void prepareFunction(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken nameToken = tokenAt(head, 1);
        if (context.existFunction(nameToken)) {
            SemanticDiagnostics.reject(context, nameToken, "duplicate function declaration is not allowed.");
        }
        TypeRegister returnTypeRegister = resolveReturnType(context, head);
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
        SourceToken returnToken = tokenAt(head, 0);
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
        SourceToken nameToken = tokenAt(head, 0);
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
        SyntaxTreeListIterator<HeadNode> iterator = new SyntaxTreeListIterator<>(node, parameterStepper);
        while (iterator.hasNext()) {
            HeadNode head = iterator.next();
            TypeRegister register = context.getType(head.get(0));
            if (register == null) {
                throw new CompilerException("parameter type is missing.");
            }
            SemanticType type = register.requireType("parameter type is required.");
            SourceToken nameToken = tokenAt(head, 1);
            SemanticDiagnostics.requireNotVoid(context, type, nameToken, "void cannot be used as parameter type.");
            for (FunctionParameter parameter : result) {
                if (parameter.isNamed(nameToken)) {
                    SemanticDiagnostics.reject(context, nameToken, "duplicate parameter declaration is not allowed.");
                }
            }
            result.add(new FunctionParameter(nameToken, type, head.get(0).toHead()));
        }
        return result;
    }

    private static TypeRegister resolveReturnType(ShiftReduceSemanticContext context, HeadNode head) {
        TypeRegister direct = context.getType(head.get(0));
        if (direct != null) {
            return direct;
        }
        ShiftReduceSyntaxTreeNode first = head.get(0);
        if (first.isToken()) {
            SourceToken token = first.toToken().getSource();
            SemanticType type = context.typeToken(token);
            if (type != null) {
                return TypeRegister.simple(type, token);
            }
        }
        return null;
    }

    private static SourceToken tokenAt(HeadNode head, int index) {
        return ShiftReduceSyntaxTreeNode.anchor(head.get(index));
    }

    private List<TypeRegister> collectArgumentTypes(
            ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        List<TypeRegister> result = new ArrayList<>();
        SyntaxTreeListIterator<HeadNode> iterator = new SyntaxTreeListIterator<>(node, argumentStepper);
        while (iterator.hasNext()) {
            HeadNode head = iterator.next();
            TypeRegister register = context.getType(head);
            if (register != null) {
                result.add(register);
            }
        }
        return result;
    }

    private enum ReduceAction {
        NOOP {
            @Override
            void accept(FunctionSemanticCallback callback, ShiftReduceSemanticContext context, HeadNode head) {
            }
        },
        PREPARE_FUNCTION {
            @Override
            void accept(FunctionSemanticCallback callback, ShiftReduceSemanticContext context, HeadNode head) {
                callback.prepareFunction(context, head);
            }
        },
        VALIDATE_RETURN {
            @Override
            void accept(FunctionSemanticCallback callback, ShiftReduceSemanticContext context, HeadNode head) {
                callback.validateReturnValue(context, head, head.containsTag(ProgramSemanticTag.VALUE));
            }
        },
        VALIDATE_CALL {
            @Override
            void accept(FunctionSemanticCallback callback, ShiftReduceSemanticContext context, HeadNode head) {
                callback.validateCall(context, head);
            }
        };

        abstract void accept(FunctionSemanticCallback callback, ShiftReduceSemanticContext context, HeadNode head);
    }

}


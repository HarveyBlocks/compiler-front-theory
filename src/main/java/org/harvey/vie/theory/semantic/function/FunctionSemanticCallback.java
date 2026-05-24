package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.analysis.SemanticTypeDiagnostics;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.type.TypeRegister;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FunctionSemanticCallback implements ShiftReduceCallback {
    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        onReduce0(context, normalizeKey(production.toString().trim()));
        ShiftReduceCallback.super.onReduce(context, production);
    }

    private void onReduce0(ShiftReduceSemanticContext context, String key) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            return;
        }
        HeadNode head = context.getTreeContext().peek().toHead();
        switch (key) {
            case "function_head->type IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE":
            case "function_head->TYPE_VOID IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE":
                prepareFunction(context, head);
                return;
            case "function_decl->function_head block":
            case "top_item->function_decl":
            case "arg_list->蔚":
            case "arg_list->ε":
                return;
            case "return_stmt->CONTROL_STRUCTURES_RETURN bool OPERATOR_SEMICOLON":
                validateReturnValue(context, head, true);
                return;
            case "return_stmt->CONTROL_STRUCTURES_RETURN OPERATOR_SEMICOLON":
                validateReturnValue(context, head, false);
                return;
            case "call_expr->IDENTIFIER OPERATOR_PARENTHESIS_OPEN arg_list OPERATOR_PARENTHESIS_CLOSE":
                validateCall(context, head);
                return;
            default:
        }
    }

    private String normalizeKey(String key) {
        return key.replace("return_type", "type");
    }

    private void prepareFunction(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken nameToken = head.get(1).toToken().getSource();
        String name = new String(nameToken.getLexeme(), StandardCharsets.UTF_8);
        if (context.existFunction(name)) {
            SemanticTypeDiagnostics.reject(context, nameToken, "duplicate function declaration is not allowed.");
        }
        TypeRegister returnTypeRegister = context.getType(head.get(0));
        if (returnTypeRegister == null) {
            throw new CompilerException("function return type is missing.");
        }
        SemanticType returnType = returnTypeRegister.requireType("function return type is required.");
        List<FunctionParameter> parameters = collectParameters(context, head.get(3));
        FunctionRecord record = new FunctionRecord(
                new FunctionSignature(name, nameToken, returnType, head),
                parameters,
                head
        );
        context.registerFunction(record);
        context.markPendingFunction(record);
    }

    private void validateReturnValue(ShiftReduceSemanticContext context, HeadNode head, boolean hasValue) {
        SourceToken returnToken = head.get(0).toToken().getSource();
        if (!context.insideFunction()) {
            SemanticTypeDiagnostics.reject(context, returnToken, "return is only allowed inside function body.");
        }
        SemanticType returnType = context.currentFunctionReturnType();
        if (returnType == null) {
            throw new CompilerException("current function return type is missing.");
        }
        if (!hasValue) {
            if (!returnType.isVoidScalar()) {
                SemanticTypeDiagnostics.reject(context, returnToken, "non-void function must return a value.");
            }
            return;
        }
        if (returnType.isVoidScalar()) {
            SemanticTypeDiagnostics.reject(context, returnToken, "void function cannot return a value.");
        }
        TypeRegister valueType = context.getType(head.get(1));
        if (valueType == null) {
            throw new CompilerException("return value type is missing.");
        }
        SemanticTypeDiagnostics.requireAssignable(
                context,
                valueType.requireType("return value type is required."),
                returnType,
                returnToken,
                "return value type does not match function return type."
        );
    }

    private void validateCall(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken nameToken = head.get(0).toToken().getSource();
        String name = new String(nameToken.getLexeme(), StandardCharsets.UTF_8);
        FunctionRecord record = context.getFunction(name);
        if (record == null) {
            SemanticTypeDiagnostics.reject(context, nameToken, "function must be defined before it is called.");
        }
        List<TypeRegister> args = collectArgumentTypes(context, head.get(2));
        if (args.size() != record.getParameters().size()) {
            SemanticTypeDiagnostics.reject(context, nameToken, "function argument count does not match.");
        }
        for (int i = 0; i < args.size(); i++) {
            SemanticType sourceType = args.get(i).requireType("argument type is required.");
            SemanticType targetType = record.getParameters().get(i).getType();
            SemanticTypeDiagnostics.requireAssignable(
                    context,
                    sourceType,
                    targetType,
                    nameToken,
                    "function argument type does not match parameter type."
            );
        }
    }

    private List<FunctionParameter> collectParameters(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        List<FunctionParameter> result = new ArrayList<>();
        collectParameters0(context, node, result);
        return result;
    }

    private void collectParameters0(
            ShiftReduceSemanticContext context,
            ShiftReduceSyntaxTreeNode node,
            List<FunctionParameter> result) {
        if (!node.isHead()) {
            return;
        }
        HeadNode head = node.toHead();
        if ("param".equals(head.getSymbol().toString())) {
            TypeRegister register = context.getType(head.get(0));
            if (register == null) {
                throw new CompilerException("parameter type is missing.");
            }
            SemanticType type = register.requireType("parameter type is required.");
            SourceToken nameToken = head.get(1).toToken().getSource();
            SemanticTypeDiagnostics.requireNotVoid(context, type, nameToken, "void cannot be used as parameter type.");
            for (FunctionParameter parameter : result) {
                if (parameter.isNamed(nameToken)) {
                    SemanticTypeDiagnostics.reject(context, nameToken, "duplicate parameter declaration is not allowed.");
                }
            }
            result.add(new FunctionParameter(nameToken, type, head.get(0).toHead()));
            return;
        }
        for (ShiftReduceSyntaxTreeNode child : head) {
            collectParameters0(context, child, result);
        }
    }

    private List<TypeRegister> collectArgumentTypes(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        List<TypeRegister> result = new ArrayList<>();
        collectArgumentTypes0(context, node, result);
        return result;
    }

    private void collectArgumentTypes0(
            ShiftReduceSemanticContext context,
            ShiftReduceSyntaxTreeNode node,
            List<TypeRegister> result) {
        if (!node.isHead()) {
            return;
        }
        HeadNode head = node.toHead();
        if ("bool".equals(head.getSymbol().toString())) {
            TypeRegister register = context.getType(head);
            if (register != null) {
                result.add(register);
            }
            return;
        }
        for (ShiftReduceSyntaxTreeNode child : head) {
            collectArgumentTypes0(context, child, result);
        }
    }
}

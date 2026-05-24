package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.type.SemanticType;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class FunctionContext {
    private final Map<IdentifierKey, FunctionRecord> functions = new LinkedHashMap<>();
    private final ArrayDeque<FunctionRecord> functionStack = new ArrayDeque<>();
    private final ArrayDeque<FunctionBodyState> bodyStateStack = new ArrayDeque<>();

    public boolean exists(SourceToken token) {
        return functions.containsKey(IdentifierKey.generate(token));
    }

    public FunctionRecord get(SourceToken token) {
        return functions.get(IdentifierKey.generate(token));
    }

    public Collection<FunctionRecord> records() {
        return functions.values();
    }

    public void register(FunctionRecord record) {
        functions.put(record.getSignature().getNameKey(), record);
    }

    public void enter(FunctionRecord record) {
        functionStack.push(record);
        bodyStateStack.push(new FunctionBodyState(record));
    }

    public FunctionBodyState exit() {
        FunctionBodyState state = bodyStateStack.pop();
        FunctionRecord function = functionStack.pop();
        if (state.getFunction() != function) {
            throw new IllegalStateException("function body state stack is inconsistent");
        }
        return state;
    }

    public Optional<FunctionRecord> current() {
        return Optional.ofNullable(functionStack.peek());
    }

    public boolean insideFunction() {
        return !functionStack.isEmpty();
    }

    public boolean hasFunctionBodyState() {
        return !bodyStateStack.isEmpty();
    }

    public SemanticType currentReturnType() {
        FunctionRecord current = functionStack.peek();
        return current == null ? null : current.getSignature().getReturnType();
    }

    public FunctionRecord requireCurrent() {
        FunctionRecord current = functionStack.peek();
        if (current == null) {
            throw new IllegalStateException("current function is absent");
        }
        return current;
    }

    public FunctionBodyState currentBodyState() {
        return bodyStateStack.peek();
    }

}

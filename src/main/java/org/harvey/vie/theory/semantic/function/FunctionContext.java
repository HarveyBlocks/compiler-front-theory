package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.semantic.analysis.SemanticType;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class FunctionContext {
    private final Map<String, FunctionRecord> functions = new LinkedHashMap<>();
    private final ArrayDeque<FunctionRecord> functionStack = new ArrayDeque<>();
    private final ArrayDeque<FunctionBodyState> bodyStateStack = new ArrayDeque<>();

    public boolean exists(String name) {
        return functions.containsKey(name);
    }

    public FunctionRecord get(String name) {
        return functions.get(name);
    }

    public Collection<FunctionRecord> records() {
        return functions.values();
    }

    public void register(FunctionRecord record) {
        functions.put(record.getName(), record);
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

package org.harvey.vie.theory.semantic.function;

public class FunctionBodyState {
    private final FunctionRecord function;

    public FunctionBodyState(FunctionRecord function) {
        this.function = function;
    }

    public FunctionRecord getFunction() {
        return function;
    }
}

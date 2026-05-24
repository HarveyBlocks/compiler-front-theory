package org.harvey.vie.theory.semantic.function;

import lombok.Getter;

/**
 * @author Temper
 */
@Getter
public class FunctionBodyState {
    private final FunctionRecord function;

    public FunctionBodyState(FunctionRecord function) {
        this.function = function;
    }

}


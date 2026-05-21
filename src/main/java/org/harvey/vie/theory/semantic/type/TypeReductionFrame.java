package org.harvey.vie.theory.semantic.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Snapshot of type attributes for the current reduce step.
 */
@Getter
@AllArgsConstructor
public class TypeReductionFrame {
    private final TypeRegister[] children;
    private final TypeRegister result;
}

package org.harvey.vie.theory.semantic.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;

/**
 * Lightweight semantic attribute for type propagation on the shift/reduce stack.
 */
@Getter
@AllArgsConstructor
public class TypeRegister {
    private final SemanticType type;
    private final SemanticType instructionType;
    private final SourceToken anchorToken;

    public static TypeRegister unknown(SourceToken anchorToken) {
        return new TypeRegister(SemanticType.unknown(), SemanticType.unknown(), anchorToken);
    }

    public static TypeRegister simple(SemanticType type, SourceToken anchorToken) {
        return new TypeRegister(type, type, anchorToken);
    }
}

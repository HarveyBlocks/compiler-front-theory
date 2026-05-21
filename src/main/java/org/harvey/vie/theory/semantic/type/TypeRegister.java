package org.harvey.vie.theory.semantic.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.exception.CompilerException;
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

    public static TypeRegister noType(SourceToken anchorToken) {
        // TODO 用null值表示特殊不是一个健壮的设计
        //  一个规范的设计应该是构造一个NoType类, 且这个类只开放单例.
        //  但是太麻烦, 就暂时这样. 这里姑且记下
        return new TypeRegister(null, null, anchorToken);
    }

    public static TypeRegister simple(SemanticType type, SourceToken anchorToken) {
        return new TypeRegister(type, type, anchorToken);
    }

    public static TypeRegister typed(SemanticType type, SemanticType instructionType, SourceToken anchorToken) {
        return new TypeRegister(type, instructionType, anchorToken);
    }

    public boolean hasType() {
        return type != null;
    }

    public SemanticType requireType(String message) {
        if (!hasType()) {
            throw new CompilerException(message);
        }
        return type;
    }

    public SemanticType requireInstructionType(String message) {
        if (instructionType == null) {
            throw new CompilerException(message);
        }
        return instructionType;
    }
}

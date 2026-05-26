package org.harvey.vie.theory.semantic.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.LocationKind;

/**
 * Semantic type attribute bound to a syntax node.
 * @author Temper
 */
@Getter
@AllArgsConstructor
public class TypeRegister {
    private final SemanticType type;
    private final SemanticType instructionType;
    private final SourceToken anchorToken;
    private final LocationKind locationKind;

    public static TypeRegister simple(SemanticType type, SourceToken anchorToken) {
        return new TypeRegister(type, type, anchorToken, null);
    }

    public static TypeRegister typed(SemanticType type, SemanticType instructionType, SourceToken anchorToken) {
        return new TypeRegister(type, instructionType, anchorToken, null);
    }

    public static TypeRegister located(
            SemanticType type,
            SemanticType instructionType,
            SourceToken anchorToken,
            LocationKind locationKind) {
        return new TypeRegister(type, instructionType, anchorToken, locationKind);
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

    public LocationKind getLocationKind() {
        return locationKind;
    }
}

package org.harvey.vie.theory.semantic.type;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

/**
 * Helper for reading semantic type attributes from the current reduce frame.
 */
public final class TypeAttributes {
    private TypeAttributes() {
    }

    public static TypeRegister child(ShiftReduceSemanticContext context, int index) {
        TypeReductionFrame frame = requireFrame(context);
        return frame.getChildren()[index];
    }

    public static SemanticType childType(ShiftReduceSemanticContext context, int index) {
        return child(context, index).getType();
    }

    public static SemanticType childInstructionType(ShiftReduceSemanticContext context, int index) {
        return child(context, index).getInstructionType();
    }

    public static SourceToken childAnchor(ShiftReduceSemanticContext context, int index) {
        return child(context, index).getAnchorToken();
    }

    public static TypeRegister result(ShiftReduceSemanticContext context) {
        return requireFrame(context).getResult();
    }

    private static TypeReductionFrame requireFrame(ShiftReduceSemanticContext context) {
        TypeReductionFrame frame = context.getCurrentTypeReductionFrame();
        if (frame == null) {
            throw new IllegalStateException("type reduction frame is not available for current translator invocation");
        }
        return frame;
    }
}

package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.value.ConstantAttributes;
import org.harvey.vie.theory.semantic.value.ConstantValue;

/**
 * @author Temper
 */
final class ConstantConditionSupport {
    private ConstantConditionSupport() {
    }

    static Boolean booleanValue(ShiftReduceSemanticContext context, int childIndex) {
        if (!ConstantAttributes.childIsConstant(context, childIndex)) {
            return null;
        }
        ConstantValue value = ConstantAttributes.child(context, childIndex);
        if (value == null || !value.getType().isBooleanScalar()) {
            return null;
        }
        return value.bool();
    }

    static boolean isBooleanConstant(ShiftReduceSemanticContext context, int childIndex) {
        return booleanValue(context, childIndex) != null;
    }
}


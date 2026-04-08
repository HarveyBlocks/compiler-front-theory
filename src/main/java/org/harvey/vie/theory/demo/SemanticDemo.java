package org.harvey.vie.theory.demo;

import org.harvey.vie.theory.demo.semantic.callable.PrintCallback;
import org.harvey.vie.theory.demo.semantic.callable.TreeBuilderPredictiveCallback;
import org.harvey.vie.theory.syntax.callback.*;
import org.harvey.vie.theory.syntax.td.conflict.LexicalConflictResolver;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-08 12:56
 */
public class SemanticDemo {
    public static void a(){

    }

    public static ShiftReduceCallbackRegister buildShiftReduceRegister() {
        ShiftReduceCallbackRegister register = new ShiftReduceCallbackRegisterImpl();
        register.add(new PrintCallback());
        return register;
    }

    public static PredictiveCallbackRegister buildPredicativeRegister() {
        TreeBuilderPredictiveCallback callback = new TreeBuilderPredictiveCallback(LexicalConflictResolver.passive());
        PredictiveCallbackRegister register = new PredictiveCallbackRegisterImpl();
        register.add(callback); // 缺点: callback有状态
        return register;
    }
}

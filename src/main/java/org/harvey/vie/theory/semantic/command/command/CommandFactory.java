package org.harvey.vie.theory.semantic.command.command;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.CommandContext;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:24
 */
public class CommandFactory {
    /**
     * 仅用作测试和demo
     */
    public static SemanticCommand loadStatic(SourceToken token) {
        // 把常量加载到栈顶
        return new StringCommand("load_st_static " + new String(token.getLexeme()));
    }

    public static SemanticCommand loadIdentifierReference(SourceToken token) {
        // 把变量的引用加载到栈顶
        return new StringCommand("load_st_identifier_reference " + new String(token.getLexeme()));
    }

    public static SemanticCommand stOperator(OperatorFactor operatorFactor) {
        // 两个栈元素出栈, 结果入栈
        return new StringCommand("st_" + operatorFactor);
    }

    public static SemanticCommand stTopRefToVal() {
        // 出栈又入栈, 引用转成值
        return new StringCommand("st_top_ref_to_val");
    }

    public static SemanticCommand assignFromStTopToRef() {
        // 栈顶出栈, 作为值; 新的栈顶出栈, 作为引用. 值赋值到引用.
        return new StringCommand("assign_from_st_top_to_ref");
    }

    public static SemanticCommand biasFromStTopToRef() {
        // 栈顶出栈, 作为偏移量; 新的栈顶出栈, 作为引用. 偏移量加到引用.
        return new StringCommand("bias_from_st_top_to_ref");
    }

    public static SemanticCommand ifGoto(CommandContext.Label label) {
        return new StringCommand("if_goto " + label.getIndex());
    }

    public static SemanticCommand ifnGoto(CommandContext.Label label) {
        return new StringSupplierCommand(() -> "ifn_goto " + label.getIndex());
    }

    public static SemanticCommand gotoCommand(CommandContext.Label label) {
        return new StringSupplierCommand(() -> "goto " + label.getIndex());
    }


}

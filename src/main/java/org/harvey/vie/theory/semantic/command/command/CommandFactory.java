package org.harvey.vie.theory.semantic.command.command;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

/**
 * TODO 静态工厂是不解耦的
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:24
 */
public class CommandFactory {
    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_CHARACTER = "character";
    private static final String TYPE_INT32 = "int32";
    private static final String TYPE_FLOAT64 = "float64";
    private static final String TYPE_STRING = "string";

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

    public static SemanticCommand loadIdentifierReference(IdentifierRecord record) {
        // 操作码中的类型后缀表示“把该偏移量处的局部变量视为什么类型”
        // 参数 1: 局部变量表偏移量 offset
        return new StringCommand("load_st_" + typeMnemonic(record) + "_reference " + record.getOffset());
    }

    private static String typeMnemonic(IdentifierRecord record) {
        SourceToken typeToken = leftMostTypeToken(record);
        if (typeToken == null) {
            return "unknown";
        }
        Object type = typeToken.getType();
        if (type == org.harvey.vie.theory.demo.program.ProgramTokenType.TYPE_BOOLEAN) {
            return TYPE_BOOLEAN;
        }
        if (type == org.harvey.vie.theory.demo.program.ProgramTokenType.TYPE_CHARACTER) {
            return TYPE_CHARACTER;
        }
        if (type == org.harvey.vie.theory.demo.program.ProgramTokenType.TYPE_INT32) {
            return TYPE_INT32;
        }
        if (type == org.harvey.vie.theory.demo.program.ProgramTokenType.TYPE_FLOAT64) {
            return TYPE_FLOAT64;
        }
        if (type == org.harvey.vie.theory.demo.program.ProgramTokenType.TYPE_STRING) {
            return TYPE_STRING;
        }
        return "unknown";
    }

    private static SourceToken leftMostTypeToken(IdentifierRecord record) {
        return leftMostTypeToken(record.getType());
    }

    private static SourceToken leftMostTypeToken(HeadNode typeNode) {
        // TODO To be fix. 递归是极其错误的, 而且这里也不应该做递归的工作!
        for (ShiftReduceSyntaxTreeNode child : typeNode) {
            if (child.isToken()) {
                return child.toToken().getSource();
            }
            SourceToken token = leftMostTypeToken(child.toHead());
            if (token != null) {
                return token;
            }
        }
        return null;
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

    public static SemanticCommand ifGoto(SemanticLabel label) {
        return new StringSupplierCommand(() -> "if_goto " + label.getIndex());
    }

    public static SemanticCommand ifnGoto(SemanticLabel label) {
        return new StringSupplierCommand(() -> "ifn_goto " + label.getIndex());
    }

    public static SemanticCommand gotoCommand(SemanticLabel label) {
        return new StringSupplierCommand(() -> "goto " + label.getIndex());
    }


    public static UncertainLabelGotoCommand gotoCommandUncertainLabel(SourceToken token) {
        return new StringUncertainLabelGotoCommand(token);
    }
}

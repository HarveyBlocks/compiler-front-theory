package org.harvey.vie.theory.semantic.command.command;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

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
}

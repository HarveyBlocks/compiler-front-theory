package org.harvey.vie.theory.semantic.command.command;


import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 15:13
 */
public interface UncertainLabelGotoCommand extends SemanticCommand{
    void setLabel(SemanticLabel label);
    SourceToken getToken();
}

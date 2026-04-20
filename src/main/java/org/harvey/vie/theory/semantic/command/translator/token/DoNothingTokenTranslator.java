package org.harvey.vie.theory.semantic.command.translator.token;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.CommandContext;
import org.harvey.vie.theory.semantic.command.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 23:07
 */
public class DoNothingTokenTranslator implements TokenTranslator {
    @Override
    public CommandContext.CommandNodeRegister translate(ShiftReduceSemanticContext context, SourceToken token) {
        return new DoNothingCommandNodeRegister();
    }

    private static class DoNothingCommandNodeRegister implements CommandContext.CommandNodeRegister {
        @Override
        public void register(CommandNodeBuilder outer) {
            // do nothing
        }
    }
}

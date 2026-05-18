package org.harvey.vie.theory.semantic.command.translator.token;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.TokenCommandRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@AllArgsConstructor
public class ContinueTokenTranslator implements TokenTranslator {

    @Override
    public CommandNodeRegister translate(ShiftReduceSemanticContext context, SourceToken token) {
        UncertainLabelGotoCommand gotoCommand = CommandFactory.gotoCommandUncertainLabel(token);
        context.registerUncertainLabelContinue(gotoCommand);
        return new TokenCommandRegister(gotoCommand);
    }
}

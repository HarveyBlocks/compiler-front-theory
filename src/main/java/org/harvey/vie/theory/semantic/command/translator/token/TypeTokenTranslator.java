package org.harvey.vie.theory.semantic.command.translator.token;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

public class TypeTokenTranslator implements TokenTranslator {
    @Override
    public CommandNodeRegister translate(ShiftReduceSemanticContext context, SourceToken token) {
        return new PlaceholderNodeRegister();
    }
}

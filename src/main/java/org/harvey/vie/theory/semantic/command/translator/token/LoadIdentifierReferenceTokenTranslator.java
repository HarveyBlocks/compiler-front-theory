package org.harvey.vie.theory.semantic.command.translator.token;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.factory.DefaultCommandFactory;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.TokenCommandRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 23:10
 */
public class LoadIdentifierReferenceTokenTranslator implements TokenTranslator {
    @Override
    public CommandNodeRegister translate(ShiftReduceSemanticContext context, SourceToken token) {
        IdentifierRecord record = context.getIdentifier(token);
        if (record == null) {
            return new TokenCommandRegister(context.getCommandFactory().loadIdentifierReference(token));
        }
        return new TokenCommandRegister(context.getCommandFactory().loadIdentifierReference(record));
    }
}

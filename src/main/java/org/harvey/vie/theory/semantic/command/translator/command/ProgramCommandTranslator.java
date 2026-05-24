package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 15:29
 */
public class ProgramCommandTranslator implements CommandTranslator {
    private final CommandTranslator delegate = new SimpleShrinkTranslator();

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length != 1 && children.length != 2) {
            throw new CompilerException("illegal statement on program production.");
        }
        CommandNodeRegister result = delegate.translate(context, production, children);
        rejectUnresolved(context, result);
        return result;
    }

    private void rejectUnresolved(ShiftReduceSemanticContext context, CommandNodeRegister result) {
        boolean failed = false;
        for (UncertainLabelGotoCommand gotoCommand : result.getUncertainBreaks()) {
            if (!gotoCommand.isResolved()) {
                context.addError(gotoCommand.getToken().getOffset(), "break is not allowed here.");
                failed = true;
            }
        }
        for (UncertainLabelGotoCommand gotoCommand : result.getUncertainContinues()) {
            if (!gotoCommand.isResolved()) {
                context.addError(gotoCommand.getToken().getOffset(), "continue is not allowed here.");
                failed = true;
            }
        }
        if (failed) {
            throw new CompilerException("break/continue is not allowed here.");
        }
    }
}

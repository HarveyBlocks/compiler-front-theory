package org.harvey.vie.theory.semantic.command.translator.token;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.TokenCommandRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

import java.util.List;

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
        // TODO 正如我在TokenCommandRegister里所说, 设计是否合理
        return new TokenCommandRegister(
                gotoCommand,
                List.of(),
                List.of(gotoCommand),
                SemanticType.unknown(),
                SemanticType.unknown(),
                token
        );
    }
}

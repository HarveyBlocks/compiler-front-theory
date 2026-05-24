package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.command.factory.DefaultCommandFactory;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.SemanticTypeDiagnostics;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@AllArgsConstructor
public class DeclarationWithInitializationTranslator implements CommandTranslator {

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production, CommandNodeRegister[] children) {
        if (children.length != 5) {
            throw new CompilerException("illegal statement on declaration with initialization production.");
        }
        SemanticType targetType = TypeAttributes.childType(context, 0);
        SemanticType sourceType = TypeAttributes.childType(context, 3);
        SemanticTypeDiagnostics.requireAssignable(
                context,
                sourceType,
                targetType,
                TypeAttributes.childAnchor(context, 2),
                "assignment requires assignable types."
        );
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[1].register(thisBuilder);
        children[3].register(thisBuilder);
        if (context.requiresImplicitCast(sourceType, targetType)) {
            // TODO 同上
            thisBuilder.add(new TerminalNode(context.getCommandFactory().stTopCast(sourceType, targetType)));
        }
        thisBuilder.add(new TerminalNode(context.getCommandFactory().assignFromStTopToRef(targetType)));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }
}

package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.semantic.command.LocationKind;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@AllArgsConstructor
public class AssignStatementTranslator implements CommandTranslator {

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        SemanticType targetType = TypeAttributes.childType(context, 0);
        SemanticType sourceType = TypeAttributes.childType(context, 2);
        SemanticDiagnostics.requireAssignable(
                context,
                sourceType,
                targetType,
                TypeAttributes.childAnchor(context, 1),
                "assignment requires assignable types."
        );
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        children[0].register(builder);
        children[2].register(builder);
        if (context.requiresImplicitCast(sourceType, targetType)) {
            builder.add(new TerminalNode(context.getCommandFactory().stTopCast(
                    CommandDataType.forValue(sourceType),
                    CommandDataType.forValue(targetType)
            )));
        }
        LocationKind locationKind = TypeAttributes.child(context, 0).getLocationKind();
        if (locationKind == LocationKind.REFERENCE) {
            builder.add(new TerminalNode(context.getCommandFactory().assignFromStTopToRef(
                    CommandDataType.forStorage(targetType)
            )));
        } else {
            builder.add(new TerminalNode(context.getCommandFactory().assignFromStTopToAddr(
                    CommandDataType.forStorage(targetType)
            )));
        }
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }
}

package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.array.ArrayCreationDimensions;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

public class NewArrayTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        SemanticType type = TypeAttributes.childType(context, 1);
        SemanticDiagnostics.requireNotVoid(
                context,
                type,
                TypeAttributes.childAnchor(context, 1),
                "void cannot be used as array element type."
        );
        HeadNode head = context.getTreeContext().peek().toHead();
        ArrayCreationDimensions.Summary summary = ArrayCreationDimensions.summarizeAndValidate(context, head.get(2));
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        children[2].register(builder);
        builder.add(new TerminalNode(context.getCommandFactory().newArray(
                CommandDataType.forStorage(type),
                summary.getTotalDimensions(),
                summary.getSpecifiedDimensions()
        )));
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }
}

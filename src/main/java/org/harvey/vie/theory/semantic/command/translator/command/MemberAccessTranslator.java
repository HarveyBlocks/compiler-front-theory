package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.structure.StructField;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * Translates member access into object reference plus fixed field offset.
 *
 * @author Temper
 */
public class MemberAccessTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        children[0].register(builder);
        SemanticType baseType = TypeAttributes.childType(context, 0);
        if (baseType == null) {
            SemanticDiagnostics.reject(
                    context,
                    TypeAttributes.childAnchor(context, 0),
                    "member access requires a typed left operand."
            );
        }
        StructRecord struct = context.getStruct(baseType);
        if (struct == null) {
            SemanticDiagnostics.reject(
                    context,
                    TypeAttributes.childAnchor(context, 0),
                    "member access requires a declared struct operand."
            );
        }
        StructField field = struct.field(TypeAttributes.childAnchor(context, 2));
        builder.add(new TerminalNode(context.getCommandFactory().biasFromStTopToRef(field.getType(), field.getOffset())));
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }
}

package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

public class ArrayTypeTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length != 4) {
            throw new CompilerException("illegal statement on array type production.");
        }
        SemanticType baseType = TypeAttributes.childType(context, 0);
        if (baseType.isUnknown()) {
            return new PlaceholderNodeRegister();
        }
        int dimension = context.getTypeSystem().integerLiteral(TypeAttributes.childAnchor(context, 2));
        SemanticType arrayType = baseType.withAppendedDimension(dimension);
        return new PlaceholderNodeRegister();
    }
}

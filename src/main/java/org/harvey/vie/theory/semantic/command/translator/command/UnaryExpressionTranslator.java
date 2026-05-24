package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.demo.program.ProgramTokenType;
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
 * Handles unary productions such as {@code ! unary} and {@code - unary}.
 *
 * @author Temper
 */
public class UnaryExpressionTranslator implements CommandTranslator {
    private final OperatorFactor operatorFactor;
    private final ProgramTokenType operatorType;

    public UnaryExpressionTranslator(OperatorFactor operatorFactor, ProgramTokenType operatorType) {
        this.operatorFactor = operatorFactor;
        this.operatorType = operatorType;
    }

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        CommandNodeRegister constant = ConstantCommandSupport.constantOrNull(context, production, children);
        if (constant != null) {
            return constant;
        }
        if (children.length != 2) {
            throw new CompilerException("illegal statement on unary expression production.");
        }
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[1].register(thisBuilder);
        SemanticType operandType = TypeAttributes.childType(context, 1);
        SemanticType instructionType;
        if (operatorType == ProgramTokenType.OPERATOR_LOGICAL_NOT) {
            SemanticTypeDiagnostics.requireBoolean(
                    context,
                    operandType,
                    TypeAttributes.childAnchor(context, 0),
                    "operator '!' requires a boolean operand."
            );
            instructionType = SemanticType.scalar(SemanticType.Kind.BOOLEAN);
        } else if (operatorType == ProgramTokenType.OPERATOR_MINUS) {
            SemanticTypeDiagnostics.requireNumeric(
                    context,
                    operandType,
                    TypeAttributes.childAnchor(context, 0),
                    "unary '-' requires a numeric operand."
            );
            instructionType = operandType;
        } else {
            throw new CompilerException("unsupported unary operator type: " + operatorType);
        }
        thisBuilder.add(new TerminalNode(context.getCommandFactory().stOperator(operatorFactor, instructionType)));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }
}


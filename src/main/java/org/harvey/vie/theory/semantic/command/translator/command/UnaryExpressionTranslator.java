package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.IdentifierTokenNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.tree.node.TokenNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * Handles unary productions such as {@code ! unary} and {@code - unary}.
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
        if (children.length != 2) {
            throw new CompilerException("illegal statement on unary expression production.");
        }
        checkOperandType(context, production);
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[1].register(thisBuilder);
        thisBuilder.add(new TerminalNode(CommandFactory.stOperator(operatorFactor)));
        return new NormalCommandNodeRegister(thisBuilder.build(), production);
    }

    private void checkOperandType(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        HeadNode unaryNode = topReducedNode(context);
        if (unaryNode == null || unaryNode.size() < 2) {
            return;
        }
        OperandType operandType = resolveType(context, unaryNode.get(1));
        if (operandType == OperandType.UNKNOWN) {
            return;
        }
        SourceToken operatorToken = unaryNode.get(0).toToken().getSource();
        if (operatorType == ProgramTokenType.OPERATOR_LOGICAL_NOT && operandType != OperandType.BOOLEAN) {
            reject(context, operatorToken, production, "operator '!' requires a boolean operand.");
        }
        if (operatorType == ProgramTokenType.OPERATOR_MINUS && !operandType.numeric) {
            reject(context, operatorToken, production, "unary '-' requires a numeric operand.");
        }
    }

    private void reject(
            ShiftReduceSemanticContext context,
            SourceToken operatorToken,
            SimpleGrammarProduction production,
            String message) {
        context.addError(operatorToken.getOffset(), message);
        throw new CompilerException(message + " production=" + production);
    }

    private HeadNode topReducedNode(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty()) {
            return null;
        }
        ShiftReduceSyntaxTreeNode top = context.getTreeContext().peek();
        return top.isHead() ? top.toHead() : null;
    }

    private OperandType resolveType(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        if (node == null) {
            return OperandType.UNKNOWN;
        }
        if (node.isToken()) {
            return resolveTokenType(context, node.toToken());
        }
        HeadNode head = node.toHead();
        String name = head.getSymbol().isDefine() ? head.getSymbol().toDefine().getName() : "";
        if ("bool".equals(name) || "join".equals(name) || "equality".equals(name) || "rel".equals(name)) {
            return OperandType.BOOLEAN;
        }
        if ("loc".equals(name)) {
            return resolveLocType(context, head);
        }
        if ("type".equals(name)) {
            return resolveDeclaredType(head);
        }
        if ("factor".equals(name)) {
            return resolveFactorType(context, head);
        }
        if ("unary".equals(name)) {
            return head.size() == 0 ? OperandType.UNKNOWN : resolveType(context, head.get(head.size() - 1));
        }
        if ("term".equals(name) || "expr".equals(name)) {
            return head.size() == 0 ? OperandType.UNKNOWN : resolveType(context, head.get(head.size() - 1));
        }
        return firstKnownType(context, head);
    }

    private OperandType resolveFactorType(ShiftReduceSemanticContext context, HeadNode head) {
        if (head.size() == 3 && head.get(0).isToken() &&
            head.get(0).toToken().getSource().getType() == ProgramTokenType.OPERATOR_PARENTHESIS_OPEN) {
            return OperandType.BOOLEAN;
        }
        if (head.size() == 1) {
            return resolveType(context, head.get(0));
        }
        return firstKnownType(context, head);
    }

    private OperandType firstKnownType(ShiftReduceSemanticContext context, HeadNode head) {
        for (ShiftReduceSyntaxTreeNode child : head) {
            OperandType type = resolveType(context, child);
            if (type != OperandType.UNKNOWN) {
                return type;
            }
        }
        return OperandType.UNKNOWN;
    }

    private OperandType resolveTokenType(ShiftReduceSemanticContext context, TokenNode tokenNode) {
        SourceToken token = tokenNode.getSource();
        if (tokenNode.isIdentifier()) {
            IdentifierTokenNode identifierNode = tokenNode.toIdentifier();
            return resolveIdentifierType(context, identifierNode.getNo());
        }
        if (token.getType() == ProgramTokenType.OPERATOR_PARENTHESIS_OPEN ||
            token.getType() == ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE) {
            return OperandType.UNKNOWN;
        }
        if (token.getType() == ProgramTokenType.CONSTANT_BOOLEAN_TRUE ||
            token.getType() == ProgramTokenType.CONSTANT_BOOLEAN_FALSE) {
            return OperandType.BOOLEAN;
        }
        if (token.getType() == ProgramTokenType.CONSTANT_INTEGER) {
            return OperandType.INT32;
        }
        if (token.getType() == ProgramTokenType.CONSTANT_FLOAT) {
            return OperandType.FLOAT64;
        }
        return OperandType.UNKNOWN;
    }

    private OperandType resolveIdentifierType(ShiftReduceSemanticContext context, int identifierNo) {
        for (IdentifierRecord record : context.identifierRecords()) {
            if (record.getNo() == identifierNo) {
                return resolveDeclaredType(record.getType());
            }
        }
        return OperandType.UNKNOWN;
    }

    private OperandType resolveLocType(ShiftReduceSemanticContext context, HeadNode locNode) {
        if (locNode.size() == 0) {
            return OperandType.UNKNOWN;
        }
        ShiftReduceSyntaxTreeNode base = locNode.get(0);
        if (base.isToken() && base.toToken().isIdentifier()) {
            return resolveIdentifierType(context, base.toToken().toIdentifier().getNo());
        }
        if (base.isHead()) {
            return resolveType(context, base);
        }
        return OperandType.UNKNOWN;
    }

    private OperandType resolveDeclaredType(HeadNode typeNode) {
        if (typeNode == null || typeNode.size() == 0) {
            return OperandType.UNKNOWN;
        }
        ShiftReduceSyntaxTreeNode first = typeNode.get(0);
        if (!first.isToken()) {
            return OperandType.UNKNOWN;
        }
        SourceToken token = first.toToken().getSource();
        if (token.getType() == ProgramTokenType.TYPE_BOOLEAN) {
            return OperandType.BOOLEAN;
        }
        if (token.getType() == ProgramTokenType.TYPE_INT32) {
            return OperandType.INT32;
        }
        if (token.getType() == ProgramTokenType.TYPE_FLOAT64) {
            return OperandType.FLOAT64;
        }
        return OperandType.UNKNOWN;
    }

    private enum OperandType {
        BOOLEAN(false),
        INT32(true),
        FLOAT64(true),
        UNKNOWN(false);

        private final boolean numeric;

        OperandType(boolean numeric) {
            this.numeric = numeric;
        }
    }
}

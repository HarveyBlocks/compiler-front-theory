package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.IdentifierTokenNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.tree.node.TokenNode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class SemanticTypeResolver {
    private SemanticTypeResolver() {
    }

    static HeadNode topReducedNode(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty()) {
            return null;
        }
        ShiftReduceSyntaxTreeNode top = context.getTreeContext().peek();
        return top.isHead() ? top.toHead() : null;
    }

    static SemanticType resolve(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        if (node == null) {
            return SemanticType.unknown();
        }
        if (node.isToken()) {
            return resolveToken(context, node.toToken());
        }
        HeadNode head = node.toHead();
        String name = head.getSymbol().isDefine() ? head.getSymbol().toDefine().getName() : "";
        switch (name) {
            case "type":
                return resolveDeclaredType(head);
            case "loc":
                return resolveLoc(context, head);
            case "factor":
                return resolveFactor(context, head);
            case "unary":
                return head.size() == 1 ? resolve(context, head.get(0)) : resolve(context, head.get(1));
            case "term":
            case "expr":
                return resolveNumericExpression(context, head);
            case "rel":
                return resolveRel(context, head);
            case "equality":
                return resolveEquality(context, head);
            case "join":
            case "bool":
                return head.size() == 1 ? resolve(context, head.get(0)) : SemanticType.scalar(SemanticType.Kind.BOOLEAN);
            default:
                return firstKnownChildType(context, head);
        }
    }

    static SemanticType resolveDeclaredType(HeadNode typeNode) {
        List<Integer> dimensions = new ArrayList<>();
        HeadNode cursor = typeNode;
        while (cursor != null && cursor.size() > 0 && cursor.get(0).isHead()) {
            if (cursor.size() >= 3 && cursor.get(2).isToken()) {
                SourceToken dimensionToken = cursor.get(2).toToken().getSource();
                dimensions.add(Integer.parseInt(new String(dimensionToken.getLexeme(), StandardCharsets.UTF_8)));
            }
            cursor = cursor.get(0).toHead();
        }
        if (cursor == null || cursor.size() == 0 || !cursor.get(0).isToken()) {
            return SemanticType.unknown();
        }
        SourceToken token = cursor.get(0).toToken().getSource();
        SemanticType.Kind kind = baseKind(token);
        if (kind == SemanticType.Kind.UNKNOWN) {
            return SemanticType.unknown();
        }
        return dimensions.isEmpty() ? SemanticType.scalar(kind) : SemanticType.array(kind, dimensions);
    }

    static String identifierName(SourceToken token) {
        return new String(token.getLexeme(), StandardCharsets.UTF_8);
    }

    private static SemanticType resolveToken(ShiftReduceSemanticContext context, TokenNode tokenNode) {
        SourceToken token = tokenNode.getSource();
        if (tokenNode.isIdentifier()) {
            IdentifierTokenNode identifierNode = tokenNode.toIdentifier();
            IdentifierRecord record = context.getIdentifierByNo(identifierNode.getNo());
            return record == null ? SemanticType.unknown() : resolveDeclaredType(record.getType());
        }
        if (token.getType() == ProgramTokenType.CONSTANT_BOOLEAN_TRUE ||
            token.getType() == ProgramTokenType.CONSTANT_BOOLEAN_FALSE) {
            return SemanticType.scalar(SemanticType.Kind.BOOLEAN);
        }
        if (token.getType() == ProgramTokenType.CONSTANT_INTEGER) {
            return SemanticType.scalar(SemanticType.Kind.INT32);
        }
        if (token.getType() == ProgramTokenType.CONSTANT_FLOAT) {
            return SemanticType.scalar(SemanticType.Kind.FLOAT64);
        }
        return SemanticType.unknown();
    }

    private static SemanticType resolveLoc(ShiftReduceSemanticContext context, HeadNode locNode) {
        if (locNode.size() == 1) {
            return resolve(context, locNode.get(0));
        }
        return resolve(context, locNode.get(0)).elementType();
    }

    private static SemanticType resolveFactor(ShiftReduceSemanticContext context, HeadNode head) {
        if (head.size() == 3 && head.get(0).isToken() &&
            head.get(0).toToken().getSource().getType() == ProgramTokenType.OPERATOR_PARENTHESIS_OPEN) {
            return resolve(context, head.get(1));
        }
        return head.size() == 1 ? resolve(context, head.get(0)) : firstKnownChildType(context, head);
    }

    private static SemanticType resolveNumericExpression(ShiftReduceSemanticContext context, HeadNode head) {
        if (head.size() == 1) {
            return resolve(context, head.get(0));
        }
        SemanticType left = resolve(context, head.get(0));
        SemanticType right = resolve(context, head.get(2));
        return left.promoteNumeric(right);
    }

    private static SemanticType resolveRel(ShiftReduceSemanticContext context, HeadNode head) {
        if (head.size() == 1) {
            return resolve(context, head.get(0));
        }
        return SemanticType.scalar(SemanticType.Kind.BOOLEAN);
    }

    private static SemanticType resolveEquality(ShiftReduceSemanticContext context, HeadNode head) {
        if (head.size() == 1) {
            return resolve(context, head.get(0));
        }
        return SemanticType.scalar(SemanticType.Kind.BOOLEAN);
    }

    private static SemanticType firstKnownChildType(ShiftReduceSemanticContext context, HeadNode head) {
        for (ShiftReduceSyntaxTreeNode child : head) {
            SemanticType type = resolve(context, child);
            if (!type.isUnknown()) {
                return type;
            }
        }
        return SemanticType.unknown();
    }

    private static SemanticType.Kind baseKind(SourceToken token) {
        if (token.getType() == ProgramTokenType.TYPE_BOOLEAN) {
            return SemanticType.Kind.BOOLEAN;
        }
        if (token.getType() == ProgramTokenType.TYPE_CHARACTER) {
            return SemanticType.Kind.CHARACTER;
        }
        if (token.getType() == ProgramTokenType.TYPE_INT32) {
            return SemanticType.Kind.INT32;
        }
        if (token.getType() == ProgramTokenType.TYPE_FLOAT64) {
            return SemanticType.Kind.FLOAT64;
        }
        if (token.getType() == ProgramTokenType.TYPE_STRING) {
            return SemanticType.Kind.STRING;
        }
        return SemanticType.Kind.UNKNOWN;
    }
}

package org.harvey.vie.theory.demo;

import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.demo.semantic.callable.TreeBuilderPredictiveCallback;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.semantic.callback.bu.*;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegister;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegisterImpl;
import org.harvey.vie.theory.semantic.command.CommandBuildCallback;
import org.harvey.vie.theory.semantic.command.CommandContext;
import org.harvey.vie.theory.semantic.command.SemanticCommandPrintCallback;
import org.harvey.vie.theory.semantic.command.translator.command.*;
import org.harvey.vie.theory.semantic.command.translator.token.DoNothingTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.LoadIdentifierReferenceTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.SimpleStringTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.TokenTranslator;
import org.harvey.vie.theory.semantic.error.PassiveErrorCallback;
import org.harvey.vie.theory.semantic.identifier.IdentifierScopeCallback;
import org.harvey.vie.theory.semantic.identifier.IdentifierTableBuildCallback;
import org.harvey.vie.theory.semantic.log.TreeLogCallback;
import org.harvey.vie.theory.semantic.tree.TreeBuildCallback;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.syntax.td.conflict.LexicalConflictResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-08 12:56
 */
public class SemanticDemo {
    public static ShiftReduceCallbackRegister buildSimpleShiftReduceRegister() {
        ShiftReduceCallbackRegister register = new ShiftReduceCallbackRegisterImpl();
        register.add(new TreeBuildCallback());
        register.add(new TreeLogCallback());
        register.add(new PassiveErrorCallback());
        return register;
    }

    public static ShiftReduceCallbackRegister buildShiftReduceRegister() {
        ShiftReduceCallbackRegister register = new ShiftReduceCallbackRegisterImpl();
        register.add(new TreeBuildCallback());
        register.add(new TreeLogCallback());
        register.add(instanceIdentifierScopeCallback());
        register.add(instanceIdentifierTableBuildCallback());
        register.add(new PassiveErrorCallback());
        register.add(instanceSemanticCommandPrintCallback());
        register.add(instanceSyntaxDirectedTranslationCallback());
        return register;
    }


    static TokenTranslator defaultTokenTranslator = new DoNothingTokenTranslator();
    static CommandTranslator defaultCommandTranslator = new SimpleShrinkTranslator();

    private static ShiftReduceCallback instanceSyntaxDirectedTranslationCallback() {
        CommandContext.TokenTranslatorStrategy shiftStrategies = shiftStrategies();
        CommandContext.CommandTranslatorStrategy reduceStrategies = reduceStrategies();
        // 需要涉及符号表的具体构建
        return new CommandBuildCallback(shiftStrategies, reduceStrategies);
    }

    private static CommandContext.TokenTranslatorStrategy shiftStrategies() {
        Map<TokenType, TokenTranslator> shiftStrategies = new HashMap<>();
        // 需要涉及符号表的具体构建
        TokenTranslator loadIdentifierReferenceTokenTranslator = new LoadIdentifierReferenceTokenTranslator();
        shiftStrategies.put(ProgramTokenType.IDENTIFIER, loadIdentifierReferenceTokenTranslator);
        // shiftStrategies.put(ProgramTokenType.SPACE, null);
        // shiftStrategies.put(ProgramTokenType.COMMENT_BLOCK, null);
        // shiftStrategies.put(ProgramTokenType.COMMENT_LINE, null);
        TokenTranslator simpleStringTokenTranslator = new SimpleStringTokenTranslator();
        shiftStrategies.put(ProgramTokenType.CONSTANT_STRING, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_CHARACTER, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_INTEGER, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_FLOAT, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_BOOLEAN_TRUE, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_BOOLEAN_FALSE, simpleStringTokenTranslator);
        // shiftStrategies.put(ProgramTokenType.TYPE_BOOLEAN, null);
        // shiftStrategies.put(ProgramTokenType.TYPE_CHARACTER, null);
        // shiftStrategies.put(ProgramTokenType.TYPE_INT32, null);
        // shiftStrategies.put(ProgramTokenType.TYPE_FLOAT64, null);
        // shiftStrategies.put(ProgramTokenType.TYPE_STRING, null);
        // shiftStrategies.put(ProgramTokenType.OPERATOR_PLUS, null);
        // shiftStrategies.put(ProgramTokenType.OPERATOR_MULTIPLY, null);
        // shiftStrategies.put(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN, null);
        // shiftStrategies.put(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE, null);
        // shiftStrategies.put(ProgramTokenType.OPERATOR_ASSIGN, null);
        // shiftStrategies.put(ProgramTokenType.OPERATOR_SEMICOLON, null);
        // shiftStrategies.put(ProgramTokenType.OPERATOR_SQUARE_OPEN, null);
        // shiftStrategies.put(ProgramTokenType.OPERATOR_SQUARE_CLOSE, null);
        // shiftStrategies.put(ProgramTokenType.OPERATOR_BRACE_OPEN, null);
        // shiftStrategies.put(ProgramTokenType.OPERATOR_BRACE_CLOSE, null);
        // shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_IF, null);
        // shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_ELSE, null);
        // shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_WHILE, null);
        // shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_DO, null);
        // shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_BREAK, null);
        // shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_CONTINUE, null);
        return t -> shiftStrategies.getOrDefault(t, defaultTokenTranslator);
    }

    private static CommandContext.CommandTranslatorStrategy reduceStrategies() {
        // TODO 每次文法改变->分析表改变->产生式的池/id改变->需要改变这里的id映射
        HashMap<Integer, CommandTranslator> map = new HashMap<>();
        // 37	: declaration_stmt->type id ; 声明语句, 但是没有复制, 好像没啥用, 不需要做声明
        map.put(37, new DoNotingTranslator());
        // 38	: term->term * factor                   // In-suffix expression
        // 39	: expr->expr + term                     // In-suffix expression
        map.put(38, new InSuffixExpressionTranslator(new OperatorFactor() {
            @Override
            public String toString() {
                return "multiply";
            }
        }));
        map.put(39, new InSuffixExpressionTranslator(new OperatorFactor() {
            @Override
            public String toString() {
                return "plus";
            }
        }));
        // 44	: declaration_stmt->type id = expr ; 可以走直接赋值的路
        map.put(44, new AssignStatementTranslator());
        // 30	: primary->lvalue
        map.put(30, new PrimaryProduceLeftValueTranslator());
        // 42	: assignment_stmt->lvalue = expr ;
        map.put(42, new AssignStatementTranslator());
        // 43	: lvalue->lvalue [ expr ]
        map.put(43, new ArrayAtExpressionTranslator());
        // 45	: unmatched_while_stmt->while ( expr ) unmatched_stmt
        // 46	: matched_while_stmt->while ( expr ) matched_stmt
        map.put(45, new WhileStatementTranslator());
        map.put(46, new WhileStatementTranslator());
        // 48	: do_while_stmt->do stmt while ( expr ) ;
        map.put(48, new DoWhileStatementTranslator());
        // 47	: unmatched_if_stmt->if ( expr ) stmt
        map.put(47, new IfStatementTranslator());
        // 49	: unmatched_if_stmt->if ( expr ) matched_stmt else unmatched_stmt
        // 50	: matched_if_stmt->if ( expr ) matched_stmt else matched_stmt
        map.put(49, new IfElseStatementTranslator());
        map.put(50, new IfElseStatementTranslator());
        //  map.put(0, null);
        return i -> map.getOrDefault(i, defaultCommandTranslator);
    }

    private static ShiftReduceCallback instanceSemanticCommandPrintCallback() {
        return new SemanticCommandPrintCallback();
    }

    private static ShiftReduceCallback instanceIdentifierScopeCallback() {
        final ShiftPredicate scopeIntoPredicate = t -> t.getType() == ProgramTokenType.OPERATOR_BRACE_OPEN;
        final ReducePredicate scopeExistPredicate = p -> p.getHead().isDefine() &&
                                                         "block".equals(p.getHead().toDefine().getName());
        return new IdentifierScopeCallback(scopeIntoPredicate, scopeExistPredicate);
    }

    private static ShiftReduceCallback instanceIdentifierTableBuildCallback() {
        final ReducePredicate usingPredicate = p -> p.getHead().isDefine() &&
                                                    "lvalue".equals(p.getHead().toDefine().getName());
        final ReducePredicate declaringPredicate = p -> p.getHead().isDefine() &&
                                                        "declaration_stmt".equals(p.getHead().toDefine().getName());
        // 和文法有关 lvalue -> id lvalue_suffix
        final IdentifierTableBuildCallback.UsingIdentifierSupplier usingIdentifierSupplier = n -> n.get(0)
                .toToken()
                .getSource();

        // 和文法有关 declaration -> type id = expr;
        final IdentifierTableBuildCallback.DeclarationRecordSupplier declarationRecordSupplier = new IdentifierTableBuildCallback.DeclarationRecordSupplier() {
            @Override
            public SourceToken identifier(HeadNode declarationReducedNode) {
                return declarationReducedNode.get(1).toToken().getSource();
            }

            @Override
            public boolean initialized(HeadNode declarationReducedNode) {
                return declarationReducedNode.size() > 2;
            }

            @Override
            public HeadNode typeHeadNode(HeadNode declarationReducedNode) {
                return declarationReducedNode.get(0).toHead();
            }
        };
        return new IdentifierTableBuildCallback(
                usingPredicate,
                declaringPredicate,
                usingIdentifierSupplier,
                declarationRecordSupplier
        );
    }

    public static PredictiveCallbackRegister buildPredicativeRegister() {
        TreeBuilderPredictiveCallback callback = new TreeBuilderPredictiveCallback(LexicalConflictResolver.passive());
        PredictiveCallbackRegister register = new PredictiveCallbackRegisterImpl();
        register.add(callback); // 缺点: callback有状态
        return register;
    }
}

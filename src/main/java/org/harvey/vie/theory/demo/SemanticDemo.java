package org.harvey.vie.theory.demo;

import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.demo.semantic.callable.TreeBuilderPredictiveCallback;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.semantic.callback.bu.*;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegister;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegisterImpl;
import org.harvey.vie.theory.semantic.command.CommandBuildCallback;
import org.harvey.vie.theory.semantic.command.SemanticCommandPrintCallback;
import org.harvey.vie.theory.semantic.command.translator.CommandTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.TokenTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.command.*;
import org.harvey.vie.theory.semantic.command.translator.token.*;
import org.harvey.vie.theory.semantic.error.PassiveErrorCallback;
import org.harvey.vie.theory.semantic.identifier.IdentifierScopeCallback;
import org.harvey.vie.theory.semantic.identifier.IdentifierTableBuildCallback;
import org.harvey.vie.theory.semantic.log.TreeLogCallback;
import org.harvey.vie.theory.semantic.tree.TreeBuildCallback;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
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
        TokenTranslatorStrategy shiftStrategies = shiftStrategies();
        CommandTranslatorStrategy reduceStrategies = reduceStrategies0();
        // 需要涉及符号表的具体构建
        return new CommandBuildCallback(shiftStrategies, reduceStrategies);
    }

    private static TokenTranslatorStrategy shiftStrategies() {
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
        shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_BREAK, new BreakTokenTranslator());
        shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_CONTINUE, new ContinueTokenTranslator());
        return t -> shiftStrategies.getOrDefault(t, defaultTokenTranslator);
    }

    private static CommandTranslatorStrategy reduceStrategies() {
        // TODO 每次文法改变->分析表改变->产生式的池/id改变->需要改变这里的id映射
        HashMap<Integer, CommandTranslator> map = new HashMap<>();
        // 35 stmt_list->stmt stmt_list
        map.put(35, new StatementListTranslator());
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
        // 44	: declaration_stmt->type id ; 什么都不做
        map.put(37, new DeclarationWithInitializationTranslator());
        // 44	: declaration_stmt->type id = expr ; 可以走直接赋值的路
        map.put(44, new DeclarationWithInitializationTranslator());
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
        // 14	: program->stmt_list
        map.put(14, new ProgramCommandTranslator());
        return production -> map.getOrDefault(null, defaultCommandTranslator);
    }
    private static CommandTranslatorStrategy reduceStrategies0() {
        HashMap<String, CommandTranslator> map = new HashMap<>();
        CommandTranslator doNothing = new DoNotingTranslator();
        CommandTranslator simpleShrink = defaultCommandTranslator;

        map.put("decls->ε", doNothing);
        map.put("program->block", new ProgramCommandTranslator());
        map.put("stmts->ε", doNothing);
        map.put("decls->decls decl", doNothing);
        map.put("stmts->stmts stmt", new StatementListTranslator());
        map.put("decl->type IDENTIFIER OPERATOR_SEMICOLON", new DeclarationWithoutInitializationTranslator());
        map.put("matched_stmt->CONTROL_STRUCTURES_BREAK OPERATOR_SEMICOLON", simpleShrink);
        map.put("type->type OPERATOR_SQUARE_OPEN CONSTANT_INTEGER OPERATOR_SQUARE_CLOSE", doNothing);
        map.put("factor->loc", new PrimaryProduceLeftValueTranslator());
        map.put("unary->OPERATOR_LOGICAL_NOT unary",
                new UnaryExpressionTranslator(operator("logical_not"), ProgramTokenType.OPERATOR_LOGICAL_NOT));
        map.put("unary->OPERATOR_MINUS unary",
                new UnaryExpressionTranslator(operator("negate"), ProgramTokenType.OPERATOR_MINUS));
        map.put("matched_stmt->loc OPERATOR_ASSIGN bool OPERATOR_SEMICOLON", new AssignStatementTranslator());
        map.put("loc->loc OPERATOR_SQUARE_OPEN bool OPERATOR_SQUARE_CLOSE", new ArrayAtExpressionTranslator());
        map.put("bool->bool OPERATOR_LOGICAL_OR join", new InSuffixExpressionTranslator(operator("logical_or")));
        map.put("unmatched_if_stmt->CONTROL_STRUCTURES_IF OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE stmt",
                new IfStatementTranslator());
        map.put("term->term OPERATOR_DIVIDE unary", new InSuffixExpressionTranslator(operator("divide")));
        map.put("term->term OPERATOR_MULTIPLY unary", new InSuffixExpressionTranslator(operator("multiply")));
        map.put("equality->equality OPERATOR_NOT_EQUAL rel", new InSuffixExpressionTranslator(operator("not_equal")));
        map.put("equality->equality OPERATOR_EQUAL rel", new InSuffixExpressionTranslator(operator("equal")));
        map.put("join->join OPERATOR_LOGICAL_AND equality", new InSuffixExpressionTranslator(operator("logical_and")));
        map.put("rel->expr OPERATOR_LESS_EQUAL expr", new InSuffixExpressionTranslator(operator("less_equal")));
        map.put("rel->expr OPERATOR_LESS expr", new InSuffixExpressionTranslator(operator("less")));
        map.put("expr->expr OPERATOR_MINUS term", new InSuffixExpressionTranslator(operator("minus")));
        map.put("rel->expr OPERATOR_GREATER_EQUAL expr", new InSuffixExpressionTranslator(operator("greater_equal")));
        map.put("rel->expr OPERATOR_GREATER expr", new InSuffixExpressionTranslator(operator("greater")));
        map.put("expr->expr OPERATOR_PLUS term", new InSuffixExpressionTranslator(operator("plus")));
        map.put("unmatched_while_stmt->CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE unmatched_stmt",
                new WhileStatementTranslator());
        map.put("matched_while_stmt->CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE matched_stmt",
                new WhileStatementTranslator());
        map.put("matched_stmt->CONTROL_STRUCTURES_DO stmt CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE OPERATOR_SEMICOLON",
                new DoWhileStatementTranslator());
        map.put("unmatched_if_stmt->CONTROL_STRUCTURES_IF OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE matched_stmt CONTROL_STRUCTURES_ELSE unmatched_stmt",
                new IfElseStatementTranslator());
        map.put("matched_if_stmt->CONTROL_STRUCTURES_IF OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE matched_stmt CONTROL_STRUCTURES_ELSE matched_stmt",
                new IfElseStatementTranslator());
        return production -> map.getOrDefault(productionKey(production), defaultCommandTranslator);
    }

    private static String productionKey(SimpleGrammarProduction production) {
        return production.toString().trim();
    }

    private static OperatorFactor operator(String name) {
        return new OperatorFactor() {
            @Override
            public String toString() {
                return name;
            }
        };
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
                                                    "loc".equals(p.getHead().toDefine().getName());
        final ReducePredicate declaringPredicate = p -> p.getHead().isDefine() &&
                                                        "decl".equals(p.getHead().toDefine().getName());
        // 和文法有关 loc -> IDENTIFIER | loc [ bool ]
        final IdentifierTableBuildCallback.UsingIdentifierSupplier usingIdentifierSupplier =
                IdentifierTableBuildCallback::leftMostToken;

        // 和文法有关 decl -> type IDENTIFIER ;
        final IdentifierTableBuildCallback.DeclarationRecordSupplier declarationRecordSupplier = new IdentifierTableBuildCallback.DeclarationRecordSupplier() {
            @Override
            public SourceToken identifier(HeadNode declarationReducedNode) {
                return declarationReducedNode.get(1).toToken().getSource();
            }

            @Override
            public boolean initialized(HeadNode declarationReducedNode) {
                return false;
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

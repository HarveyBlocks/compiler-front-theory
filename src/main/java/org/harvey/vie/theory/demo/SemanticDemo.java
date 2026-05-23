package org.harvey.vie.theory.demo;

import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.demo.semantic.callable.TreeBuilderPredictiveCallback;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.semantic.callback.bu.ReducePredicate;
import org.harvey.vie.theory.semantic.callback.bu.ShiftPredicate;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallbackRegister;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallbackRegisterImpl;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegister;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegisterImpl;
import org.harvey.vie.theory.semantic.command.CommandBuildCallback;
import org.harvey.vie.theory.semantic.command.SemanticCommandPrintCallback;
import org.harvey.vie.theory.semantic.command.SemanticResultCallback;
import org.harvey.vie.theory.semantic.command.translator.CommandTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.TokenTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.command.ArrayAtExpressionTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.ArrayTypeTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.AssignStatementTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.CommandTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.DeclarationWithInitializationTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.DeclarationWithoutInitializationTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.DoNotingTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.DoWhileStatementTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.FunctionCallTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.FunctionHeadTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.FunctionReturnTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.IfElseStatementTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.IfStatementTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.InSuffixExpressionTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.command.translator.command.ParenthesizedExpressionTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.PrimaryProduceLeftValueTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.ProgramCommandTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.SimpleShrinkTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.StatementListTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.UnaryExpressionTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.WhileStatementTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.BreakTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.ContinueTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.DoNothingTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.LoadIdentifierReferenceTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.SimpleStringTokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.TokenTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.TypeTokenTranslator;
import org.harvey.vie.theory.semantic.error.PassiveErrorCallback;
import org.harvey.vie.theory.semantic.function.FunctionSemanticCallback;
import org.harvey.vie.theory.semantic.identifier.IdentifierScopeCallback;
import org.harvey.vie.theory.semantic.identifier.IdentifierTableBuildCallback;
import org.harvey.vie.theory.semantic.log.TreeLogCallback;
import org.harvey.vie.theory.semantic.tree.TreeBuildCallback;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.type.TypeBuildCallback;
import org.harvey.vie.theory.semantic.value.ConstantValueBuildCallback;
import org.harvey.vie.theory.semantic.value.IdentifierConstantStateCallback;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.td.conflict.LexicalConflictResolver;

import java.util.HashMap;
import java.util.Map;

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
        register.add(new TypeBuildCallback());
        register.add(new ConstantValueBuildCallback());
        register.add(new FunctionSemanticCallback());
        register.add(instanceSemanticCommandPrintCallback());
        register.add(instanceSyntaxDirectedTranslationCallback());
        register.add(instanceIdentifierTableBuildCallback());
        register.add(new IdentifierConstantStateCallback());
        register.add(new PassiveErrorCallback());
        return register;
    }

    public static ShiftReduceCallbackRegister buildShiftReduceTestRegister() {
        ShiftReduceCallbackRegister register = new ShiftReduceCallbackRegisterImpl();
        register.add(new TreeBuildCallback());
        register.add(instanceIdentifierScopeCallback());
        register.add(new TypeBuildCallback());
        register.add(new ConstantValueBuildCallback());
        register.add(new FunctionSemanticCallback());
        register.add(new SemanticResultCallback());
        register.add(instanceSyntaxDirectedTranslationCallback());
        register.add(instanceIdentifierTableBuildCallback());
        register.add(new IdentifierConstantStateCallback());
        register.add(new PassiveErrorCallback());
        return register;
    }

    static TokenTranslator defaultTokenTranslator = new DoNothingTokenTranslator();
    static CommandTranslator defaultCommandTranslator = new SimpleShrinkTranslator();

    private static ShiftReduceCallback instanceSyntaxDirectedTranslationCallback() {
        return new CommandBuildCallback(shiftStrategies(), reduceStrategies0());
    }

    private static TokenTranslatorStrategy shiftStrategies() {
        Map<TokenType, TokenTranslator> shiftStrategies = new HashMap<>();
        TokenTranslator loadIdentifierReferenceTokenTranslator = new LoadIdentifierReferenceTokenTranslator();
        shiftStrategies.put(ProgramTokenType.IDENTIFIER, loadIdentifierReferenceTokenTranslator);
        TokenTranslator simpleStringTokenTranslator = new SimpleStringTokenTranslator();
        shiftStrategies.put(ProgramTokenType.CONSTANT_STRING, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_CHARACTER, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_INTEGER, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_FLOAT, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_BOOLEAN_TRUE, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_BOOLEAN_FALSE, simpleStringTokenTranslator);
        TokenTranslator typeTokenTranslator = new TypeTokenTranslator();
        shiftStrategies.put(ProgramTokenType.TYPE_BOOLEAN, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_CHARACTER, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_INT32, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_FLOAT64, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_STRING, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_BREAK, new BreakTokenTranslator());
        shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_CONTINUE, new ContinueTokenTranslator());
        return t -> shiftStrategies.getOrDefault(t, defaultTokenTranslator);
    }

    private static CommandTranslatorStrategy reduceStrategies0() {
        HashMap<String, CommandTranslator> map = new HashMap<>();
        CommandTranslator doNothing = new DoNotingTranslator();
        CommandTranslator simpleShrink = defaultCommandTranslator;

        CommandTranslator programTranslator = new ProgramCommandTranslator();
        map.put("compilation_unit->program", programTranslator);
        map.put("program->block", programTranslator);
        map.put("program->top_item", programTranslator);
        map.put("program->top_item program", programTranslator);
        map.put("top_item->function_decl", simpleShrink);
        map.put("top_item->block_item", simpleShrink);
        map.put("function_head->type IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE",
                new FunctionHeadTranslator());
        map.put("function_head->TYPE_VOID IDENTIFIER OPERATOR_PARENTHESIS_OPEN param_list OPERATOR_PARENTHESIS_CLOSE",
                new FunctionHeadTranslator());
        map.put("function_decl->function_head block", simpleShrink);
        map.put("block_items->蔚", doNothing);
        map.put("block_items->block_item", simpleShrink);
        map.put("block_items->block_items block_item", new StatementListTranslator());
        map.put("block_item->decl", simpleShrink);
        map.put("block_item->stmt", simpleShrink);
        map.put("decl->decl_plain", simpleShrink);
        map.put("decl->decl_init", simpleShrink);
        map.put("decl_plain->type IDENTIFIER OPERATOR_SEMICOLON", new DeclarationWithoutInitializationTranslator());
        map.put("decl_init->type IDENTIFIER OPERATOR_ASSIGN bool OPERATOR_SEMICOLON",
                new DeclarationWithInitializationTranslator());
        map.put("matched_stmt->expr_stmt", simpleShrink);
        map.put("break_stmt->CONTROL_STRUCTURES_BREAK OPERATOR_SEMICOLON", simpleShrink);
        map.put("continue_stmt->CONTROL_STRUCTURES_CONTINUE OPERATOR_SEMICOLON", simpleShrink);
        map.put("return_stmt->CONTROL_STRUCTURES_RETURN bool OPERATOR_SEMICOLON", new FunctionReturnTranslator());
        map.put("return_stmt->CONTROL_STRUCTURES_RETURN OPERATOR_SEMICOLON", new FunctionReturnTranslator());
        map.put("param_list->params", simpleShrink);
        map.put("param_list->蔚", doNothing);
        map.put("args->bool", simpleShrink);
        map.put("args->args OPERATOR_COMMA bool", new StatementListTranslator());
        map.put("call_expr->IDENTIFIER OPERATOR_PARENTHESIS_OPEN arg_list OPERATOR_PARENTHESIS_CLOSE", new FunctionCallTranslator());
        map.put("arg_list->args", simpleShrink);
        map.put("arg_list->蔚", doNothing);
        map.put("expr_stmt->expr OPERATOR_SEMICOLON", simpleShrink);
        map.put("type->type OPERATOR_SQUARE_OPEN CONSTANT_INTEGER OPERATOR_SQUARE_CLOSE", new ArrayTypeTranslator());
        map.put("factor->OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE", new ParenthesizedExpressionTranslator());
        map.put("factor->loc", new PrimaryProduceLeftValueTranslator());
        map.put("unary->OPERATOR_LOGICAL_NOT unary",
                new UnaryExpressionTranslator(operator("logical_not"), ProgramTokenType.OPERATOR_LOGICAL_NOT));
        map.put("unary->OPERATOR_MINUS unary",
                new UnaryExpressionTranslator(operator("negate"), ProgramTokenType.OPERATOR_MINUS));
        map.put("assign_stmt->loc OPERATOR_ASSIGN bool OPERATOR_SEMICOLON", new AssignStatementTranslator());
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
        map.put("do_while_stmt->CONTROL_STRUCTURES_DO stmt CONTROL_STRUCTURES_WHILE OPERATOR_PARENTHESIS_OPEN bool OPERATOR_PARENTHESIS_CLOSE OPERATOR_SEMICOLON",
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
        ShiftPredicate scopeIntoPredicate = t -> t.getType() == ProgramTokenType.OPERATOR_BRACE_OPEN;
        ReducePredicate scopeExistPredicate = p -> p.getHead().isDefine() &&
                "block".equals(p.getHead().toDefine().getName());
        return new IdentifierScopeCallback(scopeIntoPredicate, scopeExistPredicate);
    }

    private static ShiftReduceCallback instanceIdentifierTableBuildCallback() {
        ReducePredicate usingPredicate = p -> "loc->IDENTIFIER".equals(productionKey(p));
        ReducePredicate declaringPredicate = p -> {
            String key = productionKey(p);
            return "decl_plain->type IDENTIFIER OPERATOR_SEMICOLON".equals(key) ||
                    "decl_init->type IDENTIFIER OPERATOR_ASSIGN bool OPERATOR_SEMICOLON".equals(key);
        };
        IdentifierTableBuildCallback.UsingIdentifierSupplier usingIdentifierSupplier =
                usingIdentifierReducedNode -> usingIdentifierReducedNode.get(0).toToken().getSource();

        IdentifierTableBuildCallback.DeclarationRecordSupplier declarationRecordSupplier =
                new IdentifierTableBuildCallback.DeclarationRecordSupplier() {
                    @Override
                    public SourceToken identifier(HeadNode declarationReducedNode) {
                        return declarationReducedNode.get(1).toToken().getSource();
                    }

                    @Override
                    public boolean initialized(HeadNode declarationReducedNode) {
                        return declarationReducedNode.size() > 3;
                    }

                    @Override
                    public HeadNode typeHeadNode(HeadNode declarationReducedNode) {
                        return declarationReducedNode.get(0).toHead();
                    }

                    @Override
                    public org.harvey.vie.theory.semantic.value.ConstantValue initializerValue(
                            org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext context,
                            HeadNode declarationReducedNode) {
                        if (declarationReducedNode.size() <= 3) {
                            return null;
                        }
                        return context.getConstantValue(declarationReducedNode.get(3).toHead());
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
        register.add(callback);
        return register;
    }
}

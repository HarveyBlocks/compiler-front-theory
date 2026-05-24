package org.harvey.vie.theory.demo.program;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.demo.SemanticDemo;
import org.harvey.vie.theory.demo.SyntaxDemo;
import org.harvey.vie.theory.error.DefaultErrorContext;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.io.resource.AsciiStringResource;
import org.harvey.vie.theory.io.resource.Resource;
import org.harvey.vie.theory.lexical.analysis.LexicalAnalyzer;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.semantic.context.SemanticResult;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaser;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaserImpl;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContextBuilder;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContextBuilderImpl;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactory;
import org.harvey.vie.theory.syntax.grammar.symbol.TokenTypeTerminalSymbol;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

import java.util.Set;
import java.util.function.BiFunction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-13 20:27
 */
@Slf4j
public class ProgramSyntaxDemo {

    public static final Set<TokenType> SHOULD_BE_FILTERED = Set.of(
            ProgramTokenType.SPACE,
            ProgramTokenType.COMMENT_LINE,
            ProgramTokenType.COMMENT_BLOCK
    );
    public static final SemanticTag.Loader<?> PROGRAM_SEMANTIC_TAG_LOADER = new ProgramSemanticTag.Loader();

    public static void main(String[] args) {
        if (args.length == 0) {
            ProgramSyntaxTestRunner.run();
            return;
        }
        // 由于ProductionPool的实现是依赖与hash的底层实现的, 而这个值是随着JVM变化的
        // 比如调试和运行的结果不一样
        //  解决方法, 要不是持久化, 要不就是在一开始引入id
        //  持久化的坏处是文法改变id也随之改变
        //  一开始引入ID的坏处是难以处理 Epsilon
        //      一个不直观的解决方法是, 在 head 处存储id, 如果 body 是 epsilon, 则使用 head 的 id
        //      否则使用 body 的 id
        //      这样需要考虑到消除左递归等操作, 会创建新的产生式
        //      说实话, 很烦, 因为只有这里需要id, 而引入id主要的困难会发生在predicate
        //      所以持久化才是比较好的方案?
        String text1 = "{ " +
                "int32 i; " +
                "int32[10] arr; " +
                "boolean flag; " +
                "i = 3 + 4 * 6; " +
                "arr[1] = i - 2; " +
                "if (i < arr[1] || false) { " +
                "arr[2] = arr[1] / 2; " +
                "} else while (i != 0) { " +
                "i = i - 1; " +
                "if (i == 2) break; " +
                "} " +
                "do i = i + 1; while (i <= 10); " +
                "flag = !(i >= 10) && true; " +
                "}";
        String text2 = "{ " +
                "int32 a; " +
                "int32 b; " +
                "boolean ok; " +
                "a = 1; " +
                "b = 2; " +
                "if (a < b) " +
                "if (b < 10) " +
                "a = a + b; " +
                "else " +
                "b = b - a; " +
                "ok = a != b; " +
                "}";
        String text3 = "{ " +
                "int32[2][3] matrix; " +
                "int32 row; " +
                "int32 col; " +
                "float64 value; " +
                "row = 0; " +
                "col = 1; " +
                "matrix[row][col] = 8; " +
                "while (row < 2 && col <= 2) { " +
                "matrix[row][col] = matrix[row][col] / 2; " +
                "if (matrix[row][col] == 1) break; else col = col + 1; " +
                "} " +
                "do { " +
                "row = row + 1; " +
                "value = 3.14; " +
                "} while (!(row >= 2)); " +
                "}";
        String text = text3;
        SemanticResult result = demo(text, (iter, errCtx) -> {
            ProductionSetContext context = buildGrammar0();
            System.out.println(context);
            ShiftReduceParsingTable shiftReduceParsingTable = SyntaxDemo.buildShiftReduceParsingTable(
                    "compilation_unit",
                    context,
                    "syntax_table.data",
                    PROGRAM_SEMANTIC_TAG_LOADER
            );
            ShiftReducePhaser phaser = new ShiftReducePhaserImpl(
                    shiftReduceParsingTable,
                    t -> !SHOULD_BE_FILTERED.contains(t.getType()),
                    SemanticDemo.buildShiftReduceRegister()
            );
            return phaser.phase(iter, errCtx);
        });
    }

    public static SemanticResult demo(
            String text, BiFunction<SourceTokenIterator, ErrorContext, SemanticResult> syntaxPhaserMapper) {
        LexicalAnalyzer analyzer = ProgramLexicalDemo.lexicalAnalyzer();
        // resource
        Resource resource = new AsciiStringResource(text);
        // error context
        ErrorContext errorContext = new DefaultErrorContext();
        try (SourceTokenIterator iterator = analyzer.iterator(errorContext, resource)) {
            return syntaxPhaserMapper.apply(iterator, errorContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!errorContext.isEmpty()) {
                log.error("{}", errorContext);
            }
        }

    }


    private static ProductionSetContext build() {
        TerminalFactory terminalFactory = terminal -> new TokenTypeTerminalSymbol((TokenType) terminal);
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        // 开始符号 program ::= stmt_list
        contextBuilder.define("program")
                .alternateDefinition("stmt_list");

        // stmt_list ::= ε | stmt stmt_list
        contextBuilder.define("stmt_list")
                .alternateEpsilon()
                .alternateDefinition("stmt")
                .concatenateDefinitionLast("stmt_list");

        // stmt ::= matched_stmt | unmatched_stmt
        contextBuilder.define("stmt")
                .alternateDefinition("matched_stmt")
                .alternateDefinition("unmatched_stmt");

        // matched_stmt ::= declaration_stmt | assignment_stmt | matched_while_stmt | do_while_stmt
        //                | expr_stmt | block | empty_stmt | break_stmt | continue_stmt | matched_if_stmt
        contextBuilder.define("matched_stmt")
                .alternateDefinition("declaration_stmt")
                .alternateDefinition("assignment_stmt")
                .alternateDefinition("matched_while_stmt")
                .alternateDefinition("do_while_stmt")
                .alternateDefinition("expr_stmt")
                .alternateDefinition("block")
                .alternateDefinition("empty_stmt")
                .alternateDefinition("break_stmt")
                .alternateDefinition("continue_stmt")
                .alternateDefinition("matched_if_stmt");

        // unmatched_stmt ::= unmatched_if_stmt | unmatched_while_stmt
        contextBuilder.define("unmatched_stmt")
                .alternateDefinition("unmatched_if_stmt")
                .alternateDefinition("unmatched_while_stmt");

        // matched_if_stmt ::= if ( expr ) matched_stmt else matched_stmt
        contextBuilder.define("matched_if_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_IF)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateDefinitionLast("matched_stmt")
                .concatenateTerminalLast(ProgramTokenType.CONTROL_STRUCTURES_ELSE)
                .concatenateDefinitionLast("matched_stmt");

        // unmatched_if_stmt ::= if ( expr ) stmt
        //                    | if ( expr ) matched_stmt else unmatched_stmt
        contextBuilder.define("unmatched_if_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_IF)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateDefinitionLast("stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_IF)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateDefinitionLast("matched_stmt")
                .concatenateTerminalLast(ProgramTokenType.CONTROL_STRUCTURES_ELSE)
                .concatenateDefinitionLast("unmatched_stmt");

        // matched_while_stmt   ::= while ( expr ) matched_stmt
        contextBuilder.define("matched_while_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_WHILE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateDefinitionLast("matched_stmt");

        // unmatched_while_stmt ::= while ( expr ) unmatched_stmt
        contextBuilder.define("unmatched_while_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_WHILE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateDefinitionLast("unmatched_stmt");

        // do_while_stmt ::= do stmt while ( expr ) ;
        contextBuilder.define("do_while_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_DO)
                .concatenateDefinitionLast("stmt")
                .concatenateTerminalLast(ProgramTokenType.CONTROL_STRUCTURES_WHILE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        // declaration_stmt ::= type id = expr ; | type id ;
        contextBuilder.define("declaration_stmt")
                .alternateDefinition("type")
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_ASSIGN)
                .concatenateDefinitionLast("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON)
                .alternateDefinition("type")
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        // assignment_stmt ::= lvalue = expr ;
        contextBuilder.define("assignment_stmt")
                .alternateDefinition("lvalue")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_ASSIGN)
                .concatenateDefinitionLast("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        // lvalue ::= id | lvalue [ expr ]
        contextBuilder.define("lvalue")
                .alternateTerminal(ProgramTokenType.IDENTIFIER)
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SQUARE_OPEN)
                .concatenateDefinitionLast("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SQUARE_CLOSE);
        // expr_stmt ::= expr ;
        contextBuilder.define("expr_stmt")
                .alternateDefinition("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        // block ::= { stmt_list }
        contextBuilder.define("block")
                .alternateTerminal(ProgramTokenType.OPERATOR_BRACE_OPEN)
                .concatenateDefinitionLast("stmt_list")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_BRACE_CLOSE);

        // empty_stmt ::= ;
        contextBuilder.define("empty_stmt")
                .alternateTerminal(ProgramTokenType.OPERATOR_SEMICOLON);
        // break_stmt ::= break ;
        contextBuilder.define("break_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_BREAK)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);
        // continue_stmt ::= continue ;
        contextBuilder.define("continue_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_CONTINUE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        // expr ::= expr + term | term
        contextBuilder.define("expr")
                .alternateDefinition("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PLUS)
                .concatenateDefinitionLast("term")
                .alternateDefinition("term");

        // term ::= term * factor | factor
        contextBuilder.define("term")
                .alternateDefinition("term")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_MULTIPLY)
                .concatenateDefinitionLast("factor")
                .alternateDefinition("factor");

        // factor ::= primary
        contextBuilder.define("factor")
                .alternateDefinition("primary");

        // primary ::= lvalue | const_int | const_float | const_char | const_str
        //           | true | false | ( expr )
        contextBuilder.define("primary")
                .alternateDefinition("lvalue")
                .alternateTerminal(ProgramTokenType.CONSTANT_INTEGER)
                .alternateTerminal(ProgramTokenType.CONSTANT_FLOAT)
                .alternateTerminal(ProgramTokenType.CONSTANT_CHARACTER)
                .alternateTerminal(ProgramTokenType.CONSTANT_STRING)
                .alternateTerminal(ProgramTokenType.CONSTANT_BOOLEAN_TRUE)
                .alternateTerminal(ProgramTokenType.CONSTANT_BOOLEAN_FALSE)
                .alternateTerminal(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE);

        // type ::= boolean | char | int32 | float64 | string
        contextBuilder.define("type")
                .alternateTerminal(ProgramTokenType.TYPE_BOOLEAN)
                .alternateTerminal(ProgramTokenType.TYPE_CHARACTER)
                .alternateTerminal(ProgramTokenType.TYPE_INT32)
                .alternateTerminal(ProgramTokenType.TYPE_FLOAT64)
                .alternateTerminal(ProgramTokenType.TYPE_STRING);

        return contextBuilder.build();
    }

    public static ProductionSetContext buildGrammar0() {
        TerminalFactory terminalFactory = terminal -> new TokenTypeTerminalSymbol((TokenType) terminal);
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);

        contextBuilder.define("compilation_unit")
                .alternateDefinition("program");

        contextBuilder.define("program")
                .alternateDefinition("top_item")
                .alternateDefinition("top_item")
                .concatenateSelfLast();

        contextBuilder.define("top_item")
                .alternateDefinition("function_decl")
                .alternateDefinition("block_item");

        contextBuilder.define("function_decl")
                .alternateDefinition("function_head")
                .concatenateDefinitionLast("block");

        contextBuilder.define("function_head")
                .alternateDefinition("type")
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("param_list")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .alternateTerminal(ProgramTokenType.TYPE_VOID)
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("param_list")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE);

        contextBuilder.define("param_list")
                .alternateDefinition("params")
                .alternateEpsilon();

        contextBuilder.define("params")
                .alternateDefinition("params")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_COMMA)
                .concatenateDefinitionLast("param")
                .alternateDefinition("param");

        contextBuilder.define("param")
                .alternateDefinition("type")
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER);

        contextBuilder.define("block")
                .alternateTerminal(ProgramTokenType.OPERATOR_BRACE_OPEN)
                .concatenateDefinitionLast("block_items")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_BRACE_CLOSE);

        contextBuilder.define("block_items")
                .alternateSelf()
                .concatenateDefinitionLast("block_item")
                .alternateEpsilon();

        contextBuilder.define("block_item")
                .alternateDefinition("decl")
                .alternateDefinition("stmt");

        contextBuilder.define("decl")
                .alternateDefinition("decl_plain")
                .alternateDefinition("decl_init");

        contextBuilder.define("decl_plain")
                .alternateDefinition("type")
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        contextBuilder.define("decl_init")
                .alternateDefinition("type")
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_ASSIGN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        contextBuilder.define("type")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SQUARE_OPEN)
                .concatenateTerminalLast(ProgramTokenType.CONSTANT_INTEGER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SQUARE_CLOSE)
                .alternateTerminal(ProgramTokenType.TYPE_BOOLEAN)
                .alternateTerminal(ProgramTokenType.TYPE_CHARACTER)
                .alternateTerminal(ProgramTokenType.TYPE_INT32)
                .alternateTerminal(ProgramTokenType.TYPE_FLOAT64)
                .alternateTerminal(ProgramTokenType.TYPE_STRING);

        contextBuilder.define("stmt")
                .alternateDefinition("matched_stmt")
                .alternateDefinition("unmatched_stmt");

        contextBuilder.define("matched_stmt")
                .alternateDefinition("assign_stmt")
                .alternateDefinition("expr_stmt")
                .alternateDefinition("matched_while_stmt")
                .alternateDefinition("do_while_stmt")
                .alternateDefinition("break_stmt")
                .alternateDefinition("continue_stmt")
                .alternateDefinition("return_stmt")
                .alternateDefinition("block")
                .alternateDefinition("matched_if_stmt");

        contextBuilder.define("assign_stmt")
                .alternateDefinition("loc")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_ASSIGN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        contextBuilder.define("expr_stmt")
                .alternateDefinition("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        contextBuilder.define("do_while_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_DO)
                .concatenateDefinitionLast("stmt")
                .concatenateTerminalLast(ProgramTokenType.CONTROL_STRUCTURES_WHILE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        contextBuilder.define("break_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_BREAK)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        contextBuilder.define("continue_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_CONTINUE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        contextBuilder.define("return_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_RETURN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON)
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_RETURN)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        contextBuilder.define("unmatched_stmt")
                .alternateDefinition("unmatched_if_stmt")
                .alternateDefinition("unmatched_while_stmt");

        contextBuilder.define("matched_if_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_IF)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateDefinitionLast("matched_stmt")
                .concatenateTerminalLast(ProgramTokenType.CONTROL_STRUCTURES_ELSE)
                .concatenateDefinitionLast("matched_stmt");

        contextBuilder.define("unmatched_if_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_IF)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateDefinitionLast("stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_IF)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateDefinitionLast("matched_stmt")
                .concatenateTerminalLast(ProgramTokenType.CONTROL_STRUCTURES_ELSE)
                .concatenateDefinitionLast("unmatched_stmt");

        contextBuilder.define("matched_while_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_WHILE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateDefinitionLast("matched_stmt");

        contextBuilder.define("unmatched_while_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_WHILE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateDefinitionLast("unmatched_stmt");

        contextBuilder.define("loc")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SQUARE_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SQUARE_CLOSE)
                .alternateTerminal(ProgramTokenType.IDENTIFIER);

        contextBuilder.define("bool")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_LOGICAL_OR)
                .concatenateDefinitionLast("join")
                .alternateDefinition("join");

        contextBuilder.define("join")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_LOGICAL_AND)
                .concatenateDefinitionLast("equality")
                .alternateDefinition("equality");

        contextBuilder.define("equality")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_EQUAL)
                .concatenateDefinitionLast("rel")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_NOT_EQUAL)
                .concatenateDefinitionLast("rel")
                .alternateDefinition("rel");

        contextBuilder.define("rel")
                .alternateDefinition("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_LESS)
                .concatenateDefinitionLast("expr")
                .alternateDefinition("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_LESS_EQUAL)
                .concatenateDefinitionLast("expr")
                .alternateDefinition("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_GREATER_EQUAL)
                .concatenateDefinitionLast("expr")
                .alternateDefinition("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_GREATER)
                .concatenateDefinitionLast("expr")
                .alternateDefinition("expr");

        contextBuilder.define("expr")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PLUS)
                .concatenateDefinitionLast("term")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_MINUS)
                .concatenateDefinitionLast("term")
                .alternateDefinition("term");

        contextBuilder.define("term")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_MULTIPLY)
                .concatenateDefinitionLast("unary")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_DIVIDE)
                .concatenateDefinitionLast("unary")
                .alternateDefinition("unary");

        contextBuilder.define("unary")
                .alternateTerminal(ProgramTokenType.OPERATOR_LOGICAL_NOT)
                .concatenateDefinitionLast("unary")
                .alternateTerminal(ProgramTokenType.OPERATOR_MINUS)
                .concatenateDefinitionLast("unary")
                .alternateDefinition("factor");

        contextBuilder.define("factor")
                .alternateTerminal(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .alternateDefinition("call_expr")
                .alternateDefinition("loc")
                .alternateTerminal(ProgramTokenType.CONSTANT_INTEGER)
                .alternateTerminal(ProgramTokenType.CONSTANT_FLOAT)
                .alternateTerminal(ProgramTokenType.CONSTANT_BOOLEAN_TRUE)
                .alternateTerminal(ProgramTokenType.CONSTANT_BOOLEAN_FALSE);

        contextBuilder.define("call_expr")
                .alternateTerminal(ProgramTokenType.IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("arg_list")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE);

        contextBuilder.define("arg_list")
                .alternateDefinition("args")
                .alternateEpsilon();

        contextBuilder.define("args")
                .alternateDefinition("args")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_COMMA)
                .concatenateDefinitionLast("bool")
                .alternateDefinition("bool");

        return contextBuilder.build();
    }

}

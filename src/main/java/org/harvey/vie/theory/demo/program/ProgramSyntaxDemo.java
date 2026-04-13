package org.harvey.vie.theory.demo.program;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.demo.SemanticDemo;
import org.harvey.vie.theory.demo.SyntaxDemo;
import org.harvey.vie.theory.error.DefaultErrorContext;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.exception.CompileException;
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

    public static void main(String[] args) {
        SemanticResult result = demo("int32 i = 1 + 2;", (iter, errCtx) -> {
            ProductionSetContext context = build();
            System.out.println(context);
            ShiftReduceParsingTable shiftReduceParsingTable = SyntaxDemo.buildShiftReduceParsingTable(
                    "program", context);
            ShiftReducePhaser phaser = new ShiftReducePhaserImpl(
                    shiftReduceParsingTable,
                    t -> !SHOULD_BE_FILTERED.contains(t.getType()),
                    SemanticDemo.buildShiftReduceRegister()
            );
            return phaser.phase(iter, errCtx);
        });
    }

    public static SemanticResult demo(
            String text,
            BiFunction<SourceTokenIterator, ErrorContext, SemanticResult> syntaxPhaserMapper) {
        LexicalAnalyzer analyzer = ProgramLexicalDemo.lexicalAnalyzer();
        // resource
        Resource resource = new AsciiStringResource(text);
        // error context
        ErrorContext errorContext = new DefaultErrorContext();
        try (SourceTokenIterator iterator = analyzer.iterator(errorContext, resource)) {
            return syntaxPhaserMapper.apply(iterator, errorContext);
        } catch (CompileException e) {
            log.warn("compile error", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!errorContext.isEmpty()) {
                log.info("{}", errorContext);
            }
        }
        return null;
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
        //                | expr_stmt | block | empty_stmt | matched_if_stmt
        contextBuilder.define("matched_stmt")
                .alternateDefinition("declaration_stmt")
                .alternateDefinition("assignment_stmt")
                .alternateDefinition("matched_while_stmt")
                .alternateDefinition("do_while_stmt")
                .alternateDefinition("expr_stmt")
                .alternateDefinition("block")
                .alternateDefinition("empty_stmt")
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

        // lvalue ::= id lvalue_suffix
        contextBuilder.define("lvalue")
                .alternateTerminal(ProgramTokenType.IDENTIFIER)
                .concatenateDefinitionLast("lvalue_suffix");

        // lvalue_suffix ::= ε | [ expr ] lvalue_suffix
        contextBuilder.define("lvalue_suffix")
                .alternateEpsilon()
                .alternateTerminal(ProgramTokenType.OPERATOR_SQUARE_OPEN)
                .concatenateDefinitionLast("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SQUARE_CLOSE)
                .concatenateDefinitionLast("lvalue_suffix");

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
}

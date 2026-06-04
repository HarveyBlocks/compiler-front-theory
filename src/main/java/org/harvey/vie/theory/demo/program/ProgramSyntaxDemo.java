package org.harvey.vie.theory.demo.program;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.demo.SemanticDemo;
import org.harvey.vie.theory.demo.SyntaxDemo;
import org.harvey.vie.theory.error.DefaultErrorContext;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.io.resource.AsciiStringResource;
import org.harvey.vie.theory.io.resource.Resource;
import org.harvey.vie.theory.lexical.analysis.LexicalAnalyzer;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.semantic.context.SemanticResult;
import org.harvey.vie.theory.semantic.function.FunctionManager;
import org.harvey.vie.theory.semantic.function.FunctionReturnFlowAnalyzer;
import org.harvey.vie.theory.semantic.type.TypeResolver;
import org.harvey.vie.theory.semantic.value.ConstantResolver;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaser;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaserImpl;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContextBuilder;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContextBuilderImpl;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactory;
import org.harvey.vie.theory.syntax.grammar.symbol.TokenTypeTerminalSymbol;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTagComparator;

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
    public static final SemanticTagComparator<? super SemanticTag> PROGRAM_SEMANTIC_TAG_COMPARATOR = (o1, o2) -> {
        if (o1 == o2) {
            return 0;
        }
        if (o1 instanceof ProgramSemanticTag && o2 instanceof ProgramSemanticTag) {
            return ((ProgramSemanticTag) o1).compareTo((ProgramSemanticTag) o2);
        }
        throw new IllegalStateException("Can not compare on different semantic tag system. ");
    };
    public static final TypeResolver TYPE_RESOLVER = new ProgramTypeResolver();
    public static final ConstantResolver CONSTANT_RESOLVER = new ConstantResolver() {
        /**
         * 函数功能：将整数字面量词法单元解析为整数值。
         * 输入：
         * - token：整数字面量对应的源词法单元。
         * 输出：解析得到的整数值。
         */
        @Override
        public int integerLiteral(SourceToken token) {
            return Integer.parseInt(SourceTokenStringMapping.utf8(token));
        }
    };
    public static final FunctionManager FUNCTION_MANAGER = new FunctionManager(
            new FunctionReturnFlowAnalyzer()
    );

    /**
     * 函数功能：运行程序语法与语义分析演示入口。
     * 输入：
     * - args：命令行参数数组。
     * 输出：无。
     */
    public static void main(String[] args) {
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
                       "int32[] arr; " +
                       "boolean flag; " +
                       "arr = new int32[10]; " +
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
                       "int32[][] matrix; " +
                       "int32 row; " +
                       "int32 col; " +
                       "float64 value; " +
                       "matrix = new int32[2][3]; " +
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
                    PROGRAM_SEMANTIC_TAG_LOADER,
                    PROGRAM_SEMANTIC_TAG_COMPARATOR
            );
            ShiftReducePhaser phaser = new ShiftReducePhaserImpl(
                    shiftReduceParsingTable,
                    t -> !SHOULD_BE_FILTERED.contains(t.getType()),
                    SemanticDemo.buildShiftReduceRegister(),
                    SyntaxDemo.STRING_COMMAND_FACTORY,
                    TYPE_RESOLVER,
                    CONSTANT_RESOLVER,
                    FUNCTION_MANAGER
            );
            return phaser.phase(iter, errCtx);
        });
    }

    /**
     * 函数功能：对程序源文本执行词法分析并交给指定语法分析流程处理。
     * 输入：
     * - text：待分析的程序源文本。
     * - syntaxPhaserMapper：将词法单元迭代器和错误上下文映射为语义结果的函数。
     * 输出：语法分析得到的 SemanticResult。
     */
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


    /**
     * 函数功能：构建基础程序语法文法产生式上下文。
     * 输入：
     * - 无。
     * 输出：构建完成的 ProductionSetContext。
     */
    private static ProductionSetContext build() {
        TerminalFactory terminalFactory = terminal -> new TokenTypeTerminalSymbol((TokenType) terminal);
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        // 开始符号 program ::= stmt_list
        contextBuilder.define("program").alternateDefinition("stmt_list");

        // stmt_list ::= ε | stmt stmt_list
        contextBuilder.define("stmt_list")
                .alternateEpsilon()
                .alternateDefinition("stmt")
                .concatenateDefinitionLast("stmt_list");

        // stmt ::= matched_stmt | unmatched_stmt
        contextBuilder.define("stmt").alternateDefinition("matched_stmt").alternateDefinition("unmatched_stmt");

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
        contextBuilder.define("empty_stmt").alternateTerminal(ProgramTokenType.OPERATOR_SEMICOLON);
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
        contextBuilder.define("factor").alternateDefinition("primary");

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

    /**
     * 函数功能：构建完整程序语言语法与语义标签的产生式上下文。
     * 输入：
     * - 无。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext buildGrammar0() {
        TerminalFactory terminalFactory = terminal -> new TokenTypeTerminalSymbol((TokenType) terminal);
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);

        contextBuilder.define("compilation_unit", ProgramSemanticTag.PROGRAM).alternateDefinition("program");

        contextBuilder.define("program", ProgramSemanticTag.PROGRAM)
                .alternateDefinition("top_item")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("top_item")
                .concatenateSelfLast();

        contextBuilder.define("top_item")
                .alternateDefinition("struct_decl")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("function_decl")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("block_item")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("struct_decl", ProgramSemanticTag.STRUCT_DECL)
                .alternateTerminal(ProgramTokenType.KEYWORD_STRUCT)
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_BRACE_OPEN)
                .concatenateDefinitionLast("struct_field_list")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_BRACE_CLOSE);

        contextBuilder.define("struct_field_list", ProgramSemanticTag.LIST, ProgramSemanticTag.STRUCT_FIELD)
                .alternateDefinition("struct_fields")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateEpsilon()
                .tagLast(ProgramSemanticTag.EMPTY);

        contextBuilder.define("struct_fields", ProgramSemanticTag.LIST, ProgramSemanticTag.STRUCT_FIELD)
                .alternateDefinition("struct_fields")
                .concatenateDefinitionLast("struct_field")
                .tagLast(ProgramSemanticTag.SEQUENCE)
                .alternateDefinition("struct_field")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("struct_field", ProgramSemanticTag.STRUCT_FIELD)
                .alternateDefinition("type")
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON)
                .tagLast(ProgramSemanticTag.IDENTIFIER);

        contextBuilder.define("function_decl", ProgramSemanticTag.FUNCTION, ProgramSemanticTag.DEFINITION)
                .alternateDefinition("function_head")
                .concatenateDefinitionLast("block")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("function_head", ProgramSemanticTag.FUNCTION, ProgramSemanticTag.HEAD)
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

        contextBuilder.define("param_list", ProgramSemanticTag.LIST, ProgramSemanticTag.PARAMETER)
                .alternateDefinition("params")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateEpsilon()
                .tagLast(ProgramSemanticTag.EMPTY);

        contextBuilder.define("params", ProgramSemanticTag.LIST, ProgramSemanticTag.PARAMETER)
                .alternateDefinition("params")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_COMMA)
                .concatenateDefinitionLast("param")
                .tagLast(ProgramSemanticTag.SEQUENCE)
                .alternateDefinition("param")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("param", ProgramSemanticTag.PARAMETER)
                .alternateDefinition("type")
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER)
                .tagLast(ProgramSemanticTag.IDENTIFIER);

        contextBuilder.define("block", ProgramSemanticTag.BLOCK)
                .alternateTerminal(ProgramTokenType.OPERATOR_BRACE_OPEN)
                .concatenateDefinitionLast("block_items")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_BRACE_CLOSE)
                .tagLast(ProgramSemanticTag.COMMAND);

        contextBuilder.define("block_items", ProgramSemanticTag.BLOCK, ProgramSemanticTag.LIST)
                .alternateSelf()
                .concatenateDefinitionLast("block_item")
                .tagLast(ProgramSemanticTag.SEQUENCE)
                .alternateEpsilon()
                .tagLast(ProgramSemanticTag.EMPTY);

        contextBuilder.define("block_item", ProgramSemanticTag.ITEM)
                .alternateDefinition("decl")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("stmt")
                .tagLast(ProgramSemanticTag.STATEMENT, ProgramSemanticTag.FORWARD);

        contextBuilder.define("decl")
                .alternateDefinition("decl_plain")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("decl_init")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("decl_plain", ProgramSemanticTag.DECLARATION)
                .alternateDefinition("type")
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON)
                .tagLast(ProgramSemanticTag.IDENTIFIER);

        contextBuilder.define("decl_init", ProgramSemanticTag.DECLARATION, ProgramSemanticTag.INITIALIZED)
                .alternateDefinition("type")
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_ASSIGN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON)
                .tagLast(ProgramSemanticTag.IDENTIFIER, ProgramSemanticTag.VALUE);

        contextBuilder.define("type", ProgramSemanticTag.TYPE)
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SQUARE_OPEN)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SQUARE_CLOSE)
                .tagLast(ProgramSemanticTag.ARRAY)
                .alternateTerminal(ProgramTokenType.TYPE_IDENTIFIER)
                .tagLast(ProgramSemanticTag.STRUCT_TYPE)
                .alternateTerminal(ProgramTokenType.TYPE_BOOLEAN)
                .alternateTerminal(ProgramTokenType.TYPE_CHARACTER)
                .alternateTerminal(ProgramTokenType.TYPE_INT32)
                .alternateTerminal(ProgramTokenType.TYPE_FLOAT64)
                .alternateTerminal(ProgramTokenType.TYPE_STRING);

        contextBuilder.define("stmt", ProgramSemanticTag.STATEMENT)
                .alternateDefinition("matched_stmt")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("unmatched_stmt")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("matched_stmt", ProgramSemanticTag.STATEMENT)
                .alternateDefinition("assign_stmt")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("expr_stmt")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("matched_while_stmt")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("do_while_stmt")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("break_stmt")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("continue_stmt")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("return_stmt")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("block")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("matched_if_stmt")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("assign_stmt", ProgramSemanticTag.ASSIGNMENT)
                .alternateDefinition("loc")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_ASSIGN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON)
                .tagLast(ProgramSemanticTag.VALUE);

        contextBuilder.define("expr_stmt")
                .alternateDefinition("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON)
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("do_while_stmt", ProgramSemanticTag.LOOP, ProgramSemanticTag.DO_LOOP)
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_DO)
                .concatenateDefinitionLast("stmt")
                .concatenateTerminalLast(ProgramTokenType.CONTROL_STRUCTURES_WHILE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        contextBuilder.define("break_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_BREAK)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON)
                .tagLast(ProgramSemanticTag.NOOP);

        contextBuilder.define("continue_stmt")
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_CONTINUE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON)
                .tagLast(ProgramSemanticTag.NOOP);

        contextBuilder.define("return_stmt", ProgramSemanticTag.RETURN)
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_RETURN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON)
                .tagLast(ProgramSemanticTag.VALUE)
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_RETURN)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SEMICOLON);

        contextBuilder.define("unmatched_stmt", ProgramSemanticTag.STATEMENT)
                .alternateDefinition("unmatched_if_stmt")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("unmatched_while_stmt")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("matched_if_stmt", ProgramSemanticTag.CONDITIONAL, ProgramSemanticTag.ELSE_BRANCH)
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_IF)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateDefinitionLast("matched_stmt")
                .concatenateTerminalLast(ProgramTokenType.CONTROL_STRUCTURES_ELSE)
                .concatenateDefinitionLast("matched_stmt");

        contextBuilder.define("unmatched_if_stmt", ProgramSemanticTag.CONDITIONAL)
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
                .concatenateDefinitionLast("unmatched_stmt")
                .tagLast(ProgramSemanticTag.ELSE_BRANCH);

        contextBuilder.define("matched_while_stmt", ProgramSemanticTag.LOOP)
                .alternateTerminal(ProgramTokenType.CONTROL_STRUCTURES_WHILE)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .concatenateDefinitionLast("matched_stmt");

        contextBuilder.define("unmatched_while_stmt", ProgramSemanticTag.LOOP)
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
                .tagLast(ProgramSemanticTag.ACCESS)
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_DOT)
                .concatenateTerminalLast(ProgramTokenType.IDENTIFIER)
                .tagLast(ProgramSemanticTag.MEMBER_ACCESS)
                .alternateTerminal(ProgramTokenType.IDENTIFIER)
                .tagLast(ProgramSemanticTag.IDENTIFIER, ProgramSemanticTag.USE);

        contextBuilder.define("bool")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_LOGICAL_OR)
                .concatenateDefinitionLast("join")
                .tagLast(ProgramSemanticTag.OR)
                .alternateDefinition("join")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("join")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_LOGICAL_AND)
                .concatenateDefinitionLast("equality")
                .tagLast(ProgramSemanticTag.AND)
                .alternateDefinition("equality")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("equality")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_EQUAL)
                .concatenateDefinitionLast("rel")
                .tagLast(ProgramSemanticTag.EQUAL)
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_NOT_EQUAL)
                .concatenateDefinitionLast("rel")
                .tagLast(ProgramSemanticTag.NOT_EQUAL)
                .alternateDefinition("rel")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("rel")
                .alternateDefinition("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_LESS)
                .concatenateDefinitionLast("expr")
                .tagLast(ProgramSemanticTag.LESS)
                .alternateDefinition("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_LESS_EQUAL)
                .concatenateDefinitionLast("expr")
                .tagLast(ProgramSemanticTag.LESS_EQUAL)
                .alternateDefinition("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_GREATER_EQUAL)
                .concatenateDefinitionLast("expr")
                .tagLast(ProgramSemanticTag.GREATER_EQUAL)
                .alternateDefinition("expr")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_GREATER)
                .concatenateDefinitionLast("expr")
                .tagLast(ProgramSemanticTag.GREATER)
                .alternateDefinition("expr")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("expr")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PLUS)
                .concatenateDefinitionLast("term")
                .tagLast(ProgramSemanticTag.PLUS)
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_MINUS)
                .concatenateDefinitionLast("term")
                .tagLast(ProgramSemanticTag.MINUS)
                .alternateDefinition("term")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("term")
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_MULTIPLY)
                .concatenateDefinitionLast("unary")
                .tagLast(ProgramSemanticTag.MULTIPLY)
                .alternateSelf()
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_DIVIDE)
                .concatenateDefinitionLast("unary")
                .tagLast(ProgramSemanticTag.DIVIDE)
                .alternateDefinition("unary")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("unary")
                .alternateTerminal(ProgramTokenType.OPERATOR_LOGICAL_NOT)
                .concatenateDefinitionLast("unary")
                .tagLast(ProgramSemanticTag.LOGICAL_NOT)
                .alternateTerminal(ProgramTokenType.OPERATOR_MINUS)
                .concatenateDefinitionLast("unary")
                .tagLast(ProgramSemanticTag.NEGATE)
                .alternateDefinition("factor")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("factor")
                .alternateTerminal(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE)
                .tagLast(ProgramSemanticTag.PARENTHESIZED)
                .alternateDefinition("new_struct_expr")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("new_array_expr")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("call_expr")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateDefinition("loc")
                .tagLast(ProgramSemanticTag.LEFT_VALUE)
                .alternateTerminal(ProgramTokenType.CONSTANT_INTEGER)
                .tagLast(ProgramSemanticTag.LITERAL)
                .alternateTerminal(ProgramTokenType.CONSTANT_FLOAT)
                .tagLast(ProgramSemanticTag.LITERAL)
                .alternateTerminal(ProgramTokenType.CONSTANT_BOOLEAN_TRUE)
                .tagLast(ProgramSemanticTag.LITERAL)
                .alternateTerminal(ProgramTokenType.CONSTANT_BOOLEAN_FALSE)
                .tagLast(ProgramSemanticTag.LITERAL)
                .alternateTerminal(ProgramTokenType.CONSTANT_NULL)
                .tagLast(ProgramSemanticTag.NULL_LITERAL);

        contextBuilder.define("new_struct_expr", ProgramSemanticTag.NEW_STRUCT)
                .alternateTerminal(ProgramTokenType.KEYWORD_NEW)
                .concatenateTerminalLast(ProgramTokenType.TYPE_IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE);

        contextBuilder.define("new_array_expr", ProgramSemanticTag.NEW_ARRAY)
                .alternateTerminal(ProgramTokenType.KEYWORD_NEW)
                .concatenateDefinitionLast("array_creation_base")
                .concatenateDefinitionLast("array_creation_dims");

        contextBuilder.define("array_creation_base", ProgramSemanticTag.ARRAY_CREATION_BASE)
                .alternateTerminal(ProgramTokenType.TYPE_IDENTIFIER)
                .tagLast(ProgramSemanticTag.STRUCT_TYPE)
                .alternateTerminal(ProgramTokenType.TYPE_BOOLEAN)
                .alternateTerminal(ProgramTokenType.TYPE_CHARACTER)
                .alternateTerminal(ProgramTokenType.TYPE_INT32)
                .alternateTerminal(ProgramTokenType.TYPE_FLOAT64)
                .alternateTerminal(ProgramTokenType.TYPE_STRING);

        contextBuilder.define("array_creation_dims", ProgramSemanticTag.LIST, ProgramSemanticTag.ARRAY_CREATION_DIM)
                .alternateDefinition("array_creation_dims")
                .concatenateDefinitionLast("array_creation_dim")
                .tagLast(ProgramSemanticTag.SEQUENCE)
                .alternateDefinition("array_creation_dim")
                .tagLast(ProgramSemanticTag.FORWARD);

        contextBuilder.define("array_creation_dim", ProgramSemanticTag.ARRAY_CREATION_DIM)
                .alternateTerminal(ProgramTokenType.OPERATOR_SQUARE_OPEN)
                .concatenateDefinitionLast("bool")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SQUARE_CLOSE)
                .tagLast(ProgramSemanticTag.VALUE)
                .alternateTerminal(ProgramTokenType.OPERATOR_SQUARE_OPEN)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_SQUARE_CLOSE);

        contextBuilder.define("call_expr", ProgramSemanticTag.FUNCTION, ProgramSemanticTag.CALL)
                .alternateTerminal(ProgramTokenType.IDENTIFIER)
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_OPEN)
                .concatenateDefinitionLast("arg_list")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE);

        contextBuilder.define("arg_list", ProgramSemanticTag.LIST, ProgramSemanticTag.ARGUMENT)
                .alternateDefinition("args")
                .tagLast(ProgramSemanticTag.FORWARD)
                .alternateEpsilon()
                .tagLast(ProgramSemanticTag.EMPTY);

        contextBuilder.define("args", ProgramSemanticTag.LIST, ProgramSemanticTag.ARGUMENT)
                .alternateDefinition("args")
                .concatenateTerminalLast(ProgramTokenType.OPERATOR_COMMA)
                .concatenateDefinitionLast("bool")
                .tagLast(ProgramSemanticTag.SEQUENCE)
                .alternateDefinition("bool")
                .tagLast(ProgramSemanticTag.VALUE, ProgramSemanticTag.FORWARD);

        return contextBuilder.build();
    }

}

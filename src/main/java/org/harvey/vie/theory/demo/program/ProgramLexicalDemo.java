package org.harvey.vie.theory.demo.program;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.demo.LexicalDemo;
import org.harvey.vie.theory.lexical.DefaultLexicalDirector;
import org.harvey.vie.theory.lexical.LexicalDirector;
import org.harvey.vie.theory.lexical.LexicalPattern;
import org.harvey.vie.theory.lexical.RegexCharSet;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacterFactory;
import org.harvey.vie.theory.lexical.alphabet.RegexAlphabetCharacterFactory;
import org.harvey.vie.theory.lexical.alphabet.SourceAlphabetCharacterAdaptorImpl;
import org.harvey.vie.theory.lexical.analysis.DefaultLexicalAnalyzer;
import org.harvey.vie.theory.lexical.analysis.LexicalAnalyzer;
import org.harvey.vie.theory.lexical.dfa.status.RegexDfaStatusTable;

import java.io.*;
import java.text.ParseException;
import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-13 12:36
 */
@Slf4j
public class ProgramLexicalDemo {

    private static final RegexCharSet WHITESPACE_NEWLINE = RegexCharSet.of("\n", "\r");
    private static final RegexCharSet WHITESPACE_WITHOUT_NEWLINE = RegexCharSet.of(" ", "\t", "\f");
    private static final RegexCharSet WHITESPACE = RegexCharSet.unionAll(
            WHITESPACE_NEWLINE, WHITESPACE_WITHOUT_NEWLINE
    );
    private static final RegexCharSet NON_ZERO_DIGIT = RegexCharSet.of("1", "2", "3", "4", "5", "6", "7", "8", "9");
    private static final RegexCharSet DIGIT = RegexCharSet.unionAll(RegexCharSet.of("0"), NON_ZERO_DIGIT);
    private static final RegexCharSet LOWER_LETTER = RegexCharSet.of(
            "a", "b", "c", "d", "e", "f", "g", "h", "i",
            "j", "k", "l", "m", "n", "o", "p", "q", "r",
            "s", "t", "u", "v", "w", "x", "y", "z"
    );
    private static final RegexCharSet UPPER_LETTER = RegexCharSet.of(
            "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z"
    );
    private static final RegexCharSet OPERATOR = RegexCharSet.of(
            "!", "%", "^", "&", "\\*", "\\(", "\\)", "-", "+",
            "=", "{", "}", "[", "]", "\\|", "\\\\", ":", ";",
            "\"", "'", "<", ">", ",", ".", "?", "/"
    );
    private static final RegexCharSet OTHER = RegexCharSet.of("@", "#", "$", "_");
    private static final RegexCharSet ANY_WITHOUT_NEWLINE = RegexCharSet.unionAll(
            DIGIT, WHITESPACE_WITHOUT_NEWLINE, LOWER_LETTER, UPPER_LETTER, OPERATOR, OTHER
    );
    private static final RegexCharSet ANY = RegexCharSet.unionAll(
            DIGIT, WHITESPACE, LOWER_LETTER, UPPER_LETTER, OPERATOR, OTHER
    );
    private static final boolean FLUSH_TABLE = false;

    private static final List<LexicalPattern> REGEX_PATTERNS = List.of(
            new LexicalPattern("" +
                               RegexCharSet.unionAll(UPPER_LETTER, LOWER_LETTER, RegexCharSet.of("_")) +
                               RegexCharSet.unionAll(UPPER_LETTER, LOWER_LETTER, DIGIT, RegexCharSet.of("_")) +
                               "*", ProgramTokenType.IDENTIFIER),
            new LexicalPattern(WHITESPACE + "" + WHITESPACE + "*", ProgramTokenType.SPACE),
            new LexicalPattern(
                    "/\\*(" + ANY.exclude("\\*") + "|\\*" + ANY.exclude("/") + ")*\\**\\*/",
                    ProgramTokenType.COMMENT_BLOCK
            ),
            new LexicalPattern("//" + ANY_WITHOUT_NEWLINE + "*\n", ProgramTokenType.COMMENT_LINE),
            new LexicalPattern("\"(" +
                               ANY_WITHOUT_NEWLINE.exclude("\"", "\\\\") +
                               "|(\\\\" +
                               RegexCharSet.of("n", "r", "t", "'", "\"") +
                               "))*" +
                               '"', ProgramTokenType.CONSTANT_STRING),
            new LexicalPattern("'(" +
                               ANY_WITHOUT_NEWLINE.exclude("'", "\\\\") +
                               "|(\\\\" +
                               RegexCharSet.of("n", "r", "t", "'", "\"") +
                               "))'", ProgramTokenType.CONSTANT_CHARACTER),
            new LexicalPattern("(" + NON_ZERO_DIGIT + DIGIT + "*)|0", ProgramTokenType.CONSTANT_INTEGER),
            new LexicalPattern("((" + NON_ZERO_DIGIT + DIGIT + "*)|0)." + DIGIT + "*", ProgramTokenType.CONSTANT_FLOAT),
            new LexicalPattern("true", ProgramTokenType.CONSTANT_BOOLEAN_TRUE),
            new LexicalPattern("false", ProgramTokenType.CONSTANT_BOOLEAN_FALSE),
            new LexicalPattern("boolean", ProgramTokenType.TYPE_BOOLEAN),
            new LexicalPattern("char", ProgramTokenType.TYPE_CHARACTER),
            new LexicalPattern("int32", ProgramTokenType.TYPE_INT32),
            new LexicalPattern("float64", ProgramTokenType.TYPE_FLOAT64),
            new LexicalPattern("string", ProgramTokenType.TYPE_STRING),
            new LexicalPattern("+", ProgramTokenType.OPERATOR_PLUS),
            new LexicalPattern("\\*", ProgramTokenType.OPERATOR_MULTIPLY),
            new LexicalPattern("\\(", ProgramTokenType.OPERATOR_PARENTHESIS_OPEN),
            new LexicalPattern("\\)", ProgramTokenType.OPERATOR_PARENTHESIS_CLOSE),
            new LexicalPattern("=", ProgramTokenType.OPERATOR_ASSIGN),
            new LexicalPattern(";", ProgramTokenType.OPERATOR_SEMICOLON),
            new LexicalPattern("[", ProgramTokenType.OPERATOR_SQUARE_OPEN),
            new LexicalPattern("]", ProgramTokenType.OPERATOR_SQUARE_CLOSE),
            new LexicalPattern("{", ProgramTokenType.OPERATOR_BRACE_OPEN),
            new LexicalPattern("}", ProgramTokenType.OPERATOR_BRACE_CLOSE),
            new LexicalPattern("if", ProgramTokenType.CONTROL_STRUCTURES_IF),
            new LexicalPattern("else", ProgramTokenType.CONTROL_STRUCTURES_ELSE),
            new LexicalPattern("while", ProgramTokenType.CONTROL_STRUCTURES_WHILE),
            new LexicalPattern("do", ProgramTokenType.CONTROL_STRUCTURES_DO)
    );

    public static void main(String[] args) {
        // 编写正则会比较考验技巧
        // 由于总是饿汉模式去匹配, 因此, 必须特别设计正则
        // 正则只支持 ε, 连接, or 和 closure.
        // 括号>closure(*)>连接>or(|)
        // 状态转移表构造起来极慢, 注意多使用持久化的功能, 可以快很多
        extracted();
    }

    private static void extracted() {
        AlphabetCharacterFactory alphabetCharacterFactory = new RegexAlphabetCharacterFactory();
        RegexDfaStatusTable table = buildTable(alphabetCharacterFactory);
        String text = "/*\n" +
                      "block\n" +
                      "*/ \n" +
                      "\"string\"\n" +
                      "// line \n" +
                      "'\\n'\n" +
                      "int32 b = 556+0+1+2+3+4+123+312+331+0.123+12.31+213;\n" +
                      "int32 a = 32+453+b;\n" +
                      "string str = \"str\"+a+b;\n" +
                      " ";
        text = "int32 i = 1 + 2;";
        LexicalDemo.testLexical(text, alphabetCharacterFactory, table);
    }

    private static RegexDfaStatusTable buildStatusTable(AlphabetCharacterFactory alphabetCharacterFactory)
            throws IOException {
        LexicalDirector director = new DefaultLexicalDirector(alphabetCharacterFactory);
        RegexDfaStatusTable table;
        try {
            table = director.direct(REGEX_PATTERNS);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        try (OutputStream os = new FileOutputStream("src/main/resources/serial/lex_table.data")) {
            log.info("{}", table);
            int store = table.store(os);
            os.flush();
            log.info("store = {}", store);
            return table;
        }
    }

    public static LexicalAnalyzer lexicalAnalyzer() {
        AlphabetCharacterFactory alphabetCharacterFactory = new RegexAlphabetCharacterFactory();
        RegexDfaStatusTable table = buildTable(alphabetCharacterFactory);
        SourceAlphabetCharacterAdaptorImpl saca = new SourceAlphabetCharacterAdaptorImpl(alphabetCharacterFactory);
        return new DefaultLexicalAnalyzer(table, saca);
    }

    private static RegexDfaStatusTable buildTable(AlphabetCharacterFactory alphabetCharacterFactory) {
        RegexDfaStatusTable table;
        try (InputStream is = new FileInputStream("src/main/resources/serial/lex_table.data")) {
            if (FLUSH_TABLE) {
                table = buildStatusTable(alphabetCharacterFactory);
            }
            RegexDfaStatusTable.Loader loader = new RegexDfaStatusTable.Loader(
                    new ProgramTokenType.Loader(),
                    alphabetCharacterFactory
            );
            table = loader.load(is);
            log.info("loaded = {}", table);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return table;
    }
}

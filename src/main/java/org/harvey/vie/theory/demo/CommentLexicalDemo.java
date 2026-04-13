package org.harvey.vie.theory.demo;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.lexical.DefaultLexicalDirector;
import org.harvey.vie.theory.lexical.LexicalDirector;
import org.harvey.vie.theory.lexical.LexicalPattern;
import org.harvey.vie.theory.lexical.RegexCharSet;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacterFactory;
import org.harvey.vie.theory.lexical.alphabet.RegexAlphabetCharacterFactory;
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
public class CommentLexicalDemo {

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

    public static void main(String[] args) {
        // 编写正则会比较考验技巧
        // 由于总是饿汉模式去匹配, 因此, 必须特别设计正则
        // 正则只支持 ε, 连接, or 和 closure.
        // 括号>closure(*)>连接>or(|)
        // 状态转移表构造起来极慢, 注意多使用持久化的功能, 可以快很多
        extracted1();
    }

    private static void extracted1() {
        AlphabetCharacterFactory alphabetCharacterFactory = new RegexAlphabetCharacterFactory();
        RegexDfaStatusTable table;
        try (InputStream is = new FileInputStream("src/main/resources/serial/lex_table.data")) {
            TempType[] types = new TempType[]{new TempType(0, 1, "SPACE"), new TempType(1, 2, "BLOCK_COMMIT"),
                    new TempType(2, 1, "LINE_COMMIT"), new TempType(3, 1, "STRING_CONSTANT"),
                    new TempType(4, 1, "CHARACTER_CONSTANT")};
            // table = buildStatusTable(alphabetCharacterFactory, types);
            RegexDfaStatusTable.Loader loader = new RegexDfaStatusTable.Loader(
                    new TempType.Loader(types),
                    alphabetCharacterFactory
            );
            table = loader.load(is);
            log.info("loaded = {}", table);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String text = "/*\n" + "block\n" + "*/ \n" + "\"string\"\n" + "// line \n" + "'c'\n";
        LexicalDemo.testLexical(text, alphabetCharacterFactory, table);
    }

    private static RegexDfaStatusTable buildStatusTable(
            AlphabetCharacterFactory alphabetCharacterFactory, TempType[] types) throws IOException {
        LexicalDirector director = new DefaultLexicalDirector(alphabetCharacterFactory);
        RegexDfaStatusTable table;
        try {
            String charRegex = "'(" +
                               ANY_WITHOUT_NEWLINE.exclude("'", "\\\\") +
                               "|(\\\\" +
                               RegexCharSet.of("n", "r", "t", "'", "\"") +
                               "))'";
            table = director.direct(List.of(
                    new LexicalPattern(WHITESPACE + "" + WHITESPACE + "*", types[0]),
                    new LexicalPattern(
                            "/\\*(" + ANY.exclude("\\*") + "|\\*" + ANY.exclude("/") + ")*\\**\\*/",
                            types[1]
                    ),
                    new LexicalPattern("//" + ANY_WITHOUT_NEWLINE + "*\n", types[2]),
                    new LexicalPattern("\"(" +
                                       ANY_WITHOUT_NEWLINE.exclude("\"", "\\\\") +
                                       "|(\\\\" +
                                       RegexCharSet.of("n", "r", "t", "'", "\"") +
                                       "))*" +
                                       '"', types[3]),
                    new LexicalPattern(charRegex, types[4])
            ));
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

}

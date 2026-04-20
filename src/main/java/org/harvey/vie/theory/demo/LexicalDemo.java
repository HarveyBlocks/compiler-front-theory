package org.harvey.vie.theory.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.error.DefaultErrorContext;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.io.Loaders;
import org.harvey.vie.theory.io.Storages;
import org.harvey.vie.theory.io.resource.AsciiStringResource;
import org.harvey.vie.theory.io.resource.Resource;
import org.harvey.vie.theory.lexical.DefaultLexicalDirector;
import org.harvey.vie.theory.lexical.LexicalDirector;
import org.harvey.vie.theory.lexical.LexicalPattern;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacterFactory;
import org.harvey.vie.theory.lexical.alphabet.RegexAlphabetCharacterFactory;
import org.harvey.vie.theory.lexical.alphabet.SourceAlphabetCharacterAdaptorImpl;
import org.harvey.vie.theory.lexical.analysis.DefaultLexicalAnalyzer;
import org.harvey.vie.theory.lexical.analysis.LexicalAnalyzer;
import org.harvey.vie.theory.lexical.analysis.token.AbstractTokenType;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.dfa.status.RegexDfaStatusTable;

import java.io.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 词法分析器Demo
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:19
 */
@Slf4j
public class LexicalDemo {
    public static RegexDfaStatusTable buildTable(AlphabetCharacterFactory alphabetCharacterFactory) {
        LexicalDirector director = new DefaultLexicalDirector(alphabetCharacterFactory);
        RegexDfaStatusTable table;
        TempType[] types = new TempType[]{
                new TempType(0, 1, "space"), new TempType(1, 1, "id"),
                new TempType(2, 1, "+"), new TempType(3, 1, "*"),
                new TempType(4, 1, "("), new TempType(5, 1, ")"),
                new TempType(6, 1, "<"), new TempType(7, 1, ">"),
                new TempType(8, 1, ",")
        };
        try {
            table = director.direct(List.of(
                    new LexicalPattern(" ", types[0]),// 1个或多个空格,
                    new LexicalPattern("id", types[1]),
                    new LexicalPattern("+", types[2]),
                    new LexicalPattern("\\*", types[3]),
                    new LexicalPattern("\\(", types[4]),
                    new LexicalPattern("\\)", types[5]),
                    new LexicalPattern("<", types[6]),
                    new LexicalPattern(">", types[7]),
                    new LexicalPattern(",", types[8])
            ));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        log.info("{}", table);
        return table;
    }

    public static void main(String[] args) {
        extracted();
    }

    public static void testLexical(
            String text, AlphabetCharacterFactory alphabetCharacterFactory, RegexDfaStatusTable table) {
        Resource resource = new AsciiStringResource(text);
        ErrorContext errorContext = new DefaultErrorContext();
        SourceAlphabetCharacterAdaptorImpl saca = new SourceAlphabetCharacterAdaptorImpl(alphabetCharacterFactory);
        LexicalAnalyzer analyzer = new DefaultLexicalAnalyzer(table, saca);
        try (SourceTokenIterator iterator = analyzer.iterator(errorContext, resource)) {
            while (iterator.hasNext()) {
                SourceToken next = iterator.next();
                log.info("hint: {}, value: {}", next.hintString(), next.getLexeme());
            }
            log.info("yes");
        } catch (CompileException e) {
            log.warn("compile error", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!errorContext.isEmpty()) {
                log.info("{}", errorContext);
            }
        }
    }

    private static void extracted() {
        // region 1. RegexDfaStatusTable
        AlphabetCharacterFactory alphabetCharacterFactory = new RegexAlphabetCharacterFactory();
        LexicalDirector director = new DefaultLexicalDirector(alphabetCharacterFactory);
        RegexDfaStatusTable table;
        TempType[] types = new TempType[]{new TempType(0, 1, "A"), new TempType(1, 2, "B"), new TempType(2, 1, "C"),
                new TempType(3, 1, "D"), new TempType(4, 1, "E")};
        try {
            table = director.direct(List.of(
                    new LexicalPattern(" ", types[0]),
                    new LexicalPattern("(1|2|3)(1|2|3)*(7|8|9)*", types[1]),
                    new LexicalPattern("(7|8|9)(4|5|6)*(a|b|c)*", types[2]),
                    new LexicalPattern("(4|5|6)(7|8|9)*(1|2|3)*", types[3]),
                    new LexicalPattern("(a|b|c)(a|b|c)*(4|5|6)*", types[4])
            ));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        log.info("{}", table);
        // endregion

        // region 2. store and load
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            int store = table.store(os);
            log.info("store = {}", store);
            RegexDfaStatusTable.Loader loader = new RegexDfaStatusTable.Loader(
                    new TempType.Loader(types),
                    alphabetCharacterFactory
            );
            RegexDfaStatusTable loaded = loader.load(new ByteArrayInputStream(os.toByteArray()));
            log.info("loaded = {}", loaded);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // endregion

        // region 3. LexicalAnalyzer
        testLexical("aaa112441 123333aaa", alphabetCharacterFactory, table);
        // endregion
    }
}


package org.harvey.vie.theory;

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
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusTable;

import java.io.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 启动类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:19
 */
@Slf4j
public class Main {


    public static void main(String[] args) {
        // region 1. DfaStatusTable
        AlphabetCharacterFactory alphabetCharacterFactory = new RegexAlphabetCharacterFactory();
        LexicalDirector director = new DefaultLexicalDirector(alphabetCharacterFactory);
        DfaStatusTable table;
        TempType[] types = new TempType[]{
                new TempType(0, 1, "A"),
                new TempType(1, 2, "B"),
                new TempType(2, 1, "C"),
                new TempType(3, 1, "D"),
                new TempType(4, 1, "E")
        };
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
            DfaStatusTable.Loader loader = new DfaStatusTable.Loader(
                    new TempType.Loader(types),
                    alphabetCharacterFactory
            );
            DfaStatusTable loaded = loader.load(new ByteArrayInputStream(os.toByteArray()));
            log.info("loaded = {}", loaded);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // endregion

        // region 3. LexicalAnalyzer
        Resource resource = new AsciiStringResource("aaa112441 123333aaa");
        ErrorContext errorContext = new DefaultErrorContext();
        SourceAlphabetCharacterAdaptorImpl saca = new SourceAlphabetCharacterAdaptorImpl(alphabetCharacterFactory);
        LexicalAnalyzer analyzer = new DefaultLexicalAnalyzer(table, saca);
        try (SourceTokenIterator iterator = analyzer.iterator(errorContext, resource)) {
            while (iterator.hasNext()) {
                SourceToken next = iterator.next();
                log.info(next.hintString());
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
        // endregion
    }
}

@AllArgsConstructor
class TempType extends AbstractTokenType {
    private final int id;
    @Getter
    private final int priority;
    private final String s;

    @Override
    public @NonNull String hint() {
        return s;
    }

    @Override
    public int store(OutputStream os) throws IOException {
        return Storages.storeInteger(os, id);
    }

    static class Loader implements TokenType.Loader<TempType> {
        private final Map<Integer, TempType> map;

        Loader(TempType... types) {
            this.map = Arrays.stream(types).collect(Collectors.toMap(t -> t.id, t -> t));
        }

        /**
         * @throws IOException null for unknown token
         */
        @Override
        public TempType load(InputStream is) throws IOException {
            int id = Loaders.loadInteger(is);
            return map.get(id);
        }
    }
}
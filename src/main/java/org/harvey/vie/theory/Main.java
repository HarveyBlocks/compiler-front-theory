package org.harvey.vie.theory;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.error.DefaultErrorContext;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.DefaultLexicalDirector;
import org.harvey.vie.theory.lexical.LexicalDirector;
import org.harvey.vie.theory.lexical.LexicalPattern;
import org.harvey.vie.theory.lexical.analysis.LexicalAnalyzer;
import org.harvey.vie.theory.lexical.analysis.token.AbstractTokenType;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.resource.AsciiStringResource;
import org.harvey.vie.theory.resource.Resource;

import java.text.ParseException;
import java.util.List;

/**
 * 启动类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:19
 */
@Slf4j
public class Main {
    @AllArgsConstructor
    private static class TempType extends AbstractTokenType {
        private final String s;

        @Override
        public @NonNull String hint() {
            return s;
        }
    }

    public static void main(String[] args) {
        LexicalDirector director = new DefaultLexicalDirector();
        ErrorContext errorContext = new DefaultErrorContext();
        try {
            LexicalAnalyzer analyzer = director.direct(List.of(
                    new LexicalPattern(" ", new TempType("A")),
                    new LexicalPattern("(1|2|3)(1|2|3)*(7|8|9)*", new TempType("B")),
                    new LexicalPattern("(7|8|9)(4|5|6)*(a|b|c)*", new TempType("C")),
                    new LexicalPattern("(4|5|6)(7|8|9)*(1|2|3)*", new TempType("D")),
                    new LexicalPattern("(a|b|c)(a|b|c)*(4|5|6)*", new TempType("E"))
            ));
            Resource resource = new AsciiStringResource("aaa112441 123333aaa");
            SourceTokenIterator iterator = analyzer.iterator(errorContext, resource);
            while (iterator.hasNext()) {
                SourceToken next = iterator.next();
                log.info(next.hintString());
            }
            log.info("yes");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (CompileException e) {
            log.warn("compile error", e);
        } finally {
            log.info("{}", errorContext);
        }
    }
}

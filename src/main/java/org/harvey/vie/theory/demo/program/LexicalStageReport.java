package org.harvey.vie.theory.demo.program;

import org.harvey.vie.theory.error.CompileErrorMessage;
import org.harvey.vie.theory.error.DefaultErrorContext;
import org.harvey.vie.theory.io.resource.AsciiStringResource;
import org.harvey.vie.theory.io.resource.Resource;
import org.harvey.vie.theory.lexical.analysis.LexicalAnalyzer;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Captures stage-1 lexical output in a report-friendly form.
 */
public class LexicalStageReport {
    private final List<LexicalTokenView> filteredTokens;
    private final List<LexicalTokenView> rawTokens;
    private final List<LexicalTokenView> noiseTokens;
    private final List<IdentifierEntry> identifiers;
    private final List<CompileErrorMessage> errors;
    private final Throwable failure;

    public LexicalStageReport(
            List<LexicalTokenView> filteredTokens,
            List<LexicalTokenView> rawTokens,
            List<LexicalTokenView> noiseTokens,
            List<IdentifierEntry> identifiers,
            List<CompileErrorMessage> errors,
            Throwable failure) {
        this.filteredTokens = List.copyOf(filteredTokens);
        this.rawTokens = List.copyOf(rawTokens);
        this.noiseTokens = List.copyOf(noiseTokens);
        this.identifiers = List.copyOf(identifiers);
        this.errors = List.copyOf(errors);
        this.failure = failure;
    }

    public List<LexicalTokenView> getFilteredTokens() {
        return filteredTokens;
    }

    public List<LexicalTokenView> getRawTokens() {
        return rawTokens;
    }

    public List<LexicalTokenView> getNoiseTokens() {
        return noiseTokens;
    }

    public List<IdentifierEntry> getIdentifiers() {
        return identifiers;
    }

    public List<CompileErrorMessage> getErrors() {
        return errors;
    }

    public Throwable getFailure() {
        return failure;
    }

    public boolean isAccepted() {
        return failure == null && errors.isEmpty();
    }

    public static LexicalStageReport analyze(String source) {
        DefaultErrorContext errorContext = new DefaultErrorContext();
        List<LexicalTokenView> rawTokens = new ArrayList<>();
        List<LexicalTokenView> filteredTokens = new ArrayList<>();
        List<LexicalTokenView> noiseTokens = new ArrayList<>();
        Map<String, IdentifierEntry> identifiers = new LinkedHashMap<>();
        Throwable failure = null;
        LexicalAnalyzer analyzer = ProgramLexicalDemo.lexicalAnalyzer();
        Resource resource = new AsciiStringResource(source);
        try (SourceTokenIterator iterator = analyzer.iterator(errorContext, resource)) {
            while (iterator.hasNext()) {
                SourceToken token = iterator.next();
                if (token == SourceTokenIterator.NO_MORE_TOKEN) {
                    continue;
                }
                LexicalTokenView view = LexicalTokenView.from(token);
                rawTokens.add(view);
                ProgramTokenType type = (ProgramTokenType) token.getType();
                if (!ProgramSyntaxDemo.SHOULD_BE_FILTERED.contains(type)) {
                    filteredTokens.add(view);
                } else {
                    noiseTokens.add(view);
                }
                if (type == ProgramTokenType.IDENTIFIER || type == ProgramTokenType.TYPE_IDENTIFIER) {
                    identifiers.putIfAbsent(view.getLexeme(), new IdentifierEntry(
                            identifiers.size(),
                            view.getLexeme(),
                            type.name(),
                            view.getOffset()
                    ));
                }
            }
        } catch (Throwable throwable) {
            failure = throwable;
        }
        return new LexicalStageReport(
                filteredTokens,
                rawTokens,
                noiseTokens,
                new ArrayList<>(identifiers.values()),
                List.copyOf(errorContext.getErrors()),
                failure
        );
    }

    public static final class LexicalTokenView {
        private final String type;
        private final String lexeme;
        private final int offset;

        public LexicalTokenView(String type, String lexeme, int offset) {
            this.type = type;
            this.lexeme = lexeme;
            this.offset = offset;
        }

        public static LexicalTokenView from(SourceToken token) {
            return new LexicalTokenView(
                    token.getType() == null ? "<null>" : token.getType().hint(),
                    SourceTokenStringMapping.utf8(token),
                    token.getOffset()
            );
        }

        public String getType() {
            return type;
        }

        public String getLexeme() {
            return lexeme;
        }

        public int getOffset() {
            return offset;
        }
    }

    public static final class IdentifierEntry {
        private final int index;
        private final String name;
        private final String tokenType;
        private final int firstOffset;

        public IdentifierEntry(int index, String name, String tokenType, int firstOffset) {
            this.index = index;
            this.name = name;
            this.tokenType = tokenType;
            this.firstOffset = firstOffset;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public String getTokenType() {
            return tokenType;
        }

        public int getFirstOffset() {
            return firstOffset;
        }
    }
}

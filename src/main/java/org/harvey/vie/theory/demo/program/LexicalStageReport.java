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
    private final List<IdentifierEntry> identifiers;
    private final List<CompileErrorMessage> errors;
    private final Throwable failure;

    /**
     * 函数功能：创建词法阶段报告对象。
     * 输入：
     * - filteredTokens：过滤后的词法单元视图列表。
     * - identifiers：识别出的标识符条目列表。
     * - errors：词法分析错误信息列表。
     * - failure：词法分析过程中的异常对象。
     * 输出：无。
     */
    public LexicalStageReport(
            List<LexicalTokenView> filteredTokens,
            List<IdentifierEntry> identifiers,
            List<CompileErrorMessage> errors,
            Throwable failure) {
        this.filteredTokens = List.copyOf(filteredTokens);
        this.identifiers = List.copyOf(identifiers);
        this.errors = List.copyOf(errors);
        this.failure = failure;
    }

    /**
     * 函数功能：分析源代码并生成词法阶段报告。
     * 输入：
     * - source：待分析的源代码文本。
     * 输出：词法阶段分析报告 LexicalStageReport。
     */
    public static LexicalStageReport analyze(String source) {
        DefaultErrorContext errorContext = new DefaultErrorContext();
        List<LexicalTokenView> filteredTokens = new ArrayList<>();
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
                ProgramTokenType type = (ProgramTokenType) token.getType();
                if (!ProgramSyntaxDemo.SHOULD_BE_FILTERED.contains(type)) {
                    filteredTokens.add(view);
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
                new ArrayList<>(identifiers.values()),
                List.copyOf(errorContext.getErrors()),
                failure
        );
    }

    /**
     * 函数功能：获取过滤后的词法单元视图列表。
     * 输入：
     * - 无。
     * 输出：过滤后的 LexicalTokenView 列表。
     */
    public List<LexicalTokenView> getFilteredTokens() {
        return filteredTokens;
    }

    /**
     * 函数功能：获取词法分析识别出的标识符条目列表。
     * 输入：
     * - 无。
     * 输出：IdentifierEntry 列表。
     */
    public List<IdentifierEntry> getIdentifiers() {
        return identifiers;
    }

    /**
     * 函数功能：获取词法分析错误信息列表。
     * 输入：
     * - 无。
     * 输出：CompileErrorMessage 列表。
     */
    public List<CompileErrorMessage> getErrors() {
        return errors;
    }

    /**
     * 函数功能：获取词法分析过程中的异常对象。
     * 输入：
     * - 无。
     * 输出：Throwable 异常对象；无异常时为 null。
     */
    public Throwable getFailure() {
        return failure;
    }

    /**
     * 函数功能：判断词法分析是否被接受。
     * 输入：
     * - 无。
     * 输出：是否无异常且无错误的布尔值。
     */
    public boolean isAccepted() {
        return failure == null && errors.isEmpty();
    }

    public static final class LexicalTokenView {
        private final String type;
        private final String lexeme;
        private final int offset;

        /**
         * 函数功能：创建词法单元视图对象。
         * 输入：
         * - type：词法单元类型名称。
         * - lexeme：词法单元原始文本。
         * - offset：词法单元在源文本中的偏移量。
         * 输出：无。
         */
        public LexicalTokenView(String type, String lexeme, int offset) {
            this.type = type;
            this.lexeme = lexeme;
            this.offset = offset;
        }

        /**
         * 函数功能：从源词法单元创建报告用词法单元视图。
         * 输入：
         * - token：源词法单元对象。
         * 输出：转换后的 LexicalTokenView。
         */
        public static LexicalTokenView from(SourceToken token) {
            return new LexicalTokenView(
                    token.getType() == null ? "<null>" : token.getType().hint(),
                    SourceTokenStringMapping.utf8(token),
                    token.getOffset()
            );
        }

        /**
         * 函数功能：获取词法单元类型名称。
         * 输入：
         * - 无。
         * 输出：词法单元类型字符串。
         */
        public String getType() {
            return type;
        }

        /**
         * 函数功能：获取词法单元原始文本。
         * 输入：
         * - 无。
         * 输出：词法单元文本字符串。
         */
        public String getLexeme() {
            return lexeme;
        }

        /**
         * 函数功能：获取词法单元在源文本中的偏移量。
         * 输入：
         * - 无。
         * 输出：偏移量整数。
         */
        public int getOffset() {
            return offset;
        }
    }

    public static final class IdentifierEntry {
        private final int index;
        private final String name;
        private final String tokenType;
        private final int firstOffset;

        /**
         * 函数功能：创建标识符报告条目。
         * 输入：
         * - index：标识符序号。
         * - name：标识符名称。
         * - tokenType：标识符对应的词法单元类型名称。
         * - firstOffset：标识符首次出现的偏移量。
         * 输出：无。
         */
        public IdentifierEntry(int index, String name, String tokenType, int firstOffset) {
            this.index = index;
            this.name = name;
            this.tokenType = tokenType;
            this.firstOffset = firstOffset;
        }

        /**
         * 函数功能：获取标识符序号。
         * 输入：
         * - 无。
         * 输出：标识符序号整数。
         */
        public int getIndex() {
            return index;
        }

        /**
         * 函数功能：获取标识符名称。
         * 输入：
         * - 无。
         * 输出：标识符名称字符串。
         */
        public String getName() {
            return name;
        }

        /**
         * 函数功能：获取标识符对应的词法单元类型名称。
         * 输入：
         * - 无。
         * 输出：词法单元类型字符串。
         */
        public String getTokenType() {
            return tokenType;
        }

        /**
         * 函数功能：获取标识符首次出现的偏移量。
         * 输入：
         * - 无。
         * 输出：首次出现偏移量整数。
         */
        public int getFirstOffset() {
            return firstOffset;
        }
    }
}

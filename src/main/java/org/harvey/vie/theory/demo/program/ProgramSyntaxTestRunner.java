package org.harvey.vie.theory.demo.program;

import lombok.Getter;
import org.harvey.vie.theory.demo.SemanticDemo;
import org.harvey.vie.theory.demo.SyntaxDemo;
import org.harvey.vie.theory.error.CompileErrorMessage;
import org.harvey.vie.theory.error.DefaultErrorContext;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.io.resource.AsciiStringResource;
import org.harvey.vie.theory.io.resource.Resource;
import org.harvey.vie.theory.lexical.analysis.LexicalAnalyzer;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;
import org.harvey.vie.theory.semantic.context.SemanticAnalysisResult;
import org.harvey.vie.theory.semantic.context.SemanticResult;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.value.ConstantValue;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaser;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaserImpl;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable;
import org.harvey.vie.theory.util.RuntimeProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.harvey.vie.theory.demo.program.ProgramSyntaxDemo.PROGRAM_SEMANTIC_TAG_COMPARATOR;
import static org.harvey.vie.theory.demo.program.ProgramSyntaxDemo.PROGRAM_SEMANTIC_TAG_LOADER;

/**
 * @author Temper
 */
public final class ProgramSyntaxTestRunner {
    private static final Path TEST_CASE_DIR = Path.of("src/main/resources/program-tests");
    private static final Path REPORT_ROOT_DIR = Path.of("run-reports/program-syntax");
    private static final DateTimeFormatter RUN_ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String SERIAL_SYNTAX_TABLE = "syntax_table.data";
    private ProgramSyntaxTestRunner() {
    }

    public static SemanticRunReport run() {
        try {
            Files.createDirectories(REPORT_ROOT_DIR);
            List<Path> cases = listTestCases();
            if (cases.isEmpty()) {
                throw new IllegalStateException("no test cases found in " + TEST_CASE_DIR);
            }
            String runId = LocalDateTime.now().format(RUN_ID_FORMATTER);
            Path runReportDir = REPORT_ROOT_DIR.resolve(runId);
            Files.createDirectories(runReportDir);
            List<TestCaseResult> results = cases.stream().map(testCase -> {
                try {
                    return runOneTestCase(testCase, runReportDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
            Path summaryReport = runReportDir.resolve("summary.md");
            writeSummary(summaryReport, runId, results);
            return new SemanticRunReport(runId, summaryReport, results);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Path> listTestCases() throws IOException {
        String onlyCase = RuntimeProperties.programTestCase();
        try (Stream<Path> stream = Files.list(TEST_CASE_DIR)) {
            return stream.filter(path -> path.getFileName().toString().endsWith(".txt"))
                    .filter(path -> onlyCase == null || path.getFileName().toString().equals(onlyCase + ".txt"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .collect(Collectors.toList());
        }
    }

    private static TestCaseResult runOneTestCase(Path testCase, Path runReportDir) throws IOException {
        String caseName = testCase.getFileName().toString().replaceFirst("\\.txt$", "");
        boolean expectedFailure = caseName.contains("invalid");
        String text = Files.readString(testCase, StandardCharsets.UTF_8);
        DefaultErrorContext errorContext = new DefaultErrorContext();
        SemanticAnalysisResult semanticResult = null;
        Throwable failure = null;
        boolean executedSuccessfully = false;
        try {
            semanticResult = executeSemanticTest(text, errorContext);
            executedSuccessfully = true;
        } catch (Throwable throwable) {
            failure = throwable;
        }
        boolean hasErrors = !errorContext.isEmpty();
        boolean observedRejected = failure != null || hasErrors;
        boolean observedAccepted = executedSuccessfully && !hasErrors;
        boolean matchedExpectation = expectedFailure ? observedRejected : observedAccepted;
        Path report = runReportDir.resolve(caseName + ".md");
        writeReport(
                report,
                caseName,
                text,
                semanticResult,
                errorContext,
                matchedExpectation,
                observedAccepted,
                observedRejected,
                failure,
                expectedFailure
        );
        int commandCount = semanticResult == null ? 0 : semanticResult.getCommands().size();
        int symbolCount = semanticResult == null ? 0 : semanticResult.getIdentifierRecords().length;
        return new TestCaseResult(
                caseName,
                report,
                matchedExpectation,
                expectedFailure,
                observedAccepted,
                observedRejected,
                errorContext.size(),
                commandCount,
                symbolCount,
                semanticResult,
                List.copyOf(errorContext.getErrors()),
                failure
        );
    }

    public static SemanticAnalysisResult executeSemanticTest(String text, DefaultErrorContext errorContext) {
        LexicalAnalyzer analyzer = ProgramLexicalDemo.lexicalAnalyzer();
        Resource resource = new AsciiStringResource(text);
        ShiftReduceParsingTable shiftReduceParsingTable = SyntaxDemo.buildShiftReduceParsingTable(
                "compilation_unit",
                ProgramSyntaxDemo.buildGrammar0(),
                SERIAL_SYNTAX_TABLE,
                PROGRAM_SEMANTIC_TAG_LOADER,
                PROGRAM_SEMANTIC_TAG_COMPARATOR
        );
        ShiftReducePhaser phaser = new ShiftReducePhaserImpl(
                shiftReduceParsingTable,
                t -> !ProgramSyntaxDemo.SHOULD_BE_FILTERED.contains(t.getType()),
                SemanticDemo.buildShiftReduceTestRegister(),
                SyntaxDemo.STRING_COMMAND_FACTORY,
                ProgramSyntaxDemo.TYPE_RESOLVER,
                ProgramSyntaxDemo.CONSTANT_RESOLVER,
                ProgramSyntaxDemo.FUNCTION_MANAGER
        );
        try (SourceTokenIterator iterator = analyzer.iterator(errorContext, resource)) {
            SemanticResult result = phaser.phase(iterator, errorContext);
            if (result == null) {
                return null;
            }
            if (!(result instanceof SemanticAnalysisResult)) {
                throw new IllegalStateException("unexpected semantic result type: " + result.getClass());
            }
            return (SemanticAnalysisResult) result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeSummary(Path summaryReport, String runId, List<TestCaseResult> results) throws IOException {
        long passCount = results.stream().filter(TestCaseResult::isExpectationMatched).count();
        long failCount = results.size() - passCount;
        StringBuilder summary = new StringBuilder();
        summary.append("# Program Semantic Test Summary\n\n");
        summary.append("- Run Id: ").append(runId).append("\n");
        summary.append("- Generated At: ").append(LocalDateTime.now()).append("\n");
        summary.append("- Cases: ").append(results.size()).append("\n");
        summary.append("- Passed: ").append(passCount).append("\n");
        summary.append("- Failed: ").append(failCount).append("\n\n");
        summary.append("| Case | Expected | Observed | Matched | Errors | Commands | Symbols | Report |\n");
        summary.append("| --- | --- | --- | --- | --- | --- | --- | --- |\n");
        for (TestCaseResult result : results) {
            summary.append("| ")
                    .append(result.caseName)
                    .append(" | ")
                    .append(result.expectedFailure ? "REJECT" : "ACCEPT")
                    .append(" | ")
                    .append(result.isObservedRejected() ? "REJECT" : "ACCEPT")
                    .append(" | ")
                    .append(result.expectationMatched ? "YES" : "NO")
                    .append(" | ")
                    .append(result.errorCount)
                    .append(" | ")
                    .append(result.commandCount)
                    .append(" | ")
                    .append(result.symbolCount)
                    .append(" | ")
                    .append(result.report.toAbsolutePath())
                    .append(" |\n");
        }
        Files.writeString(summaryReport, summary.toString(), StandardCharsets.UTF_8);
    }

    private static void writeReport(
            Path report,
            String caseName,
            String source,
            SemanticAnalysisResult semanticResult,
            ErrorContext errorContext,
            boolean matchedExpectation,
            boolean observedAccepted,
            boolean observedRejected,
            Throwable failure,
            boolean expectedFailure) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("# Program Semantic Test: ").append(caseName).append("\n\n");
        builder.append("- Expected: ").append(expectedFailure ? "REJECT" : "ACCEPT").append("\n");
        builder.append("- Observed: ").append(observedRejected ? "REJECT" : observedAccepted ? "ACCEPT" : "UNKNOWN").append("\n");
        builder.append("- Matched Expectation: ").append(matchedExpectation ? "YES" : "NO").append("\n");
        builder.append("- Errors: ").append(errorContext.size()).append("\n");
        builder.append("- Commands: ").append(semanticResult == null ? 0 : semanticResult.getCommands().size()).append("\n");
        builder.append("- Symbols: ").append(semanticResult == null ? 0 : semanticResult.getIdentifierRecords().length).append("\n");
        builder.append("- Generated At: ").append(LocalDateTime.now()).append("\n\n");
        builder.append("## Source\n\n```text\n").append(source).append("\n```\n\n");
        builder.append("## Semantic Commands\n\n");
        if (semanticResult == null || semanticResult.getCommands().isEmpty()) {
            builder.append("_None_\n");
        } else {
            builder.append("```text\n");
            int index = 0;
            for (String command : semanticResult.getCommands()) {
                builder.append(String.format("[%03d] %s%n", index++, command));
            }
            builder.append("```\n");
        }
        builder.append("\n## Symbol Table\n\n");
        if (semanticResult == null || semanticResult.getIdentifierRecords().length == 0) {
            builder.append("_None_\n");
        } else {
            builder.append("```text\n");
            Arrays.stream(semanticResult.getIdentifierRecords())
                    .forEach(record -> builder.append(formatRecord(record)).append('\n'));
            builder.append("```\n");
        }
        builder.append("\n## Errors\n\n");
        if (errorContext.isEmpty()) {
            builder.append("_None_\n");
        } else {
            builder.append("```text\n");
            for (CompileErrorMessage error : errorContext) {
                builder.append(error).append('\n');
            }
            builder.append("```\n");
        }
        if (failure != null) {
            builder.append("\n## Failure\n\n```text\n").append(failure).append("\n```\n");
        }
        Files.writeString(report, builder.toString(), StandardCharsets.UTF_8);
    }

    private static String formatRecord(IdentifierRecord record) {
        ConstantValue constantValue = record.getConstantValue();
        return String.format(
                "record=%d offset=%d type=%s name=%s initialized=%s constant=%s",
                record.getNo(),
                record.getOffset(),
                formatType(record.getType()),
                SourceTokenStringMapping.utf8(record.getLexeme()),
                record.isInitialized(),
                constantValue == null ? "<none>" : constantValue.toString()
        );
    }

    private static String formatType(HeadNode typeNode) {
        StringJoiner joiner = new StringJoiner(" ");
        appendTypeLexemes(typeNode, joiner);
        String value = joiner.toString().trim();
        return value.isEmpty() ? typeNode.toString() : value;
    }

    private static void appendTypeLexemes(ShiftReduceSyntaxTreeNode node, StringJoiner joiner) {
        if (node.isToken()) {
            SourceToken token = node.toToken().getSource();
            joiner.add(SourceTokenStringMapping.utf8(token));
            return;
        }
        for (ShiftReduceSyntaxTreeNode child : node.toHead()) {
            appendTypeLexemes(child, joiner);
        }
    }

    @Getter
    public static final class SemanticRunReport {
        private final String runId;
        private final Path summaryReport;
        private final List<TestCaseResult> results;

        public SemanticRunReport(String runId, Path summaryReport, List<TestCaseResult> results) {
            this.runId = runId;
            this.summaryReport = summaryReport;
            this.results = List.copyOf(results);
        }

    }

    @Getter
    public static final class TestCaseResult {
        private final String caseName;
        private final Path report;
        private final boolean expectationMatched;
        private final boolean expectedFailure;
        private final boolean observedAccepted;
        private final boolean observedRejected;
        private final int errorCount;
        private final int commandCount;
        private final int symbolCount;
        private final SemanticAnalysisResult semanticResult;
        private final List<CompileErrorMessage> errors;
        private final Throwable failure;

        private TestCaseResult(
                String caseName,
                Path report,
                boolean expectationMatched,
                boolean expectedFailure,
                boolean observedAccepted,
                boolean observedRejected,
                int errorCount,
                int commandCount,
                int symbolCount,
                SemanticAnalysisResult semanticResult,
                List<CompileErrorMessage> errors,
                Throwable failure) {
            this.caseName = caseName;
            this.report = report;
            this.expectationMatched = expectationMatched;
            this.expectedFailure = expectedFailure;
            this.observedAccepted = observedAccepted;
            this.observedRejected = observedRejected;
            this.errorCount = errorCount;
            this.commandCount = commandCount;
            this.symbolCount = symbolCount;
            this.semanticResult = semanticResult;
            this.errors = errors;
            this.failure = failure;
        }

        public boolean isSuccess() {
            return expectationMatched;
        }

    }
}


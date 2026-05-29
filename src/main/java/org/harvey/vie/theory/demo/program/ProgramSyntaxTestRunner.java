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
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.semantic.command.FunctionCommandSegment;
import org.harvey.vie.theory.semantic.context.SemanticAnalysisResult;
import org.harvey.vie.theory.semantic.context.SemanticResult;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallbackRegisterImpl;
import org.harvey.vie.theory.semantic.display.SemanticDisplaySupport;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.structure.StructField;
import org.harvey.vie.theory.semantic.structure.StructRecord;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.harvey.vie.theory.demo.program.ProgramSyntaxDemo.PROGRAM_SEMANTIC_TAG_COMPARATOR;
import static org.harvey.vie.theory.demo.program.ProgramSyntaxDemo.PROGRAM_SEMANTIC_TAG_LOADER;

/**
 * @author Temper
 */
public final class ProgramSyntaxTestRunner {
    private static final Path TEST_CASE_DIR = Path.of("src/main/resources/program-tests");
    private static final Path PHASE_REPORT_CASE_DIR = TEST_CASE_DIR.resolve("phase-report");
    private static final Path REPORT_ROOT_DIR = Path.of("run-reports/program-syntax");
    private static final DateTimeFormatter RUN_ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String SERIAL_SYNTAX_TABLE = "syntax_table.data";
    private static final String COMMON_LEXICAL_REPORT = "stage1-common.md";
    private static final String COMMON_SYNTAX_REPORT = "stage2-common.md";
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
            boolean containsPhaseReportCases = containsPhaseReportCases(cases);
            if (containsPhaseReportCases) {
                writeCommonLexicalReport(runReportDir.resolve(COMMON_LEXICAL_REPORT));
                writeCommonSyntaxReport(runReportDir.resolve(COMMON_SYNTAX_REPORT));
            }
            List<TestCaseResult> results = cases.stream().map(testCase -> {
                try {
                    return runOneTestCase(testCase, runReportDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
            Path summaryReport = runReportDir.resolve("summary.md");
            writeSummary(summaryReport, runId, results, containsPhaseReportCases);
            return new SemanticRunReport(runId, summaryReport, results);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Path> listTestCases() throws IOException {
        String onlyCase = RuntimeProperties.programTestCase();
        try (Stream<Path> stream = Files.walk(TEST_CASE_DIR)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> isTestCaseFile(path.getFileName().toString()))
                    .filter(path -> onlyCase == null || path.getFileName().toString().equals(onlyCase + ".txt"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .collect(Collectors.toList());
        }
    }

    private static boolean isTestCaseFile(String fileName) {
        return fileName.endsWith(".txt");
    }

    private static boolean isPhaseReportCase(Path testCase) {
        return testCase.normalize().startsWith(PHASE_REPORT_CASE_DIR.normalize());
    }

    private static boolean containsPhaseReportCases(List<Path> cases) {
        return cases.stream().anyMatch(ProgramSyntaxTestRunner::isPhaseReportCase);
    }

    private static TestCaseResult runOneTestCase(Path testCase, Path runReportDir) throws IOException {
        String caseName = testCase.getFileName().toString().replaceFirst("\\.txt$", "");
        boolean expectedFailure = caseName.contains("invalid");
        String text = Files.readString(testCase, StandardCharsets.UTF_8);
        boolean phaseReportCase = isPhaseReportCase(testCase);
        LexicalStageReport lexicalReport = phaseReportCase ? LexicalStageReport.analyze(text) : null;
        DefaultErrorContext errorContext = new DefaultErrorContext();
        SemanticAnalysisResult semanticResult = null;
        SyntaxTraceReport syntaxTrace = phaseReportCase ? new SyntaxTraceReport() : null;
        Throwable failure = null;
        boolean executedSuccessfully = false;
        try {
            semanticResult = executeSemanticTest(text, errorContext, syntaxTrace);
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
                lexicalReport,
                syntaxTrace,
                semanticResult,
                errorContext,
                matchedExpectation,
                observedAccepted,
                observedRejected,
                failure,
                expectedFailure,
                phaseReportCase
        );
        int commandCount = semanticResult == null ? 0 : semanticResult.commandCount();
        int symbolCount = semanticResult == null ? 0 : semanticResult.getIdentifierRecords().length;
        return new TestCaseResult(
                caseName,
                report,
                matchedExpectation,
                expectedFailure,
                observedAccepted,
                observedRejected,
                errorContext.size(),
                phaseReportCase,
                lexicalReport,
                syntaxTrace == null ? List.of() : syntaxTrace.snapshot(),
                commandCount,
                symbolCount,
                semanticResult,
                List.copyOf(errorContext.getErrors()),
                failure
        );
    }

    public static SemanticAnalysisResult executeSemanticTest(
            String text,
            DefaultErrorContext errorContext,
            SyntaxTraceReport syntaxTrace) {
        LexicalAnalyzer analyzer = ProgramLexicalDemo.lexicalAnalyzer();
        Resource resource = new AsciiStringResource(text);
        ShiftReduceParsingTable shiftReduceParsingTable = SyntaxDemo.buildShiftReduceParsingTable(
                "compilation_unit",
                ProgramSyntaxDemo.buildGrammar0(),
                SERIAL_SYNTAX_TABLE,
                PROGRAM_SEMANTIC_TAG_LOADER,
                PROGRAM_SEMANTIC_TAG_COMPARATOR
        );
        ShiftReduceCallbackRegisterImpl register = new ShiftReduceCallbackRegisterImpl();
        if (syntaxTrace != null) {
            register.add(new SyntaxTraceReportProxy(syntaxTrace));
        }
        for (var callback : SemanticDemo.buildShiftReduceTestRegister()) {
            register.add(callback);
        }
        ShiftReducePhaser phaser = new ShiftReducePhaserImpl(
                shiftReduceParsingTable,
                t -> !ProgramSyntaxDemo.SHOULD_BE_FILTERED.contains(t.getType()),
                register,
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

    private static void writeSummary(
            Path summaryReport,
            String runId,
            List<TestCaseResult> results,
            boolean containsPhaseReportCases) throws IOException {
        long passCount = results.stream().filter(TestCaseResult::isExpectationMatched).count();
        long failCount = results.size() - passCount;
        StringBuilder summary = new StringBuilder();
        summary.append("# Program Semantic Test Summary\n\n");
        summary.append("- Run Id: ").append(runId).append("\n");
        summary.append("- Generated At: ").append(LocalDateTime.now()).append("\n");
        summary.append("- Cases: ").append(results.size()).append("\n");
        summary.append("- Passed: ").append(passCount).append("\n");
        summary.append("- Failed: ").append(failCount).append("\n\n");
        if (containsPhaseReportCases) {
            summary.append("- Stage 1 Common Report: ")
                    .append(REPORT_ROOT_DIR.resolve(runId).resolve(COMMON_LEXICAL_REPORT).toAbsolutePath())
                    .append("\n");
            summary.append("- Stage 2 Common Report: ")
                    .append(REPORT_ROOT_DIR.resolve(runId).resolve(COMMON_SYNTAX_REPORT).toAbsolutePath())
                    .append("\n\n");
        }
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
            LexicalStageReport lexicalReport,
            SyntaxTraceReport syntaxTrace,
            SemanticAnalysisResult semanticResult,
            ErrorContext errorContext,
            boolean matchedExpectation,
            boolean observedAccepted,
            boolean observedRejected,
            Throwable failure,
            boolean expectedFailure,
            boolean phaseReportCase) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("# Program Semantic Test: ").append(caseName).append("\n\n");
        builder.append("- Expected: ").append(expectedFailure ? "REJECT" : "ACCEPT").append("\n");
        builder.append("- Observed: ").append(observedRejected ? "REJECT" : observedAccepted ? "ACCEPT" : "UNKNOWN").append("\n");
        builder.append("- Matched Expectation: ").append(matchedExpectation ? "YES" : "NO").append("\n");
        builder.append("- Errors: ").append(errorContext.size()).append("\n");
        builder.append("- Commands: ").append(semanticResult == null ? 0 : semanticResult.commandCount()).append("\n");
        builder.append("- Symbols: ").append(semanticResult == null ? 0 : semanticResult.getIdentifierRecords().length).append("\n");
        builder.append("- Generated At: ").append(LocalDateTime.now()).append("\n\n");
        builder.append("## Source\n\n```text\n").append(source).append("\n```\n\n");
        writeRequestedExtraSections(builder, phaseReportCase, lexicalReport, syntaxTrace);
        writeSemanticSections(builder, semanticResult);
        builder.append("\n");
        writeErrorSection(builder, errorContext, "## Errors");
        if (failure != null) {
            builder.append("\n## Failure\n\n```text\n").append(failure).append("\n```\n");
        }
        Files.writeString(report, builder.toString(), StandardCharsets.UTF_8);
    }

    private static void writeSemanticSections(StringBuilder builder, SemanticAnalysisResult semanticResult) {
        builder.append("## Struct Table\n\n");
        writeStructTable(builder, semanticResult == null ? null : semanticResult.getStructTable());
        builder.append("\n");
        builder.append("## Global Segment\n\n");
        builder.append("- Kind: Program Entry\n\n");
        builder.append("### Commands\n\n");
        writeCommands(builder, semanticResult == null ? null : semanticResult.getCommands());
        builder.append("\n### Local Variables\n\n");
        writeIdentifierTable(
                builder,
                semanticResult == null ? null : semanticResult.getEntryLocalVariables(),
                semanticResult == null ? List.of() : semanticResult.getStructTable()
        );
        if (semanticResult != null && !semanticResult.getFunctionSegments().isEmpty()) {
            for (FunctionCommandSegment segment : semanticResult.getFunctionSegments()) {
                writeFunctionSection(builder, semanticResult, segment);
            }
        }
    }

    private static void writeCommonLexicalReport(Path report) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("# Experiment 1 Common Lexical Report\n\n");
        builder.append("- Generated At: ").append(LocalDateTime.now()).append("\n");
        builder.append("- Scope: shared lexical DFA/status table for all program test cases\n\n");
        builder.append("## Lexical DFA Status Table\n\n```text\n");
        builder.append(ProgramLexicalDemo.lexicalTable()).append('\n');
        builder.append("```\n");
        Files.writeString(report, builder.toString(), StandardCharsets.UTF_8);
    }

    private static void writeCommonSyntaxReport(Path report) throws IOException {
        ShiftReduceParsingTable table = SyntaxDemo.buildShiftReduceParsingTable(
                "compilation_unit",
                ProgramSyntaxDemo.buildGrammar0(),
                SERIAL_SYNTAX_TABLE,
                PROGRAM_SEMANTIC_TAG_LOADER,
                PROGRAM_SEMANTIC_TAG_COMPARATOR
        );
        StringBuilder builder = new StringBuilder();
        builder.append("# Experiment 2 Common Syntax Report\n\n");
        builder.append("- Generated At: ").append(LocalDateTime.now()).append("\n");
        builder.append("- Scope: shared grammar/table output for all program test cases\n\n");
        builder.append("## Syntax Analysis Table\n\n```text\n");
        builder.append(table).append('\n');
        builder.append("```\n");
        Files.writeString(report, builder.toString(), StandardCharsets.UTF_8);
    }

    private static void writeLexicalSection(StringBuilder builder, LexicalStageReport lexicalReport) {
        builder.append("- Observed: ").append(lexicalReport.isAccepted() ? "ACCEPT" : "REJECT").append("\n");
        builder.append("- Filtered Tokens: ").append(lexicalReport.getFilteredTokens().size()).append("\n");
        builder.append("- Identifiers: ").append(lexicalReport.getIdentifiers().size()).append("\n");
        builder.append("- Errors: ").append(lexicalReport.getErrors().size()).append("\n\n");
        builder.append("### Token Pair Sequence\n\n");
        if (lexicalReport.getFilteredTokens().isEmpty()) {
            builder.append("_None_\n");
        } else {
            builder.append("```text\n");
            for (LexicalStageReport.LexicalTokenView token : lexicalReport.getFilteredTokens()) {
                builder.append("(")
                        .append(token.getLexeme())
                        .append(", ")
                        .append(token.getType())
                        .append(") @")
                        .append(token.getOffset())
                        .append('\n');
            }
            builder.append("```\n");
        }
        builder.append("\n### Symbol Table\n\n");
        if (lexicalReport.getIdentifiers().isEmpty()) {
            builder.append("_None_\n");
        } else {
            builder.append("```text\n");
            for (LexicalStageReport.IdentifierEntry identifier : lexicalReport.getIdentifiers()) {
                builder.append("index=")
                        .append(identifier.getIndex())
                        .append(" name=")
                        .append(identifier.getName())
                        .append(" tokenType=")
                        .append(identifier.getTokenType())
                        .append(" firstOffset=")
                        .append(identifier.getFirstOffset())
                        .append('\n');
            }
            builder.append("```\n");
        }
        if (!lexicalReport.getErrors().isEmpty()) {
            builder.append("\n### Lexical Errors\n\n```text\n");
            for (CompileErrorMessage error : lexicalReport.getErrors()) {
                builder.append(error).append('\n');
            }
            builder.append("```\n");
        }
        if (lexicalReport.getFailure() != null) {
            builder.append("\n### Lexical Failure\n\n```text\n")
                    .append(lexicalReport.getFailure())
                    .append("\n```\n");
        }
    }

    private static void writeSyntaxTrace(StringBuilder builder, SyntaxTraceReport syntaxTrace) {
        List<SyntaxTraceReport.TraceEntry> entries = syntaxTrace.snapshot();
        builder.append("- Steps: ").append(entries.size()).append("\n");
        builder.append("- Error Type: ").append(syntaxTrace.getErrorType() == null ? "<none>" : syntaxTrace.getErrorType().name()).append("\n");
        builder.append("- Shared Analysis Table Report: ").append(COMMON_SYNTAX_REPORT).append("\n\n");
        builder.append("### Analysis Stack And Process Trace\n\n");
        if (entries.isEmpty()) {
            builder.append("_None_\n");
            return;
        }
        builder.append("```text\n");
        int index = 0;
        for (SyntaxTraceReport.TraceEntry entry : entries) {
            builder.append(String.format(
                    "[%03d] phase=%s stack=%s action=%s tokenType=%s lexeme=%s offset=%d%n",
                    index++,
                    entry.getPhase(),
                    entry.getStackBefore(),
                    entry.getAction(),
                    entry.getTokenType(),
                    entry.getLexeme(),
                    entry.getOffset()
            ));
        }
        builder.append("```\n");
    }

    private static void writeCommands(StringBuilder builder, List<String> commands) {
        if (commands == null || commands.isEmpty()) {
            builder.append("_None_\n");
            return;
        }
        builder.append("```text\n");
        int index = 0;
        for (String command : commands) {
            builder.append(String.format("[%03d] %s%n", index++, command));
        }
        builder.append("```\n");
    }

    private static void writeStructTable(StringBuilder builder, List<StructRecord> records) {
        if (records == null || records.isEmpty()) {
            builder.append("_None_\n");
            return;
        }
        builder.append("```text\n");
        for (StructRecord record : records) {
            builder.append(SemanticDisplaySupport.formatStructRecord(record, records)).append('\n');
            for (StructField field : record.getFields()) {
                builder.append("  ").append(SemanticDisplaySupport.formatStructField(field, records)).append('\n');
            }
        }
        builder.append("```\n");
    }

    private static void writeIdentifierTable(StringBuilder builder, IdentifierRecord[] records, List<StructRecord> structTable) {
        if (records == null || records.length == 0) {
            builder.append("_None_\n");
            return;
        }
        builder.append("```text\n");
        Arrays.stream(records)
                .forEach(record -> builder.append(SemanticDisplaySupport.formatIdentifierRecord(record, structTable)).append('\n'));
        builder.append("```\n");
    }

    private static void writeFunctionSection(
            StringBuilder builder,
            SemanticAnalysisResult semanticResult,
            FunctionCommandSegment segment) {
        FunctionRecord function = segment.getFunction();
        builder.append("\n## Function Segment: ")
                .append(SemanticDisplaySupport.formatFunctionName(function))
                .append("\n\n");
        builder.append("- Function Index: ").append(function.getTableIndex()).append("\n");
        builder.append("- Signature: ")
                .append(SemanticDisplaySupport.formatFunctionSignature(function, semanticResult.getStructTable()))
                .append("\n\n");
        builder.append("### Commands\n\n");
        writeCommands(builder, new org.harvey.vie.theory.semantic.command.ThreeAddressCodePrinter().print(segment.getCommands()));
        builder.append("\n### Local Variables\n\n");
        writeIdentifierTable(
                builder,
                semanticResult.getFunctionLocalVariables(function),
                semanticResult.getStructTable()
        );
    }

    private static void writeErrorSection(StringBuilder builder, ErrorContext errorContext, String title) {
        builder.append("\n").append(title).append("\n\n");
        if (errorContext.isEmpty()) {
            builder.append("_None_\n");
            return;
        }
        builder.append("```text\n");
        for (CompileErrorMessage error : errorContext) {
            builder.append(error).append('\n');
        }
        builder.append("```\n");
    }

    private static void writeRequestedExtraSections(
            StringBuilder builder,
            boolean phaseReportCase,
            LexicalStageReport lexicalReport,
            SyntaxTraceReport syntaxTrace) {
        if (phaseReportCase) {
            builder.append("## Experiment 1 Required Output\n\n");
            writeLexicalSection(builder, lexicalReport);
            builder.append("\n");
            builder.append("## Experiment 2 Required Output\n\n");
            writeSyntaxTrace(builder, syntaxTrace);
            builder.append("\n");
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
        private final boolean phaseReportCase;
        private final LexicalStageReport lexicalReport;
        private final List<SyntaxTraceReport.TraceEntry> syntaxTrace;
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
                boolean phaseReportCase,
                LexicalStageReport lexicalReport,
                List<SyntaxTraceReport.TraceEntry> syntaxTrace,
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
            this.phaseReportCase = phaseReportCase;
            this.lexicalReport = lexicalReport;
            this.syntaxTrace = syntaxTrace;
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
    private static final class SyntaxTraceReportProxy implements org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback {
        private final SyntaxTraceReport delegate;

        private SyntaxTraceReportProxy(SyntaxTraceReport delegate) {
            this.delegate = delegate;
        }

        @Override
        public void beforeAccept(org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext context,
                                 org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction production) {
            delegate.beforeAccept(context, production);
        }

        @Override
        public void onAccept(org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext context,
                             org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction production) {
            delegate.onAccept(context, production);
        }

        @Override
        public void onReduce(org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext context,
                             org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction production) {
            delegate.onReduce(context, production);
        }

        @Override
        public void onShift(org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext context,
                            int nextStatus,
                            org.harvey.vie.theory.lexical.analysis.token.SourceToken token) {
            delegate.onShift(context, nextStatus, token);
        }

        @Override
        public void onError(org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext context,
                            org.harvey.vie.theory.semantic.callback.bu.ShiftReduceErrorType errorType) {
            delegate.onError(context, errorType);
        }
    }
}


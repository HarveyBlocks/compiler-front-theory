package org.harvey.vie.theory.demo.program;

import org.harvey.vie.theory.demo.SemanticDemo;
import org.harvey.vie.theory.demo.SyntaxDemo;
import org.harvey.vie.theory.error.DefaultErrorContext;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.semantic.context.SemanticResult;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaser;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaserImpl;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ProgramSyntaxTestRunner {
    private static final Path TEST_CASE_DIR = Path.of("src/main/resources/program-tests");
    private static final Path REPORT_DIR = Path.of("run-reports/program-syntax");
    private static final DateTimeFormatter RUN_ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private ProgramSyntaxTestRunner() {
    }

    public static void run() {
        try {
            Files.createDirectories(REPORT_DIR);
            List<Path> cases = listTestCases();
            if (cases.isEmpty()) {
                throw new IllegalStateException("no test cases found in " + TEST_CASE_DIR);
            }
            String runId = LocalDateTime.now().format(RUN_ID_FORMATTER);
            StringBuilder summary = new StringBuilder();
            summary.append("# Program Syntax Test Summary\n\n");
            summary.append("- Run Id: ").append(runId).append("\n");
            summary.append("- Generated At: ").append(LocalDateTime.now()).append("\n");
            summary.append("- Cases: ").append(cases.size()).append("\n\n");
            summary.append("| Case | Result | Report |\n");
            summary.append("| --- | --- | --- |\n");
            System.out.println("program syntax test cases: " + cases.size());
            for (Path testCase : cases) {
                TestCaseResult result = runOneTestCase(testCase, runId);
                summary.append("| ")
                        .append(result.caseName)
                        .append(" | ")
                        .append(result.success ? "PASS" : "FAIL")
                        .append(" | ")
                        .append(result.report.toAbsolutePath())
                        .append(" |\n");
            }
            Path summaryReport = REPORT_DIR.resolve(runId + "-summary.md");
            Files.writeString(summaryReport, summary.toString(), StandardCharsets.UTF_8);
            System.out.println("reports: " + REPORT_DIR.toAbsolutePath());
            System.out.println("summary: " + summaryReport.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Path> listTestCases() throws IOException {
        try (Stream<Path> stream = Files.list(TEST_CASE_DIR)) {
            return stream.filter(path -> path.getFileName().toString().endsWith(".txt"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .collect(Collectors.toList());
        }
    }

    private static TestCaseResult runOneTestCase(Path testCase, String runId) throws IOException {
        String caseName = testCase.getFileName().toString().replaceFirst("\\.txt$", "");
        String text = Files.readString(testCase, StandardCharsets.UTF_8);
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        Throwable failure = null;
        boolean success = false;
        try (PrintStream capture = new PrintStream(outputBuffer, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            ProgramSyntaxDemo.demo(text, ProgramSyntaxTestRunner::phaseGrammar0);
            success = true;
        } catch (Throwable throwable) {
            failure = throwable;
        } finally {
            System.setOut(originalOut);
        }
        String output = outputBuffer.toString(StandardCharsets.UTF_8);
        Path report = REPORT_DIR.resolve(runId + "-" + caseName + ".md");
        writeReport(report, caseName, text, output, success, failure);
        System.out.println((success ? "[PASS] " : "[FAIL] ") + caseName + " -> " + report.toAbsolutePath());
        if (failure != null) {
            failure.printStackTrace(System.out);
        }
        return new TestCaseResult(caseName, report, success);
    }

    private static SemanticResult phaseGrammar0(SourceTokenIterator iter, ErrorContext errCtx) {
        ProductionSetContext context = ProgramSyntaxDemo.buildGrammar0();
        System.out.println(context);
        ShiftReduceParsingTable shiftReduceParsingTable = SyntaxDemo.buildShiftReduceParsingTable(
                "program",
                context,
                "syntax_table.data"
        );
        ShiftReducePhaser phaser = new ShiftReducePhaserImpl(
                shiftReduceParsingTable,
                t -> !ProgramSyntaxDemo.SHOULD_BE_FILTERED.contains(t.getType()),
                SemanticDemo.buildShiftReduceRegister()
        );
        return phaser.phase(iter, errCtx);
    }

    private static void writeReport(
            Path report,
            String caseName,
            String source,
            String output,
            boolean success,
            Throwable failure) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("# Program Syntax Test: ").append(caseName).append("\n\n");
        builder.append("- Result: ").append(success ? "PASS" : "FAIL").append("\n");
        builder.append("- Generated At: ").append(LocalDateTime.now()).append("\n\n");
        builder.append("## Source\n\n```text\n").append(source).append("\n```\n\n");
        builder.append("## Output\n\n```text\n").append(output).append("\n```\n");
        if (failure != null) {
            builder.append("\n## Failure\n\n```text\n").append(failure).append("\n```\n");
        }
        Files.writeString(report, builder.toString(), StandardCharsets.UTF_8);
    }

    private static class TestCaseResult {
        private final String caseName;
        private final Path report;
        private final boolean success;

        private TestCaseResult(String caseName, Path report, boolean success) {
            this.caseName = caseName;
            this.report = report;
            this.success = success;
        }
    }
}

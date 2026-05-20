package org.harvey.vie.theory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.harvey.vie.theory.demo.program.ProgramSyntaxTestRunner;
import org.harvey.vie.theory.demo.program.ProgramSyntaxTestRunner.SemanticRunReport;
import org.harvey.vie.theory.demo.program.ProgramSyntaxTestRunner.TestCaseResult;
import org.harvey.vie.theory.semantic.context.SemanticAnalysisResult;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class AppTest extends TestCase {
    public AppTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(AppTest.class);
        return suite;
    }

    public void testProgramSemanticCases() throws Exception {
        SemanticRunReport runReport = ProgramSyntaxTestRunner.run();
        List<TestCaseResult> failures = runReport.getResults().stream()
                .filter(result -> !result.isSuccess())
                .collect(Collectors.toList());
        assertTrue(buildFailureMessage(runReport, failures), failures.isEmpty());

        TestCaseResult siblingScope = findCase(runReport, "text5-sibling-scope");
        assertNotNull("missing sibling scope case", siblingScope);
        SemanticAnalysisResult siblingResult = siblingScope.getSemanticResult();
        assertNotNull("semantic result missing for sibling scope case", siblingResult);
        IdentifierRecord left = findRecordByName(siblingResult.getIdentifierRecords(), "left");
        IdentifierRecord right = findRecordByName(siblingResult.getIdentifierRecords(), "right");
        assertNotNull("missing symbol left", left);
        assertNotNull("missing symbol right", right);
        assertFalse("sibling scope declarations should have different declaration records", left.getNo() == right.getNo());
        assertEquals("sibling scope declarations should reuse local offset", left.getOffset(), right.getOffset());

        TestCaseResult invalidUnary = findCase(runReport, "text4-unary-invalid");
        assertNotNull("missing invalid unary case", invalidUnary);
        assertTrue("invalid unary case should be recognized as expected failure", invalidUnary.isExpectedFailure());
        assertTrue("invalid unary case should report semantic error or failure",
                invalidUnary.getErrorCount() > 0 || invalidUnary.getFailure() != null);

        assertTrue("summary report should exist: " + runReport.getSummaryReport(), Files.exists(runReport.getSummaryReport()));
    }

    private static String buildFailureMessage(SemanticRunReport runReport, List<TestCaseResult> failures) {
        if (failures.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("semantic test failures, summary=")
                .append(runReport.getSummaryReport().toAbsolutePath())
                .append('\n');
        for (TestCaseResult failure : failures) {
            builder.append("- case=")
                    .append(failure.getCaseName())
                    .append(", errors=")
                    .append(failure.getErrorCount())
                    .append(", report=")
                    .append(failure.getReport().toAbsolutePath());
            if (failure.getFailure() != null) {
                builder.append(", failure=").append(failure.getFailure());
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    private static TestCaseResult findCase(SemanticRunReport runReport, String caseName) {
        return runReport.getResults().stream()
                .filter(result -> caseName.equals(result.getCaseName()))
                .findFirst()
                .orElse(null);
    }

    private static IdentifierRecord findRecordByName(IdentifierRecord[] records, String name) {
        for (IdentifierRecord record : records) {
            if (name.equals(new String(record.getLexeme()))) {
                return record;
            }
        }
        return null;
    }
}

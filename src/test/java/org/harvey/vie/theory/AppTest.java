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

        TestCaseResult arrayCase = findCase(runReport, "text3");
        assertNotNull("missing array case", arrayCase);
        SemanticAnalysisResult arrayResult = arrayCase.getSemanticResult();
        assertNotNull("semantic result missing for array case", arrayResult);
        boolean hasUnknownReference = arrayResult.getCommands().stream()
                .anyMatch(command -> command.contains("load_st_unknown_reference"));
        assertFalse("array references should not degrade to unknown typed loads", hasUnknownReference);
        assertEquals("do-while back edge should jump to the start of the do body",
                "if_goto 63",
                arrayResult.getCommands().get(77));

        TestCaseResult danglingIfCase = findCase(runReport, "text2");
        assertNotNull("missing dangling if case", danglingIfCase);
        SemanticAnalysisResult danglingIfResult = danglingIfCase.getSemanticResult();
        assertNotNull("semantic result missing for dangling if case", danglingIfResult);
        int thenAddIndex = danglingIfResult.getCommands().indexOf("st_plus_int32");
        assertTrue("then branch arithmetic should exist", thenAddIndex >= 0);
        int thenAssignIndex = thenAddIndex + 1;
        assertEquals("then branch should end with assignment",
                "assign_from_st_top_to_ref_int32",
                danglingIfResult.getCommands().get(thenAssignIndex));
        int skipElseGotoIndex = thenAssignIndex + 1;
        assertTrue("then branch should be followed by a goto that skips else branch",
                danglingIfResult.getCommands().get(skipElseGotoIndex).startsWith("goto "));
        assertEquals("outer if should jump to the same join point after then branch and when condition is false",
                danglingIfResult.getCommands().get(11).replace("ifn_goto ", ""),
                danglingIfResult.getCommands().get(skipElseGotoIndex).replace("goto ", ""));

        TestCaseResult wideningCase = findCase(runReport, "text12-int32-to-float64-implicit-cast");
        assertNotNull("missing implicit cast widening case", wideningCase);
        SemanticAnalysisResult wideningResult = wideningCase.getSemanticResult();
        assertNotNull("semantic result missing for widening case", wideningResult);
        assertTrue("int32 to float64 assignment should insert cast",
                wideningResult.getCommands().contains("st_top_int32_cast_float64"));
        assertTrue("mixed int32/float64 arithmetic should use float64 operator",
                wideningResult.getCommands().contains("st_plus_float64"));
        assertTrue("float64 assignment should remain typed",
                wideningResult.getCommands().contains("assign_from_st_top_to_ref_float64"));

        TestCaseResult narrowingCase = findCase(runReport, "text13-float64-to-int32-invalid");
        assertNotNull("missing invalid narrowing case", narrowingCase);
        assertTrue("float64 to int32 assignment should be expected failure", narrowingCase.isExpectedFailure());
        assertTrue("float64 to int32 assignment should report semantic error or failure",
                narrowingCase.getErrorCount() > 0 || narrowingCase.getFailure() != null);

        TestCaseResult mixedRelCase = findCase(runReport, "text14-mixed-relational-int-float");
        assertNotNull("missing mixed relational numeric case", mixedRelCase);
        SemanticAnalysisResult mixedRelResult = mixedRelCase.getSemanticResult();
        assertNotNull("semantic result missing for mixed relational case", mixedRelResult);
        assertTrue("mixed relational comparison should widen before compare",
                mixedRelResult.getCommands().contains("st_top_int32_cast_float64"));
        assertTrue("mixed relational comparison should use float64 comparison",
                mixedRelResult.getCommands().contains("st_less_float64"));

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

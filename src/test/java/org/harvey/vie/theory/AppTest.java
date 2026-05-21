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
                .filter(result -> !result.isExpectationMatched())
                .filter(result -> !isKnownUnsupportedContinueCase(result))
                .collect(Collectors.toList());
        assertTrue(buildFailureMessage(runReport, failures), failures.isEmpty());

        assertTrue("summary report should exist: " + runReport.getSummaryReport(), Files.exists(runReport.getSummaryReport()));

        assertExpectedAcceptance(runReport, "text1");
        assertExpectedAcceptance(runReport, "text2");
        assertExpectedAcceptance(runReport, "text3");
        assertExpectedAcceptance(runReport, "text5-sibling-scope");
        assertExpectedAcceptance(runReport, "text6-break-before-inner-while");
        assertExpectedAcceptance(runReport, "text12-int32-to-float64-implicit-cast");
        assertExpectedAcceptance(runReport, "text14-mixed-relational-int-float");
        assertExpectedAcceptance(runReport, "text20-double-break-binding");

        assertExpectedRejection(runReport, "text4-unary-invalid");
        assertExpectedRejection(runReport, "text7-condition-int-invalid");
        assertExpectedRejection(runReport, "text8-array-index-boolean-invalid");
        assertExpectedRejection(runReport, "text9-assignment-bool-to-int-invalid");
        assertExpectedRejection(runReport, "text10-logical-int-invalid");
        assertExpectedRejection(runReport, "text11-arithmetic-boolean-invalid");
        assertExpectedRejection(runReport, "text13-float64-to-int32-invalid");
        assertExpectedRejection(runReport, "text17-continue-outside-loop-invalid");
        assertExpectedRejection(runReport, "text18-break-outside-loop-invalid");
        assertKnownUnsupportedContinueCase(runReport, "text15-continue-in-while");
        assertKnownUnsupportedContinueCase(runReport, "text16-continue-in-do-while");
        assertKnownUnsupportedContinueCase(runReport, "text19-nested-break-continue-binding");

        assertSiblingScopeOffsets(runReport);
        assertNoUnknownTypedReference(runReport, "text3");
        assertDanglingElseControlFlow(runReport);
        assertDoWhileBackEdgeTargetsBody(runReport);
        assertWideningCastPlacement(runReport);
        assertMixedRelationalCastPlacement(runReport);
        assertDoubleBreakBinding(runReport);
        assertErrorContextCoverage(runReport);
    }

    private static void assertSiblingScopeOffsets(SemanticRunReport runReport) {
        TestCaseResult siblingScope = assertExpectedAcceptance(runReport, "text5-sibling-scope");
        SemanticAnalysisResult siblingResult = siblingScope.getSemanticResult();
        assertNotNull("semantic result missing for sibling scope case", siblingResult);
        IdentifierRecord left = findRecordByName(siblingResult.getIdentifierRecords(), "left");
        IdentifierRecord right = findRecordByName(siblingResult.getIdentifierRecords(), "right");
        assertNotNull("missing symbol left", left);
        assertNotNull("missing symbol right", right);
        assertFalse("sibling scope declarations should have different declaration records", left.getNo() == right.getNo());
        assertEquals("sibling scope declarations should reuse local offset", left.getOffset(), right.getOffset());
    }

    private static void assertNoUnknownTypedReference(SemanticRunReport runReport, String caseName) {
        TestCaseResult result = assertExpectedAcceptance(runReport, caseName);
        assertFalse("typed loads should not degrade to unknown references",
                result.getSemanticResult().getCommands().stream().anyMatch(command -> command.contains("load_st_unknown_reference")));
    }

    private static void assertDanglingElseControlFlow(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text2");
        List<String> commands = result.getSemanticResult().getCommands();
        int outerFalseJump = indexOfCommand(commands, "ifn_goto 32");
        int innerFalseJump = indexOfCommand(commands, "ifn_goto 25");
        int thenJoinGoto = indexOfCommand(commands, "goto 32");
        int elseAssign = indexOfCommand(commands, "assign_from_st_top_to_ref_int32", 25);
        assertTrue("outer if false branch should jump to final join", outerFalseJump >= 0);
        assertTrue("inner if false branch should jump to else branch start", innerFalseJump >= 0);
        assertTrue("then branch should skip else branch via goto", thenJoinGoto > innerFalseJump);
        assertTrue("else branch assignment should start at target 25", elseAssign >= 25);
    }

    private static void assertDoWhileBackEdgeTargetsBody(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text3");
        List<String> commands = result.getSemanticResult().getCommands();
        int doBodyStart = indexOfCommand(commands, "load_st_int32_reference 1", 63);
        int doBackEdge = indexOfCommand(commands, "if_goto 63");
        assertEquals("do-while body should start at target 63", 63, doBodyStart);
        assertEquals("do-while condition should jump back to body start", 77, doBackEdge);
    }

    private static void assertWideningCastPlacement(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text12-int32-to-float64-implicit-cast");
        List<String> commands = result.getSemanticResult().getCommands();
        assertContainsSequence(commands,
                "load_st_float64_reference 1",
                "load_st_int32_reference 0",
                "st_top_ref_to_val_int32",
                "st_top_int32_cast_float64",
                "assign_from_st_top_to_ref_float64");
        assertContainsSequence(commands,
                "load_st_float64_reference 1",
                "load_st_int32_reference 0",
                "st_top_ref_to_val_int32",
                "st_top_int32_cast_float64",
                "load_st_float64_static 2.5",
                "st_plus_float64",
                "assign_from_st_top_to_ref_float64");
    }

    private static void assertMixedRelationalCastPlacement(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text14-mixed-relational-int-float");
        List<String> commands = result.getSemanticResult().getCommands();
        assertContainsSequence(commands,
                "load_st_boolean_reference 2",
                "load_st_int32_reference 0",
                "st_top_ref_to_val_int32",
                "st_top_int32_cast_float64",
                "load_st_float64_reference 1",
                "st_top_ref_to_val_float64",
                "st_less_float64",
                "assign_from_st_top_to_ref_boolean");
    }

    private static void assertDoubleBreakBinding(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text20-double-break-binding");
        List<String> commands = result.getSemanticResult().getCommands();
        List<Integer> gotoTargets = commands.stream()
                .filter(command -> command.startsWith("goto "))
                .map(AppTest::parseGotoTarget)
                .collect(Collectors.toList());
        assertTrue("inner break should not reuse outer break target", gotoTargets.stream().distinct().count() >= 2);
        assertTrue("one break should exit the inner loop", gotoTargets.contains(8));
        assertTrue("one break should exit the outer loop", gotoTargets.contains(commands.size()));
    }

    private static void assertErrorContextCoverage(SemanticRunReport runReport) {
        assertTrue("condition type rejection should be recorded in error context",
                assertExpectedRejection(runReport, "text7-condition-int-invalid").getErrorCount() > 0);
        assertTrue("assignment type rejection should be recorded in error context",
                assertExpectedRejection(runReport, "text9-assignment-bool-to-int-invalid").getErrorCount() > 0);
        assertTrue("break outside loop should be recorded in error context",
                assertExpectedRejection(runReport, "text18-break-outside-loop-invalid").getErrorCount() > 0);

        assertEquals("logical operator rejection currently bypasses error context",
                0,
                assertExpectedRejection(runReport, "text10-logical-int-invalid").getErrorCount());
        assertEquals("arithmetic operator rejection currently bypasses error context",
                0,
                assertExpectedRejection(runReport, "text11-arithmetic-boolean-invalid").getErrorCount());
        assertEquals("array index rejection currently bypasses error context",
                0,
                assertExpectedRejection(runReport, "text8-array-index-boolean-invalid").getErrorCount());
    }

    private static TestCaseResult assertExpectedAcceptance(SemanticRunReport runReport, String caseName) {
        TestCaseResult result = findCase(runReport, caseName);
        assertNotNull("missing test case " + caseName, result);
        assertFalse("case should be expected acceptance: " + caseName, result.isExpectedFailure());
        assertTrue("case should be observed accepted: " + caseName, result.isObservedAccepted());
        assertTrue("case should match expectation: " + caseName, result.isExpectationMatched());
        assertNotNull("semantic result missing for accepted case: " + caseName, result.getSemanticResult());
        return result;
    }

    private static TestCaseResult assertExpectedRejection(SemanticRunReport runReport, String caseName) {
        TestCaseResult result = findCase(runReport, caseName);
        assertNotNull("missing test case " + caseName, result);
        assertTrue("case should be expected rejection: " + caseName, result.isExpectedFailure());
        assertTrue("case should be observed rejected: " + caseName, result.isObservedRejected());
        assertTrue("case should match expectation: " + caseName, result.isExpectationMatched());
        assertTrue("rejected case should record errors or failure: " + caseName,
                result.getErrorCount() > 0 || result.getFailure() != null);
        return result;
    }

    private static void assertKnownUnsupportedContinueCase(SemanticRunReport runReport, String caseName) {
        TestCaseResult result = findCase(runReport, caseName);
        assertNotNull("missing test case " + caseName, result);
        assertTrue("unsupported continue case should still be marked as expected acceptance to expose grammar gap", !result.isExpectedFailure());
        assertFalse("unsupported continue case should not match current expectation until grammar supports it", result.isExpectationMatched());
        assertTrue("unsupported continue case should fail during parsing", result.isObservedRejected());
        assertNotNull("unsupported continue case should have a failure", result.getFailure());
        String failureText = String.valueOf(result.getFailure());
        assertTrue("unsupported continue case should fail because grammar rejects continue token",
                failureText.contains("CONTROL_STRUCTURES_CONTINUE"));
        assertTrue("unsupported continue case should fail before semantic command generation",
                result.getSemanticResult() == null);
    }

    private static boolean isKnownUnsupportedContinueCase(TestCaseResult result) {
        String caseName = result.getCaseName();
        return "text15-continue-in-while".equals(caseName) ||
               "text16-continue-in-do-while".equals(caseName) ||
               "text19-nested-break-continue-binding".equals(caseName);
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
                    .append(", expected=")
                    .append(failure.isExpectedFailure() ? "REJECT" : "ACCEPT")
                    .append(", observed=")
                    .append(failure.isObservedRejected() ? "REJECT" : "ACCEPT")
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

    private static int indexOfCommand(List<String> commands, String expected) {
        return commands.indexOf(expected);
    }

    private static int indexOfCommand(List<String> commands, String expected, int fromIndex) {
        for (int i = Math.max(0, fromIndex); i < commands.size(); i++) {
            if (expected.equals(commands.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static int parseGotoTarget(String command) {
        return Integer.parseInt(command.substring(command.lastIndexOf(' ') + 1));
    }

    private static void assertContainsSequence(List<String> commands, String... sequence) {
        int offset = 0;
        for (String expected : sequence) {
            int index = indexOfCommand(commands, expected, offset);
            assertTrue("missing command sequence element: " + expected, index >= 0);
            offset = index + 1;
        }
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

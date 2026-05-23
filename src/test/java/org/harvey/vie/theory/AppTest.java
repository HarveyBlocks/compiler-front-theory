package org.harvey.vie.theory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.harvey.vie.theory.demo.program.ProgramSyntaxTestRunner;
import org.harvey.vie.theory.demo.program.ProgramSyntaxTestRunner.SemanticRunReport;
import org.harvey.vie.theory.demo.program.ProgramSyntaxTestRunner.TestCaseResult;
import org.harvey.vie.theory.semantic.context.SemanticAnalysisResult;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.value.ConstantValue;

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
        assertExpectedAcceptance(runReport, "text15-continue-in-while");
        assertExpectedAcceptance(runReport, "text16-continue-in-do-while");
        assertExpectedAcceptance(runReport, "text19-nested-break-continue-binding");
        assertExpectedAcceptance(runReport, "text20-double-break-binding");
        assertExpectedAcceptance(runReport, "text21-constant-folding");
        assertExpectedAcceptance(runReport, "text22-constant-propagation");
        assertExpectedAcceptance(runReport, "text23-constant-reassigned");
        assertExpectedAcceptance(runReport, "text24-if-true-inline");
        assertExpectedAcceptance(runReport, "text25-if-false-elided");
        assertExpectedAcceptance(runReport, "text26-while-false-elided");
        assertExpectedAcceptance(runReport, "text27-do-while-false-once");
        assertExpectedAcceptance(runReport, "text28-nested-if-constant-inner-then");
        assertExpectedAcceptance(runReport, "text29-nested-if-constant-inner-else");
        assertExpectedAcceptance(runReport, "text30-nested-if-outer-false");
        assertExpectedAcceptance(runReport, "text31-function-return");
        assertExpectedAcceptance(runReport, "text32-function-call");
        assertExpectedAcceptance(runReport, "text36-function-call-arg-order");

        assertExpectedRejection(runReport, "text4-unary-invalid");
        assertExpectedRejection(runReport, "text7-condition-int-invalid");
        assertExpectedRejection(runReport, "text8-array-index-boolean-invalid");
        assertExpectedRejection(runReport, "text9-assignment-bool-to-int-invalid");
        assertExpectedRejection(runReport, "text10-logical-int-invalid");
        assertExpectedRejection(runReport, "text11-arithmetic-boolean-invalid");
        assertExpectedRejection(runReport, "text13-float64-to-int32-invalid");
        assertExpectedRejection(runReport, "text17-continue-outside-loop-invalid");
        assertExpectedRejection(runReport, "text18-break-outside-loop-invalid");
        assertExpectedRejection(runReport, "text33-function-missing-return-invalid");
        assertExpectedRejection(runReport, "text34-void-return-value-invalid");
        assertExpectedRejection(runReport, "text35-return-outside-function-invalid");
        assertExpectedRejection(runReport, "text37-function-call-arg-count-invalid");
        assertExpectedRejection(runReport, "text38-if-false-break-invalid");
        assertExpectedRejection(runReport, "text39-if-false-continue-invalid");
        assertExpectedRejection(runReport, "text40-function-conditional-return-invalid");

        assertSiblingScopeOffsets(runReport);
        assertNoUnknownTypedReference(runReport, "text3");
        assertDanglingElseControlFlow(runReport);
        assertDoWhileBackEdgeTargetsBody(runReport);
        assertWideningCastPlacement(runReport);
        assertMixedRelationalCastPlacement(runReport);
        assertWhileContinueTargetsCondition(runReport);
        assertDoWhileContinueTargetsCondition(runReport);
        assertNestedLoopBinding(runReport);
        assertDoubleBreakBinding(runReport);
        assertConstantFolding(runReport);
        assertConstantPropagation(runReport);
        assertConstantInvalidation(runReport);
        assertIfTrueInlined(runReport);
        assertIfFalseElided(runReport);
        assertWhileFalseElided(runReport);
        assertDoWhileFalseOnce(runReport);
        assertNestedIfConstantThen(runReport);
        assertNestedIfConstantElse(runReport);
        assertNestedIfOuterFalse(runReport);
        assertFunctionReturn(runReport);
        assertFunctionCall(runReport);
        assertDeadBranchControlFlowIsStillDiagnosed(runReport);
        assertConditionalReturnDoesNotSatisfyFunction(runReport);
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

    private static void assertWhileContinueTargetsCondition(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text15-continue-in-while");
        List<String> commands = result.getSemanticResult().getCommands();
        assertContainsSequence(commands,
                "load_st_int32_reference 0",
                "st_top_ref_to_val_int32",
                "load_st_int32_static 3",
                "st_less_int32",
                "ifn_goto 25");
        assertTrue("continue in while should jump back to condition start",
                commands.contains("goto 6"));
        assertEquals("while continue case should contain exactly two back edges to condition",
                2,
                commands.stream().filter("goto 6"::equals).count());
    }

    private static void assertDoWhileContinueTargetsCondition(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text16-continue-in-do-while");
        List<String> commands = result.getSemanticResult().getCommands();
        assertTrue("continue in do-while should jump to condition evaluation point",
                commands.contains("goto 19"));
        assertContainsSequence(commands,
                "goto 19",
                "load_st_int32_reference 0",
                "st_top_ref_to_val_int32",
                "load_st_int32_static 3",
                "st_less_int32",
                "if_goto 6");
    }

    private static void assertNestedLoopBinding(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text19-nested-break-continue-binding");
        List<String> commands = result.getSemanticResult().getCommands();
        assertTrue("inner continue should jump back to inner loop condition start", commands.contains("goto 14"));
        assertTrue("inner break should jump to inner loop exit", commands.contains("goto 44"));
        assertTrue("outer loop back edge should jump to outer condition start", commands.contains("goto 6"));
        assertEquals("inner continue and normal inner-loop back edge should both target inner condition",
                2,
                commands.stream().filter("goto 14"::equals).count());
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

    private static void assertConstantFolding(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text21-constant-folding");
        SemanticAnalysisResult semanticResult = result.getSemanticResult();
        IdentifierRecord a = findRecordByName(semanticResult.getIdentifierRecords(), "a");
        IdentifierRecord ok = findRecordByName(semanticResult.getIdentifierRecords(), "ok");
        assertNotNull("missing symbol a", a);
        assertNotNull("missing symbol ok", ok);
        assertConstantInt32(a, 7);
        assertConstantBoolean(ok, true);
    }

    private static void assertConstantPropagation(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text22-constant-propagation");
        SemanticAnalysisResult semanticResult = result.getSemanticResult();
        IdentifierRecord a = findRecordByName(semanticResult.getIdentifierRecords(), "a");
        IdentifierRecord b = findRecordByName(semanticResult.getIdentifierRecords(), "b");
        IdentifierRecord ok = findRecordByName(semanticResult.getIdentifierRecords(), "ok");
        assertNotNull("missing symbol a", a);
        assertNotNull("missing symbol b", b);
        assertNotNull("missing symbol ok", ok);
        assertConstantInt32(a, 7);
        assertConstantInt32(b, 8);
        assertConstantBoolean(ok, true);
    }

    private static void assertConstantInvalidation(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text23-constant-reassigned");
        SemanticAnalysisResult semanticResult = result.getSemanticResult();
        IdentifierRecord a = findRecordByName(semanticResult.getIdentifierRecords(), "a");
        IdentifierRecord b = findRecordByName(semanticResult.getIdentifierRecords(), "b");
        assertNotNull("missing symbol a", a);
        assertNotNull("missing symbol b", b);
        assertNull("a should lose constant state after reassignment", a.getConstantValue());
        assertNull("b should not be treated as constant after reading reassigned a", b.getConstantValue());
        assertContainsSequence(semanticResult.getCommands(),
                "load_st_int32_reference 0",
                "load_st_int32_static 2",
                "assign_from_st_top_to_ref_int32",
                "load_st_identifier_reference b",
                "load_st_int32_reference 0",
                "st_top_ref_to_val_int32",
                "load_st_int32_static 1",
                "st_plus_int32",
                "assign_from_st_top_to_ref_int32");
    }

    private static void assertIfTrueInlined(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text24-if-true-inline");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse("if(true) should be inlined without conditional branch", commands.stream().anyMatch(c -> c.startsWith("ifn_goto")));
        assertFalse("if(true) should not emit unconditional gotos", commands.stream().anyMatch(c -> c.startsWith("goto ")));
        assertContainsSequence(commands,
                "load_st_int32_reference 0",
                "load_st_int32_static 1",
                "assign_from_st_top_to_ref_int32",
                "load_st_int32_reference 0",
                "load_st_int32_static 2",
                "assign_from_st_top_to_ref_int32",
                "load_st_int32_reference 0",
                "load_st_int32_static 3",
                "assign_from_st_top_to_ref_int32");
    }

    private static void assertIfFalseElided(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text25-if-false-elided");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse("if(false) should be removed without conditional branch", commands.stream().anyMatch(c -> c.startsWith("ifn_goto")));
        assertFalse("if(false) should not emit unconditional gotos", commands.stream().anyMatch(c -> c.startsWith("goto ")));
        assertContainsSequence(commands,
                "load_st_int32_reference 0",
                "load_st_int32_static 1",
                "assign_from_st_top_to_ref_int32",
                "load_st_int32_reference 0",
                "load_st_int32_static 3",
                "assign_from_st_top_to_ref_int32");
    }

    private static void assertWhileFalseElided(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text26-while-false-elided");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse("while(false) should be removed without conditional branch", commands.stream().anyMatch(c -> c.startsWith("ifn_goto")));
        assertFalse("while(false) should not emit unconditional gotos", commands.stream().anyMatch(c -> c.startsWith("goto ")));
        assertContainsSequence(commands,
                "load_st_int32_reference 0",
                "load_st_int32_static 1",
                "assign_from_st_top_to_ref_int32",
                "load_st_int32_reference 0",
                "load_st_int32_static 3",
                "assign_from_st_top_to_ref_int32");
    }

    private static void assertDoWhileFalseOnce(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text27-do-while-false-once");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse("do-while(false) should not emit conditional branch", commands.stream().anyMatch(c -> c.startsWith("if_goto")));
        assertFalse("do-while(false) should not emit back edges", commands.stream().anyMatch(c -> c.startsWith("goto ")));
        assertContainsSequence(commands,
                "load_st_int32_reference 0",
                "load_st_int32_static 1",
                "assign_from_st_top_to_ref_int32",
                "load_st_int32_reference 0",
                "load_st_int32_static 2",
                "assign_from_st_top_to_ref_int32",
                "load_st_int32_reference 0",
                "load_st_int32_static 3",
                "assign_from_st_top_to_ref_int32");
    }

    private static void assertNestedIfConstantThen(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text28-nested-if-constant-inner-then");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse("nested constant if should not keep conditional branches", commands.stream().anyMatch(c -> c.startsWith("ifn_goto")));
        assertTrue("nested constant if should resolve to then branch", commands.contains("load_st_int32_static 1"));
        assertFalse("nested constant if should not keep else assignment 2", commands.contains("load_st_int32_static 2"));
        assertFalse("outer if(true) should not keep else assignment 3", commands.contains("load_st_int32_static 3"));
    }

    private static void assertNestedIfConstantElse(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text29-nested-if-constant-inner-else");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse("nested constant if should not keep conditional branches", commands.stream().anyMatch(c -> c.startsWith("ifn_goto")));
        assertFalse("nested constant if should not keep then assignment 1", commands.contains("load_st_int32_static 1"));
        assertTrue("nested constant if should resolve to else branch", commands.contains("load_st_int32_static 2"));
        assertFalse("outer if(true) should not keep else assignment 3", commands.contains("load_st_int32_static 3"));
    }

    private static void assertNestedIfOuterFalse(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text30-nested-if-outer-false");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse("outer false branch should remove conditional branches", commands.stream().anyMatch(c -> c.startsWith("ifn_goto")));
        assertFalse("outer false branch should remove inner then assignment 1", commands.contains("load_st_int32_static 1"));
        assertFalse("outer false branch should remove inner else assignment 2", commands.contains("load_st_int32_static 2"));
        assertTrue("outer false branch should keep else assignment 3", commands.contains("load_st_int32_static 3"));
    }

    private static void assertFunctionReturn(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text31-function-return");
        List<String> commands = result.getSemanticResult().getCommands();
        assertContainsSequence(commands, "load_st_int32_reference 0", "st_top_ref_to_val_int32", "load_st_int32_static 1", "st_plus_int32", "return");
    }

    private static void assertFunctionCall(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text32-function-call");
        List<String> commands = result.getSemanticResult().getCommands();
        assertContainsSequence(commands, "load_st_int32_static 2", "call inc");
    }

    private static void assertDeadBranchControlFlowIsStillDiagnosed(SemanticRunReport runReport) {
        assertExpectedRejection(runReport, "text38-if-false-break-invalid");
        assertExpectedRejection(runReport, "text39-if-false-continue-invalid");
    }

    private static void assertConditionalReturnDoesNotSatisfyFunction(SemanticRunReport runReport) {
        assertExpectedRejection(runReport, "text40-function-conditional-return-invalid");
    }

    private static void assertFunctionCallArgOrder(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text36-function-call-arg-order");
        List<String> commands = result.getSemanticResult().getCommands();
        assertContainsSequence(commands,
                "load_st_int32_reference 0",
                "st_top_ref_to_val_int32",
                "load_st_int32_static 2",
                "st_plus_int32",
                "call add");
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

    private static void assertConstantInt32(IdentifierRecord record, int expected) {
        ConstantValue value = record.getConstantValue();
        assertNotNull("missing constant value for " + new String(record.getLexeme()), value);
        assertEquals("unexpected int32 constant value", expected, value.int32());
    }

    private static void assertConstantBoolean(IdentifierRecord record, boolean expected) {
        ConstantValue value = record.getConstantValue();
        assertNotNull("missing constant value for " + new String(record.getLexeme()), value);
        assertEquals("unexpected boolean constant value", expected, value.bool());
    }
}

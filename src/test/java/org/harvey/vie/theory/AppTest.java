package org.harvey.vie.theory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.harvey.vie.theory.demo.program.ProgramSyntaxTestRunner;
import org.harvey.vie.theory.demo.program.ProgramSyntaxTestRunner.SemanticRunReport;
import org.harvey.vie.theory.demo.program.ProgramSyntaxTestRunner.TestCaseResult;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;
import org.harvey.vie.theory.semantic.command.FunctionCommandSegment;
import org.harvey.vie.theory.semantic.context.SemanticAnalysisResult;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.semantic.value.ConstantValue;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Temper
 */
public class AppTest extends TestCase {
    public AppTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(AppTest.class);
        return suite;
    }

    private static void assertSiblingScopeOffsets(SemanticRunReport runReport) {
        TestCaseResult siblingScope = assertExpectedAcceptance(runReport, "text5-sibling-scope");
        SemanticAnalysisResult siblingResult = siblingScope.getSemanticResult();
        assertNotNull("semantic result missing for sibling scope case", siblingResult);
        IdentifierRecord left = findRecordByName(siblingResult.getIdentifierRecords(), "left");
        IdentifierRecord right = findRecordByName(siblingResult.getIdentifierRecords(), "right");
        assertNotNull("missing symbol left", left);
        assertNotNull("missing symbol right", right);
        assertFalse(
                "sibling scope declarations should have different declaration records",
                left.getNo() == right.getNo()
        );
        assertEquals("sibling scope declarations should reuse local offset", left.getOffset(), right.getOffset());
    }

    private static void assertNoUnknownTypedReference(SemanticRunReport runReport, String caseName) {
        TestCaseResult result = assertExpectedAcceptance(runReport, caseName);
        assertFalse(
                "typed loads should not degrade to unknown references",
                result.getSemanticResult()
                        .getCommands()
                        .stream()
                        .anyMatch(command -> command.contains("load_st_unknown_address"))
        );
    }

    private static void assertDanglingElseControlFlow(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text2");
        List<String> commands = result.getSemanticResult().getCommands();
        int outerFalseJump = indexOfPrefix(commands, "ifn_goto ");
        int innerFalseJump = indexOfPrefix(commands, "ifn_goto ", outerFalseJump + 1);
        int thenJoinGoto = indexOfPrefix(commands, "goto ", innerFalseJump + 1);
        assertTrue("outer if false branch should jump to final join", outerFalseJump >= 0);
        assertTrue("inner if false branch should jump to else branch start", innerFalseJump >= 0);
        assertTrue("then branch should skip else branch via goto", thenJoinGoto > innerFalseJump);
        assertEquals(
                "outer if false branch should target final join",
                32,
                parseGotoTarget(commands.get(outerFalseJump))
        );
        assertEquals(
                "inner if false branch should target else branch start",
                25,
                parseGotoTarget(commands.get(innerFalseJump))
        );
        assertEquals("then branch should skip to final join", 32, parseGotoTarget(commands.get(thenJoinGoto)));
    }

    private static void assertDoWhileBackEdgeTargetsBody(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text3");
        List<String> commands = result.getSemanticResult().getCommands();
        int doWhileExit = indexOfCommand(commands, "goto 15");
        int doBodyStart = indexOfCommand(commands, "load_st_int32_address 1", doWhileExit + 1);
        int doBackEdge = indexOfPrefix(commands, "if_goto ", doBodyStart);
        assertTrue("do-while body should be present near the loop tail", doBodyStart >= 0);
        assertTrue("do-while condition should jump back to body start", doBackEdge > doBodyStart);
        assertEquals(
                "do-while back edge should target the body start",
                doBodyStart,
                parseGotoTarget(commands.get(doBackEdge))
        );
    }

    private static void assertWideningCastPlacement(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text12-int32-to-float64-implicit-cast");
        List<String> commands = result.getSemanticResult().getCommands();
        assertContainsSequence(
                commands,
                "load_st_float64_address 1",
                "load_st_int32_address 0",
                "st_top_addr_to_val_int32",
                "st_top_int32_cast_float64",
                "assign_from_st_top_to_addr_float64"
        );
        assertContainsSequence(
                commands,
                "load_st_float64_address 1",
                "load_st_int32_address 0",
                "st_top_addr_to_val_int32",
                "st_top_int32_cast_float64",
                "load_st_float64_static 2.5",
                "st_plus_float64",
                "assign_from_st_top_to_addr_float64"
        );
    }

    private static void assertMixedRelationalCastPlacement(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text14-mixed-relational-int-float");
        List<String> commands = result.getSemanticResult().getCommands();
        assertContainsSequence(
                commands,
                "load_st_boolean_address 2",
                "load_st_int32_address 0",
                "st_top_addr_to_val_int32",
                "st_top_int32_cast_float64",
                "load_st_float64_address 1",
                "st_top_addr_to_val_float64",
                "st_less_float64",
                "assign_from_st_top_to_addr_boolean"
        );
    }

    private static void assertDoubleBreakBinding(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text20-double-break-binding");
        List<String> commands = result.getSemanticResult().getCommands();
        List<Integer> gotoTargets = commands.stream()
                .filter(command -> command.startsWith("goto "))
                .map(AppTest::parseGotoTarget)
                .collect(Collectors.toList());
        assertTrue("inner break should not reuse outer break target", gotoTargets.stream().distinct().count() >= 2);
        assertTrue("one break should exit the inner loop", gotoTargets.contains(12));
        assertTrue("one break should exit the outer loop", gotoTargets.contains(14));
    }

    private static void assertWhileContinueTargetsCondition(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text15-continue-in-while");
        List<String> commands = result.getSemanticResult().getCommands();
        int conditionStart = indexOfCommand(commands, "load_st_int32_address 0", 6);
        int conditionBranch = indexOfPrefix(commands, "ifn_goto ", conditionStart);
        assertTrue("while condition should be emitted", conditionStart >= 0);
        assertTrue("while condition should end with ifn_goto", conditionBranch > conditionStart);
        long backEdges = commands.stream()
                .filter(command -> command.equals("goto " + conditionStart))
                .count();
        assertEquals("while continue case should contain exactly two back edges to condition", 2, backEdges);
    }

    private static void assertDoWhileContinueTargetsCondition(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text16-continue-in-do-while");
        List<String> commands = result.getSemanticResult().getCommands();
        int doBodyStart = indexOfCommand(commands, "load_st_int32_address 0", 6);
        int conditionStart = indexOfCommand(commands, "load_st_int32_address 0", 19);
        int continueJump = indexOfCommand(commands, "goto " + conditionStart);
        int backEdge = indexOfPrefix(commands, "if_goto ", conditionStart);
        assertTrue("continue in do-while should jump to condition evaluation point", continueJump >= 0);
        assertTrue("do-while should contain a conditional back edge", backEdge > conditionStart);
        assertEquals(
                "do-while back edge should target body start",
                doBodyStart,
                parseGotoTarget(commands.get(backEdge))
        );
    }

    private static void assertNestedLoopBinding(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text19-nested-break-continue-binding");
        List<String> commands = result.getSemanticResult().getCommands();
        int innerContinue = indexOfCommand(commands, "goto 14");
        int innerBreak = indexOfCommand(commands, "goto 44");
        int outerLoopBack = indexOfCommand(commands, "goto 6");
        assertTrue("inner continue should jump back to inner loop condition start", innerContinue >= 0);
        assertTrue("inner break should jump to inner loop exit", innerBreak >= 0);
        assertTrue("outer loop back edge should jump to outer condition start", outerLoopBack >= 0);
        assertEquals(
                "inner continue should target the inner loop condition",
                14,
                parseGotoTarget(commands.get(innerContinue))
        );
        assertEquals("inner break should target the inner loop exit", 44, parseGotoTarget(commands.get(innerBreak)));
        assertEquals(
                "outer loop back edge should target the outer loop condition",
                6,
                parseGotoTarget(commands.get(outerLoopBack))
        );
    }

    private static void assertErrorContextCoverage(SemanticRunReport runReport) {
        assertTrue(
                "condition type rejection should be recorded in error context",
                assertExpectedRejection(runReport, "text7-condition-int-invalid").getErrorCount() > 0
        );
        assertTrue(
                "assignment type rejection should be recorded in error context",
                assertExpectedRejection(runReport, "text9-assignment-bool-to-int-invalid").getErrorCount() > 0
        );
        assertTrue(
                "break outside loop should be recorded in error context",
                assertExpectedRejection(runReport, "text18-break-outside-loop-invalid").getErrorCount() > 0
        );

        assertEquals(
                "logical operator rejection currently bypasses error context",
                0,
                assertExpectedRejection(runReport, "text10-logical-int-invalid").getErrorCount()
        );
        assertEquals(
                "arithmetic operator rejection currently bypasses error context",
                0,
                assertExpectedRejection(runReport, "text11-arithmetic-boolean-invalid").getErrorCount()
        );
        assertEquals(
                "array index rejection currently bypasses error context",
                0,
                assertExpectedRejection(runReport, "text8-array-index-boolean-invalid").getErrorCount()
        );
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
        assertContainsSequence(
                semanticResult.getCommands(),
                "load_st_int32_static 2",
                "assign_from_st_top_to_addr_int32",
                "load_st_int32_address " + b.getOffset(),
                "load_st_int32_address " + a.getOffset(),
                "st_top_addr_to_val_int32",
                "load_st_int32_static 1",
                "st_plus_int32",
                "assign_from_st_top_to_addr_int32"
        );
    }

    private static void assertIfTrueInlined(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text24-if-true-inline");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse(
                "if(true) should be inlined without conditional branch",
                commands.stream().anyMatch(c -> c.startsWith("ifn_goto"))
        );
        assertFalse(
                "if(true) should not emit unconditional gotos",
                commands.stream().anyMatch(c -> c.startsWith("goto "))
        );
        assertContainsSequence(
                commands,
                "load_st_int32_address 0",
                "load_st_int32_static 1",
                "assign_from_st_top_to_addr_int32",
                "load_st_int32_address 0",
                "load_st_int32_static 2",
                "assign_from_st_top_to_addr_int32",
                "load_st_int32_address 0",
                "load_st_int32_static 3",
                "assign_from_st_top_to_addr_int32"
        );
    }

    private static void assertIfFalseElided(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text25-if-false-elided");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse(
                "if(false) should be removed without conditional branch",
                commands.stream().anyMatch(c -> c.startsWith("ifn_goto"))
        );
        assertFalse(
                "if(false) should not emit unconditional gotos",
                commands.stream().anyMatch(c -> c.startsWith("goto "))
        );
        assertContainsSequence(
                commands,
                "load_st_int32_address 0",
                "load_st_int32_static 1",
                "assign_from_st_top_to_addr_int32",
                "load_st_int32_address 0",
                "load_st_int32_static 3",
                "assign_from_st_top_to_addr_int32"
        );
    }

    private static void assertWhileFalseElided(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text26-while-false-elided");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse(
                "while(false) should be removed without conditional branch",
                commands.stream().anyMatch(c -> c.startsWith("ifn_goto"))
        );
        assertFalse(
                "while(false) should not emit unconditional gotos",
                commands.stream().anyMatch(c -> c.startsWith("goto "))
        );
        assertContainsSequence(
                commands,
                "load_st_int32_address 0",
                "load_st_int32_static 1",
                "assign_from_st_top_to_addr_int32",
                "load_st_int32_address 0",
                "load_st_int32_static 3",
                "assign_from_st_top_to_addr_int32"
        );
    }

    private static void assertDoWhileFalseOnce(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text27-do-while-false-once");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse(
                "do-while(false) should not emit conditional branch",
                commands.stream().anyMatch(c -> c.startsWith("if_goto"))
        );
        assertFalse(
                "do-while(false) should not emit back edges",
                commands.stream().anyMatch(c -> c.startsWith("goto "))
        );
        assertContainsSequence(
                commands,
                "load_st_int32_address 0",
                "load_st_int32_static 1",
                "assign_from_st_top_to_addr_int32",
                "load_st_int32_address 0",
                "load_st_int32_static 2",
                "assign_from_st_top_to_addr_int32",
                "load_st_int32_address 0",
                "load_st_int32_static 3",
                "assign_from_st_top_to_addr_int32"
        );
    }

    private static void assertNestedIfConstantThen(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text28-nested-if-constant-inner-then");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse(
                "nested constant if should not keep conditional branches",
                commands.stream().anyMatch(c -> c.startsWith("ifn_goto"))
        );
        assertTrue("nested constant if should resolve to then branch", commands.contains("load_st_int32_static 1"));
        assertFalse(
                "nested constant if should not keep else assignment 2",
                commands.contains("load_st_int32_static 2")
        );
        assertFalse("outer if(true) should not keep else assignment 3", commands.contains("load_st_int32_static 3"));
    }

    private static void assertNestedIfConstantElse(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text29-nested-if-constant-inner-else");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse(
                "nested constant if should not keep conditional branches",
                commands.stream().anyMatch(c -> c.startsWith("ifn_goto"))
        );
        assertFalse(
                "nested constant if should not keep then assignment 1",
                commands.contains("load_st_int32_static 1")
        );
        assertTrue("nested constant if should resolve to else branch", commands.contains("load_st_int32_static 2"));
        assertFalse("outer if(true) should not keep else assignment 3", commands.contains("load_st_int32_static 3"));
    }

    private static void assertNestedIfOuterFalse(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text30-nested-if-outer-false");
        List<String> commands = result.getSemanticResult().getCommands();
        assertFalse(
                "outer false branch should remove conditional branches",
                commands.stream().anyMatch(c -> c.startsWith("ifn_goto"))
        );
        assertFalse(
                "outer false branch should remove inner then assignment 1",
                commands.contains("load_st_int32_static 1")
        );
        assertFalse(
                "outer false branch should remove inner else assignment 2",
                commands.contains("load_st_int32_static 2")
        );
        assertTrue("outer false branch should keep else assignment 3", commands.contains("load_st_int32_static 3"));
    }

    private static void assertFunctionReturn(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text31-function-return");
        List<String> commands = functionCommands(result.getSemanticResult(), "addOne");
        assertContainsSequence(
                commands,
                "load_st_int32_address 0",
                "st_top_addr_to_val_int32",
                "load_st_int32_static 1",
                "st_plus_int32",
                "return"
        );
    }

    private static void assertFunctionCall(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text32-function-call");
        List<String> commands = result.getSemanticResult().getCommands();
        assertContainsSequence(commands, "call 0", "assign_from_st_top_to_addr_int32");
    }

    private static void assertFunctionSegmentation(SemanticRunReport runReport) {
        SemanticAnalysisResult returnOnly = assertExpectedAcceptance(
                runReport,
                "text31-function-return"
        ).getSemanticResult();
        assertTrue("function-only program should not synthesize entry commands", returnOnly.getCommands().isEmpty());
        assertEquals(
                "function-only program should have one function segment",
                1,
                returnOnly.getFunctionSegments().size()
        );
        assertEquals(
                "function-only program should have one function table entry",
                1,
                returnOnly.getFunctionTable().size()
        );
        assertEquals(
                "function-only program should not have global locals",
                0,
                returnOnly.getEntryLocalVariables().length
        );
        assertEquals("function parameter should belong to function local table", 1,
                returnOnly.getFunctionLocalVariables(returnOnly.getFunctionTable().get(0)).length
        );

        SemanticAnalysisResult callCase = assertExpectedAcceptance(
                runReport,
                "text32-function-call"
        ).getSemanticResult();
        assertEquals("call case should keep one function table entry", 1, callCase.getFunctionTable().size());
        assertEquals("call case should keep one function segment", 1, callCase.getFunctionSegments().size());
        assertEquals("call case should keep one global local", 1, callCase.getEntryLocalVariables().length);
        assertEquals("call case should keep one function local", 1,
                callCase.getFunctionLocalVariables(callCase.getFunctionTable().get(0)).length
        );
        assertEquals(
                "call target should reference function table index 0",
                0,
                callCase.getFunctionTable().get(0).getTableIndex()
        );
        assertEquals("function table entry should match callee name", "inc",
                SourceTokenStringMapping.utf8(callCase.getFunctionTable().get(0).getSignature().getNameToken())
        );
        assertEquals("entry segment should only contain global code for call case", 4, callCase.getCommands().size());
        assertEquals("function segment should keep function body isolated", 5,
                functionCommands(callCase, "inc").size()
        );
    }

    private static void assertStructDeclarationAndAccess(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text41-struct-declare-and-access");
        SemanticAnalysisResult semanticResult = result.getSemanticResult();
        IdentifierRecord p = findRecordByName(semanticResult.getIdentifierRecords(), "p");
        assertNotNull("missing symbol p", p);
        assertEquals("struct table should contain one declared struct", 1, semanticResult.getStructTable().size());
        StructRecord point = semanticResult.getStructTable().get(0);
        assertEquals("struct table index should start from 0", 0, point.getTableIndex());
        assertEquals("struct name should be preserved in struct table", "Point", point.displayName());
        assertEquals("struct field count should match declaration", 2, point.getFields().size());
        assertEquals(
                "first field should be x",
                "x",
                SourceTokenStringMapping.utf8(point.getFields().get(0).getNameToken())
        );
        assertEquals("first field offset should be 0", 0, point.getFields().get(0).getOffset());
        assertEquals(
                "second field should be y",
                "y",
                SourceTokenStringMapping.utf8(point.getFields().get(1).getNameToken())
        );
        assertEquals("second field offset should be 1", 1, point.getFields().get(1).getOffset());
        List<String> commands = semanticResult.getCommands();
        assertTrue("struct construction should reference struct table index", commands.contains("new_struct 0"));
        assertTrue(
                "member access should emit field bias",
                commands.stream().anyMatch(c -> c.startsWith("bias_from_st_top_to_ref_int32"))
        );
    }

    private static void assertStructNullAssignment(SemanticRunReport runReport) {
        TestCaseResult result = assertExpectedAcceptance(runReport, "text42-struct-null-assignment");
        List<String> commands = result.getSemanticResult().getCommands();
        assertTrue(
                "null assignment should be allowed for struct references",
                commands.contains("load_st_null_static null")
        );
    }

    private static void assertArrayCreationInstructions(SemanticRunReport runReport) {
        List<String> full = assertExpectedAcceptance(runReport, "text45-array-create-fully-specified")
                .getSemanticResult()
                .getCommands();
        assertContainsSequence(
                full,
                "load_st_ref_address 0",
                "load_st_int32_static 11",
                "load_st_int32_static 12",
                "load_st_int32_static 13",
                "new_array_int32 3 3",
                "assign_from_st_top_to_addr_ref"
        );

        List<String> omitOne = assertExpectedAcceptance(runReport, "text46-array-create-tail-omitted-one")
                .getSemanticResult()
                .getCommands();
        assertContainsSequence(
                omitOne,
                "load_st_ref_address 0",
                "load_st_int32_static 11",
                "load_st_int32_static 12",
                "new_array_int32 3 2",
                "assign_from_st_top_to_addr_ref"
        );

        List<String> omitTwo = assertExpectedAcceptance(runReport, "text47-array-create-tail-omitted-two")
                .getSemanticResult()
                .getCommands();
        assertContainsSequence(
                omitTwo,
                "load_st_ref_address 0",
                "load_st_int32_static 11",
                "new_array_int32 3 1",
                "assign_from_st_top_to_addr_ref"
        );
    }

    private static void assertNestedStructArrayLValueAssignment(SemanticRunReport runReport) {
        SemanticAnalysisResult semanticResult = assertExpectedAcceptance(
                runReport,
                "text49-nested-struct-array-lvalue-assignment"
        )
                .getSemanticResult();
        List<String> commands = semanticResult.getCommands();
        assertTrue(
                "nested struct-array assignment should allocate school root object",
                commands.contains("new_struct 3")
        );
        assertTrue(
                "nested struct-array assignment should allocate grade array",
                commands.contains("new_array_ref 1 1")
        );
        assertTrue(
                "nested struct-array assignment should allocate classroom array",
                commands.contains("new_array_ref 1 1")
        );
        assertTrue(
                "nested struct-array assignment should allocate student array",
                commands.contains("new_array_ref 1 1")
        );
        assertStructFieldOffset(semanticResult, "School", "grade", 1);
        assertStructFieldOffset(semanticResult, "Grade", "class", 1);
        assertStructFieldOffset(semanticResult, "ClassRoom", "student", 1);
        assertStructFieldOffset(semanticResult, "Student", "gender", 1);
        assertContainsSequence(
                commands,
                "load_st_ref_address 0",
                "bias_from_st_top_to_ref_ref 1",
                "load_st_int32_static 4",
                "bias_from_st_top_to_ref_ref",
                "bias_from_st_top_to_ref_ref 1",
                "load_st_int32_static 4",
                "bias_from_st_top_to_ref_ref",
                "bias_from_st_top_to_ref_ref 1",
                "load_st_int32_static 2",
                "bias_from_st_top_to_ref_ref",
                "bias_from_st_top_to_ref_boolean 1",
                "load_st_ref_address 0",
                "bias_from_st_top_to_ref_ref 1",
                "load_st_int32_static 2",
                "bias_from_st_top_to_ref_ref",
                "bias_from_st_top_to_ref_ref 1",
                "load_st_int32_static 3",
                "bias_from_st_top_to_ref_ref",
                "bias_from_st_top_to_ref_ref 1",
                "load_st_int32_static 0",
                "bias_from_st_top_to_ref_ref",
                "bias_from_st_top_to_ref_boolean 1",
                "st_top_ref_to_val_boolean",
                "assign_from_st_top_to_ref_boolean"
        );
        assertContainsSequence(
                commands,
                "load_st_ref_address 0",
                "bias_from_st_top_to_ref_ref 1",
                "load_st_int32_static 2",
                "bias_from_st_top_to_ref_ref",
                "bias_from_st_top_to_ref_ref 1",
                "load_st_int32_static 3",
                "bias_from_st_top_to_ref_ref",
                "bias_from_st_top_to_ref_ref 1",
                "load_st_int32_static 0",
                "bias_from_st_top_to_ref_ref",
                "bias_from_st_top_to_ref_boolean 1",
                "load_st_ref_address 0",
                "bias_from_st_top_to_ref_ref 1",
                "load_st_int32_static 4",
                "bias_from_st_top_to_ref_ref",
                "bias_from_st_top_to_ref_ref 1",
                "load_st_int32_static 4",
                "bias_from_st_top_to_ref_ref",
                "bias_from_st_top_to_ref_ref 1",
                "load_st_int32_static 2",
                "bias_from_st_top_to_ref_ref",
                "bias_from_st_top_to_ref_boolean 1",
                "st_top_ref_to_val_boolean",
                "assign_from_st_top_to_ref_boolean"
        );
    }

    private static void assertFunctionStructAndArrayParameters(SemanticRunReport runReport) {
        SemanticAnalysisResult semanticResult = assertExpectedAcceptance(
                runReport,
                "text50-function-nested-struct-array-params"
        )
                .getSemanticResult();
        assertEquals(
                "combined parameter case should register three functions",
                3,
                semanticResult.getFunctionTable().size()
        );

        var readStudent = semanticResult.getFunctionTable().get(0);
        var readStudentArray = semanticResult.getFunctionTable().get(1);
        var pickFromSchool = semanticResult.getFunctionTable().get(2);

        assertEquals("first function name should be preserved", "readStudent",
                SourceTokenStringMapping.utf8(readStudent.getSignature().getNameToken())
        );
        assertEquals("second function name should be preserved", "readStudentArray",
                SourceTokenStringMapping.utf8(readStudentArray.getSignature().getNameToken())
        );
        assertEquals("third function name should be preserved", "pickFromSchool",
                SourceTokenStringMapping.utf8(pickFromSchool.getSignature().getNameToken())
        );

        assertEquals("readStudent should have one parameter", 1, readStudent.getParameters().size());
        assertTrue(
                "readStudent parameter should be struct-typed",
                readStudent.getParameters().get(0).getType().isStruct()
        );

        assertEquals("readStudentArray should have two parameters", 2, readStudentArray.getParameters().size());
        assertTrue(
                "readStudentArray first parameter should be an array",
                readStudentArray.getParameters().get(0).getType().isArray()
        );
        assertTrue(
                "readStudentArray first parameter element type should be struct",
                readStudentArray.getParameters().get(0).getType().arrayElementType().isStruct()
        );

        assertEquals("pickFromSchool should have three parameters", 3, pickFromSchool.getParameters().size());
        assertTrue(
                "pickFromSchool first parameter should be struct array",
                pickFromSchool.getParameters().get(0).getType().isArray()
        );
        assertTrue(
                "pickFromSchool second parameter should be nested array",
                pickFromSchool.getParameters().get(1).getType().isArray()
        );
        assertTrue(
                "pickFromSchool second parameter should remain array after one element dereference",
                pickFromSchool.getParameters().get(1).getType().arrayElementType().isArray()
        );
        assertTrue(
                "pickFromSchool third parameter should be struct",
                pickFromSchool.getParameters().get(2).getType().isStruct()
        );

        List<String> entryCommands = semanticResult.getCommands();
        assertContainsSequence(
                entryCommands,
                "load_st_boolean_address 4",
                "load_st_ref_address 2",
                "load_st_ref_address 3",
                "load_st_ref_address 0",
                "call 2",
                "assign_from_st_top_to_addr_boolean"
        );
        assertContainsSequence(
                entryCommands,
                "load_st_boolean_address 5",
                "load_st_ref_address 3",
                "load_st_int32_static 0",
                "bias_from_st_top_to_ref_ref",
                "load_st_int32_static 1",
                "call 1",
                "assign_from_st_top_to_addr_boolean"
        );

        List<String> readStudentArrayCommands = functionCommands(semanticResult, "readStudentArray");
        assertContainsSequence(
                readStudentArrayCommands,
                "load_st_ref_address 0",
                "load_st_int32_address 1",
                "st_top_addr_to_val_int32",
                "bias_from_st_top_to_ref_ref",
                "st_top_ref_to_val_ref",
                "call 0",
                "return"
        );

        List<String> pickFromSchoolCommands = functionCommands(semanticResult, "pickFromSchool");
        assertContainsSequence(
                pickFromSchoolCommands,
                "load_st_ref_address 0",
                "st_top_addr_to_val_ref",
                "load_st_int32_static 0",
                "call 1",
                "return"
        );
    }

    private static void assertStructErrors(SemanticRunReport runReport) {
        assertExpectedRejection(runReport, "text43-struct-duplicate-field-invalid");
        assertExpectedRejection(runReport, "text44-struct-missing-field-invalid");
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
        assertContainsSequence(
                commands,
                "assign_from_st_top_to_addr_int32",
                "load_st_int32_address 0",
                "st_top_addr_to_val_int32",
                "load_st_int32_static 2",
                "st_plus_int32",
                "call 0"
        );
    }

    private static void assertFunctionMultiArgOrder(SemanticRunReport runReport) {
        SemanticAnalysisResult semanticResult = assertExpectedAcceptance(runReport, "text48-function-multi-arg-order")
                .getSemanticResult();
        assertEquals(
                "multi-arg function case should have one function table entry",
                1,
                semanticResult.getFunctionTable().size()
        );
        assertEquals("first parameter should keep source order", "left",
                SourceTokenStringMapping.utf8(semanticResult.getFunctionTable()
                        .get(0)
                        .getParameters()
                        .get(0)
                        .getNameToken())
        );
        assertEquals("second parameter should keep source order", "right",
                SourceTokenStringMapping.utf8(semanticResult.getFunctionTable()
                        .get(0)
                        .getParameters()
                        .get(1)
                        .getNameToken())
        );
        assertContainsSequence(
                semanticResult.getCommands(),
                "load_st_int32_static 1",
                "load_st_int32_static 2",
                "call 0"
        );
    }

    private static List<String> functionCommands(SemanticAnalysisResult result, String name) {
        for (FunctionCommandSegment segment : result.getFunctionSegments()) {
            if (name.equals(SourceTokenStringMapping.utf8(segment.getFunction().getSignature().getNameToken()))) {
                return new org.harvey.vie.theory.semantic.command.ThreeAddressCodePrinter().print(segment.getCommands());
            }
        }
        fail("missing function command segment: " + name);
        return List.of();
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
        assertTrue(
                "rejected case should record errors or failure: " + caseName,
                result.getErrorCount() > 0 || result.getFailure() != null
        );
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

    private static int indexOfPrefix(List<String> commands, String prefix) {
        return indexOfPrefix(commands, prefix, 0);
    }

    private static int indexOfPrefix(List<String> commands, String prefix, int fromIndex) {
        for (int i = Math.max(0, fromIndex); i < commands.size(); i++) {
            if (commands.get(i).startsWith(prefix)) {
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

    private static void assertStructFieldOffset(
            SemanticAnalysisResult semanticResult,
            String structName,
            String fieldName,
            int expectedOffset) {
        StructRecord struct = semanticResult.getStructTable().stream()
                .filter(record -> structName.equals(record.displayName()))
                .findFirst()
                .orElse(null);
        assertNotNull("missing struct " + structName, struct);
        int actualOffset = struct.getFields().stream()
                .filter(field -> fieldName.equals(SourceTokenStringMapping.utf8(field.getNameToken())))
                .findFirst()
                .orElseThrow(() -> new AssertionError("missing field " + structName + "." + fieldName))
                .getOffset();
        assertEquals("unexpected field offset for " + structName + "." + fieldName, expectedOffset, actualOffset);
    }

    public void testProgramSemanticCases() {
        SemanticRunReport runReport = ProgramSyntaxTestRunner.run();
        List<TestCaseResult> failures = runReport.getResults().stream()
                .filter(result -> !result.isExpectationMatched())
                .collect(Collectors.toList());
        assertTrue(buildFailureMessage(runReport, failures), failures.isEmpty());

        assertTrue(
                "summary report should exist: " + runReport.getSummaryReport(),
                Files.exists(runReport.getSummaryReport())
        );

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
        assertExpectedAcceptance(runReport, "text41-struct-declare-and-access");
        assertExpectedAcceptance(runReport, "text42-struct-null-assignment");
        assertExpectedAcceptance(runReport, "text45-array-create-fully-specified");
        assertExpectedAcceptance(runReport, "text46-array-create-tail-omitted-one");
        assertExpectedAcceptance(runReport, "text47-array-create-tail-omitted-two");
        assertExpectedAcceptance(runReport, "text48-function-multi-arg-order");
        assertExpectedAcceptance(runReport, "text49-nested-struct-array-lvalue-assignment");
        assertExpectedAcceptance(runReport, "text50-function-nested-struct-array-params");

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
        assertExpectedRejection(runReport, "text43-struct-duplicate-field-invalid");
        assertExpectedRejection(runReport, "text44-struct-missing-field-invalid");

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
        assertFunctionCallArgOrder(runReport);
        assertFunctionSegmentation(runReport);
        assertFunctionMultiArgOrder(runReport);
        assertStructDeclarationAndAccess(runReport);
        assertStructNullAssignment(runReport);
        assertArrayCreationInstructions(runReport);
        assertNestedStructArrayLValueAssignment(runReport);
        assertFunctionStructAndArrayParameters(runReport);
        assertDeadBranchControlFlowIsStillDiagnosed(runReport);
        assertConditionalReturnDoesNotSatisfyFunction(runReport);
        assertErrorContextCoverage(runReport);
        assertStructErrors(runReport);
    }
}


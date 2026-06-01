package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.display.SemanticDisplaySupport;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.command.node.CommandContext;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.structure.StructField;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * 交互 demo 使用的打印回调：在 accept 前直接把命令段、符号表和结构体表打印到控制台。
 * <p>
 * 测试环境更常用 {@link SemanticResultCallback}，它把结果封装成
 * {@link org.harvey.vie.theory.semantic.context.SemanticAnalysisResult}；本类主要帮助课堂现场展示。
 * 它同样通过 {@link CommandSegmentSupport#flatten(CommandNodeRegister)} 展开入口段，并使用
 * {@link ThreeAddressCodePrinter} 给出可读的中间代码视图。
 * <p>
 * 讲完本类回到主线 {@link SemanticResultCallback}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:43
 */
public class SemanticCommandPrintCallback implements ShiftReduceCallback {

    @Override
    public void beforeAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        CommandNodeRegister top = topCommandNodeRegister(context);
        ShiftReduceCallback.super.beforeAccept(context, production);
        printResult(context, top);
    }

    private static CommandNodeRegister topCommandNodeRegister(ShiftReduceSemanticContext context) {
        CommandContext commandContext = context.getCommandContext();
        if (commandContext.size() != 1) {
            // 由于增广语法 S' -> S, 右部只有一个
            throw new CompilerException("illegal statement before accept on production.");
        }
        return commandContext.peek();
    }

    private static void printResult(ShiftReduceSemanticContext context, CommandNodeRegister top) {
        List<SemanticCommand> entryCommands = CommandSegmentSupport.flatten(top);
        List<StructRecord> structTable = context.structRecords();
        printStructTable(structTable);
        printGlobalSection(entryCommands, entryLocals(context.identifierRecords()), structTable);
        for (FunctionCommandSegment segment : context.getFunctionCommandSegmentContext().snapshot()) {
            printFunctionSection(segment, functionLocals(context.identifierRecords(), segment.getFunction()), structTable);
        }
    }

    private static void printSegment(String title, List<SemanticCommand> result) {
        System.out.println(title + ":");
        if (result.isEmpty()) {
            System.out.println("<empty>");
            return;
        }
        IdGenerator rawGenerator = new IdGenerator();
        for (SemanticCommand semanticCommand : result) {
            System.out.printf("[%03d] %s\n", rawGenerator.next(), semanticCommand);
        }
        System.out.println("semantic command view:");
        ThreeAddressCodePrinter printer = new ThreeAddressCodePrinter();
        List<String> lines = printer.print(result);
        for (String line : lines) {
            System.out.println(line);
        }
    }

    private static void printStructTable(List<StructRecord> structTable) {
        System.out.println("struct table:");
        if (structTable.isEmpty()) {
            System.out.println("<empty>");
            return;
        }
        for (StructRecord record : structTable) {
            System.out.println(SemanticDisplaySupport.formatStructRecord(record, structTable));
            for (StructField field : record.getFields()) {
                System.out.println("  " + SemanticDisplaySupport.formatStructField(field, structTable));
            }
        }
    }

    private static void printGlobalSection(List<SemanticCommand> commands, IdentifierRecord[] locals, List<StructRecord> structTable) {
        System.out.println("global segment:");
        printSegment("commands", commands);
        printIdentifierTable("local variables", locals, structTable);
    }

    private static void printFunctionSection(
            FunctionCommandSegment segment,
            IdentifierRecord[] locals,
            List<StructRecord> structTable) {
        FunctionRecord function = segment.getFunction();
        System.out.println("function segment:");
        System.out.println("index=" + function.getTableIndex());
        System.out.println("signature=" + SemanticDisplaySupport.formatFunctionSignature(function, structTable));
        printSegment("commands", segment.getCommands());
        printIdentifierTable("local variables", locals, structTable);
    }

    private static void printIdentifierTable(String title, IdentifierRecord[] records, List<StructRecord> structTable) {
        System.out.println(title + ":");
        if (records.length == 0) {
            System.out.println("<empty>");
            return;
        }
        for (IdentifierRecord record : records) {
            System.out.println(SemanticDisplaySupport.formatIdentifierRecord(record, structTable));
        }
    }

    private static IdentifierRecord[] entryLocals(IdentifierRecord[] records) {
        List<IdentifierRecord> result = new ArrayList<>();
        for (IdentifierRecord record : records) {
            if (record.getOwnerFunction() == null) {
                result.add(record);
            }
        }
        return result.toArray(IdentifierRecord[]::new);
    }

    private static IdentifierRecord[] functionLocals(IdentifierRecord[] records, FunctionRecord function) {
        List<IdentifierRecord> result = new ArrayList<>();
        for (IdentifierRecord record : records) {
            if (record.getOwnerFunction() == function) {
                result.add(record);
            }
        }
        return result.toArray(IdentifierRecord[]::new);
    }

}

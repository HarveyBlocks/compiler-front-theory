package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.node.CommandContext;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.display.SemanticDisplaySupport;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.structure.StructField;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:43
 */
public class SemanticCommandPrintCallback implements ShiftReduceCallback {
    /**
     * 函数功能：获取栈顶命令节点注册器。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * 输出：CommandNodeRegister 类型返回值。
     */

    private static CommandNodeRegister topCommandNodeRegister(ShiftReduceSemanticContext context) {
        CommandContext commandContext = context.getCommandContext();
        if (commandContext.size() != 1) {
            // 由于增广语法 S' -> S, 右部只有一个
            throw new CompilerException("illegal statement before accept on production.");
        }
        return commandContext.peek();
    }

    /**
     * 函数功能：打印语义结果。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - top：CommandNodeRegister 类型参数。
     * 输出：无。
     */

    private static void printResult(ShiftReduceSemanticContext context, CommandNodeRegister top) {
        List<SemanticCommand> entryCommands = CommandSegmentSupport.flatten(top);
        List<StructRecord> structTable = context.structRecords();
        printStructTable(structTable);
        printGlobalSection(entryCommands, entryLocals(context.identifierRecords()), structTable);
        for (FunctionCommandSegment segment : context.getFunctionCommandSegmentContext().snapshot()) {
            printFunctionSection(
                    segment,
                    functionLocals(context.identifierRecords(), segment.getFunction()),
                    structTable
            );
        }
    }

    /**
     * 函数功能：打印命令片段。
     * 输入：
     * - title：String 类型参数。
     * - result：List<SemanticCommand> 类型参数。
     * 输出：无。
     */

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

    /**
     * 函数功能：打印结构体表。
     * 输入：
     * - structTable：List<StructRecord> 类型参数。
     * 输出：无。
     */

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

    /**
     * 函数功能：打印全局命令片段。
     * 输入：
     * - commands：List<SemanticCommand> 类型参数。
     * - locals：IdentifierRecord[] 类型参数。
     * - structTable：List<StructRecord> 类型参数。
     * 输出：无。
     */

    private static void printGlobalSection(
            List<SemanticCommand> commands,
            IdentifierRecord[] locals,
            List<StructRecord> structTable) {
        System.out.println("global segment:");
        printSegment("commands", commands);
        printIdentifierTable("local variables", locals, structTable);
    }

    /**
     * 函数功能：打印函数命令片段。
     * 输入：
     * - segment：FunctionCommandSegment 类型参数。
     * - locals：IdentifierRecord[] 类型参数。
     * - structTable：List<StructRecord> 类型参数。
     * 输出：无。
     */

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

    /**
     * 函数功能：打印标识符表。
     * 输入：
     * - title：String 类型参数。
     * - records：IdentifierRecord[] 类型参数。
     * - structTable：List<StructRecord> 类型参数。
     * 输出：无。
     */

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

    /**
     * 函数功能：获取入口局部变量列表。
     * 输入：
     * - records：IdentifierRecord[] 类型参数。
     * 输出：IdentifierRecord[] 类型数组。
     */

    private static IdentifierRecord[] entryLocals(IdentifierRecord[] records) {
        List<IdentifierRecord> result = new ArrayList<>();
        for (IdentifierRecord record : records) {
            if (record.getOwnerFunction() == null) {
                result.add(record);
            }
        }
        return result.toArray(IdentifierRecord[]::new);
    }

    /**
     * 函数功能：获取函数局部变量列表。
     * 输入：
     * - records：IdentifierRecord[] 类型参数。
     * - function：FunctionRecord 类型参数。
     * 输出：IdentifierRecord[] 类型数组。
     */

    private static IdentifierRecord[] functionLocals(IdentifierRecord[] records, FunctionRecord function) {
        List<IdentifierRecord> result = new ArrayList<>();
        for (IdentifierRecord record : records) {
            if (record.getOwnerFunction() == function) {
                result.add(record);
            }
        }
        return result.toArray(IdentifierRecord[]::new);
    }

    /**
     * 函数功能：处理接受前事件。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：无。
     */

    @Override
    public void beforeAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        CommandNodeRegister top = topCommandNodeRegister(context);
        ShiftReduceCallback.super.beforeAccept(context, production);
        printResult(context, top);
    }

}

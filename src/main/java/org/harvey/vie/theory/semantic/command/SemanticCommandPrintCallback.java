package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.function.FunctionParameter;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.command.node.CommandContext;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * TODO
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
        printGlobalSection(entryCommands, entryLocals(context.identifierRecords()));
        for (FunctionCommandSegment segment : context.getFunctionCommandSegmentContext().snapshot()) {
            printFunctionSection(segment, functionLocals(context.identifierRecords(), segment.getFunction()));
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

    private static void printGlobalSection(List<SemanticCommand> commands, IdentifierRecord[] locals) {
        System.out.println("global segment:");
        printSegment("commands", commands);
        printIdentifierTable("local variables", locals);
    }

    private static void printFunctionSection(FunctionCommandSegment segment, IdentifierRecord[] locals) {
        FunctionRecord function = segment.getFunction();
        System.out.println("function segment:");
        System.out.println("index=" + function.getTableIndex());
        System.out.println("signature=" + formatFunctionSignature(function));
        printSegment("commands", segment.getCommands());
        printIdentifierTable("local variables", locals);
    }

    private static void printIdentifierTable(String title, IdentifierRecord[] records) {
        System.out.println(title + ":");
        if (records.length == 0) {
            System.out.println("<empty>");
            return;
        }
        for (IdentifierRecord record : records) {
            System.out.println(record.displayString());
        }
    }

    private static String formatFunctionSignature(FunctionRecord function) {
        StringJoiner joiner = new StringJoiner(", ");
        for (FunctionParameter parameter : function.getParameters()) {
            joiner.add(formatType(parameter.getTypeNode()) + " " +
                       SourceTokenStringMapping.utf8(parameter.getNameToken()));
        }
        return String.format(
                "%s %s(%s)",
                formatReturnType(function),
                SourceTokenStringMapping.utf8(function.getSignature().getNameToken()),
                joiner
        );
    }

    private static String formatReturnType(FunctionRecord function) {
        ShiftReduceSyntaxTreeNode node = function.getFunctionHeadNode().get(0);
        if (node.isToken()) {
            return SourceTokenStringMapping.utf8(node.toToken().getSource());
        }
        return formatType(node.toHead());
    }

    private static String formatType(HeadNode typeNode) {
        StringJoiner joiner = new StringJoiner(" ");
        appendTypeLexemes(typeNode, joiner);
        String value = joiner.toString().trim();
        return value.isEmpty() ? typeNode.toString() : value;
    }

    private static void appendTypeLexemes(ShiftReduceSyntaxTreeNode node, StringJoiner joiner) {
        if (node.isToken()) {
            joiner.add(SourceTokenStringMapping.utf8(node.toToken().getSource()));
            return;
        }
        for (ShiftReduceSyntaxTreeNode child : node.toHead()) {
            appendTypeLexemes(child, joiner);
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

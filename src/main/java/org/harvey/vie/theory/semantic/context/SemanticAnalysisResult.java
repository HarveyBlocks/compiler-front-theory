package org.harvey.vie.theory.semantic.context;

import org.harvey.vie.theory.semantic.command.FunctionCommandSegment;
import org.harvey.vie.theory.semantic.command.ThreeAddressCodePrinter;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.structure.StructRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Temper
 */
public class SemanticAnalysisResult implements SemanticResult {
    private final IdentifierRecord[] identifierRecords;
    private final List<SemanticCommand> entryCommands;
    private final List<FunctionCommandSegment> functionSegments;
    private final List<StructRecord> structTable;

    public SemanticAnalysisResult(
            List<SemanticCommand> entryCommands,
            List<FunctionCommandSegment> functionSegments,
            List<StructRecord> structTable,
            IdentifierRecord[] identifierRecords) {
        this.entryCommands = List.copyOf(entryCommands);
        this.functionSegments = List.copyOf(functionSegments);
        this.structTable = List.copyOf(structTable);
        this.identifierRecords = identifierRecords.clone();
    }

    public IdentifierRecord[] getIdentifierRecords() {
        return identifierRecords.clone();
    }

    public List<SemanticCommand> getEntryCommands() {
        return List.copyOf(entryCommands);
    }

    public List<String> getCommands() {
        return new ThreeAddressCodePrinter().print(entryCommands);
    }

    public List<FunctionCommandSegment> getFunctionSegments() {
        return List.copyOf(functionSegments);
    }

    public List<StructRecord> getStructTable() {
        return List.copyOf(structTable);
    }

    public List<FunctionRecord> getFunctionTable() {
        List<FunctionRecord> table = new ArrayList<>(functionSegments.size());
        for (FunctionCommandSegment segment : functionSegments) {
            table.add(segment.getFunction());
        }
        return List.copyOf(table);
    }

    public IdentifierRecord[] getEntryLocalVariables() {
        List<IdentifierRecord> result = new ArrayList<>();
        for (IdentifierRecord record : identifierRecords) {
            if (record.getOwnerFunction() == null) {
                result.add(record);
            }
        }
        return result.toArray(IdentifierRecord[]::new);
    }

    public IdentifierRecord[] getFunctionLocalVariables(FunctionRecord function) {
        List<IdentifierRecord> result = new ArrayList<>();
        for (IdentifierRecord record : identifierRecords) {
            if (record.getOwnerFunction() == function) {
                result.add(record);
            }
        }
        return result.toArray(IdentifierRecord[]::new);
    }

    public int commandCount() {
        int total = entryCommands.size();
        for (FunctionCommandSegment segment : functionSegments) {
            total += segment.getCommands().size();
        }
        return total;
    }
}

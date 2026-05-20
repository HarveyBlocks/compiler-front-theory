package org.harvey.vie.theory.semantic.context;

import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;

import java.util.List;

public class SemanticAnalysisResult implements SemanticResult {
    private final List<String> commands;
    private final IdentifierRecord[] identifierRecords;

    public SemanticAnalysisResult(List<String> commands, IdentifierRecord[] identifierRecords) {
        this.commands = List.copyOf(commands);
        this.identifierRecords = identifierRecords.clone();
    }

    public List<String> getCommands() {
        return commands;
    }

    public IdentifierRecord[] getIdentifierRecords() {
        return identifierRecords.clone();
    }
}

package org.harvey.vie.theory.semantic.context;

import lombok.Getter;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;

import java.util.List;

/**
 * @author Temper
 */
public class SemanticAnalysisResult implements SemanticResult {
    private final IdentifierRecord[] identifierRecords;
    @Getter
    private final List<String> commands;

    public SemanticAnalysisResult(List<String> commands, IdentifierRecord[] identifierRecords) {
        this.commands = List.copyOf(commands);
        this.identifierRecords = identifierRecords.clone();
    }

    public IdentifierRecord[] getIdentifierRecords() {
        return identifierRecords.clone();
    }
}

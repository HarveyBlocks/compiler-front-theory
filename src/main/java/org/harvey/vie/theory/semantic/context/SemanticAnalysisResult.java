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

    /**
     * 函数功能：创建 SemanticAnalysisResult 对象。
     * 输入：
     * - entryCommands：List<SemanticCommand> 类型参数。
     * - functionSegments：List<FunctionCommandSegment> 类型参数。
     * - structTable：List<StructRecord> 类型参数。
     * - identifierRecords：IdentifierRecord[] 类型参数。
     * 输出：无。
     */

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

    /**
     * 函数功能：获取标识符记录列表。
     * 输入：
     * - 无。
     * 输出：IdentifierRecord[] 类型数组。
     */

    public IdentifierRecord[] getIdentifierRecords() {
        return identifierRecords.clone();
    }

    /**
     * 函数功能：获取入口命令列表。
     * 输入：
     * - 无。
     * 输出：List<SemanticCommand> 类型集合或迭代结果。
     */

    public List<SemanticCommand> getEntryCommands() {
        return List.copyOf(entryCommands);
    }

    /**
     * 函数功能：获取命令列表。
     * 输入：
     * - 无。
     * 输出：List<String> 类型集合或迭代结果。
     */

    public List<String> getCommands() {
        return new ThreeAddressCodePrinter().print(entryCommands);
    }

    /**
     * 函数功能：获取函数命令片段列表。
     * 输入：
     * - 无。
     * 输出：List<FunctionCommandSegment> 类型集合或迭代结果。
     */

    public List<FunctionCommandSegment> getFunctionSegments() {
        return List.copyOf(functionSegments);
    }

    /**
     * 函数功能：获取结构体表。
     * 输入：
     * - 无。
     * 输出：List<StructRecord> 类型集合或迭代结果。
     */

    public List<StructRecord> getStructTable() {
        return List.copyOf(structTable);
    }

    /**
     * 函数功能：获取函数表。
     * 输入：
     * - 无。
     * 输出：List<FunctionRecord> 类型集合或迭代结果。
     */

    public List<FunctionRecord> getFunctionTable() {
        List<FunctionRecord> table = new ArrayList<>(functionSegments.size());
        for (FunctionCommandSegment segment : functionSegments) {
            table.add(segment.getFunction());
        }
        return List.copyOf(table);
    }

    /**
     * 函数功能：获取入口局部变量列表。
     * 输入：
     * - 无。
     * 输出：IdentifierRecord[] 类型数组。
     */

    public IdentifierRecord[] getEntryLocalVariables() {
        List<IdentifierRecord> result = new ArrayList<>();
        for (IdentifierRecord record : identifierRecords) {
            if (record.getOwnerFunction() == null) {
                result.add(record);
            }
        }
        return result.toArray(IdentifierRecord[]::new);
    }

    /**
     * 函数功能：获取函数局部变量映射。
     * 输入：
     * - function：FunctionRecord 类型参数。
     * 输出：IdentifierRecord[] 类型数组。
     */

    public IdentifierRecord[] getFunctionLocalVariables(FunctionRecord function) {
        List<IdentifierRecord> result = new ArrayList<>();
        for (IdentifierRecord record : identifierRecords) {
            if (record.getOwnerFunction() == function) {
                result.add(record);
            }
        }
        return result.toArray(IdentifierRecord[]::new);
    }

    /**
     * 函数功能：获取命令总数。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    public int commandCount() {
        int total = entryCommands.size();
        for (FunctionCommandSegment segment : functionSegments) {
            total += segment.getCommands().size();
        }
        return total;
    }
}

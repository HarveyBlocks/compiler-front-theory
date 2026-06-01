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
 * 讲解主线第 9 站：语义分析最终结果，同时保存入口命令段、函数命令段、结构体表和符号表。
 * <p>
 * 中间代码主线从 {@link org.harvey.vie.theory.semantic.command.SemanticResultCallback} 进入这里：
 * {@code entryCommands} 是程序入口段线性命令；
 * {@code functionSegments} 是 {@link FunctionCommandSegment} 列表，每个函数定义单独保存自己的命令。
 * <p>
 * {@link #getCommands()} 是测试和报告最常用的入口，它调用
 * {@link ThreeAddressCodePrinter#print(List)} 把入口段命令对象转成文本。函数段命令则通过
 * {@link #getFunctionSegments()} 取出后再用同一个 printer 打印。
 * <p>
 * 主线下一站：{@link ThreeAddressCodePrinter}。下一站会讲命令对象如何真正变成报告里的 Commands 文本。
 *
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

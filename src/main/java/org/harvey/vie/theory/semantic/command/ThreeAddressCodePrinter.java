package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * 把 demo 语义命令整理成更容易阅读的展示文本。
 *
 * @author Temper
 */
public class ThreeAddressCodePrinter {
    private final List<String> lines = new ArrayList<>();
/**
 * 函数功能：打印语义分析结果。
 * 输入：
 * - commands：List<SemanticCommand> 类型参数。
 * 输出：List<String> 类型集合或迭代结果。
 */

    public List<String> print(List<SemanticCommand> commands) {
        lines.clear();
        for (SemanticCommand command : commands) {
            lines.add(command.toString());
        }
        return List.copyOf(lines);
    }
/**
 * 函数功能：获取三地址代码行列表。
 * 输入：
 * - 无。
 * 输出：List<String> 类型集合或迭代结果。
 */

    public List<String> lines() {
        return List.copyOf(lines);
    }
}


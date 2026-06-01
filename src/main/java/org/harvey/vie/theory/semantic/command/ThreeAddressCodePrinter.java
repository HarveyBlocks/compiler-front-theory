package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * 讲解主线第 9 站：把 {@link SemanticCommand} 列表打印成课堂可读的中间代码文本。
 * <p>
 * 这个名字说明项目展示的是“三地址码风格”的中间代码，但实现上并没有再把命令拆成标准四元组结构；
 * 它直接调用每个 {@link SemanticCommand#toString()}，输出一行一条文本命令。
 * 所以本项目的实际中间表示可以准确表述为：“栈式求值命令 + 标签跳转命令”的三地址码/四元式风格文本 IR，
 * 不是 JVM 字节码。
 * <p>
 * 讲完本类可跳到测试报告入口
 * {@link org.harvey.vie.theory.demo.program.ProgramSyntaxTestRunner}，
 * 或测试断言 {@link org.harvey.vie.theory.AppTest}。
 *
 * @author Temper
 */
public class ThreeAddressCodePrinter {
    private final List<String> lines = new ArrayList<>();

    public List<String> print(List<SemanticCommand> commands) {
        lines.clear();
        for (SemanticCommand command : commands) {
            lines.add(command.toString());
        }
        return List.copyOf(lines);
    }

    public List<String> lines() {
        return List.copyOf(lines);
    }
}

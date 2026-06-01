package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * 讲解主线第 10 站：把 {@link SemanticCommand} 列表打印成课堂可读的 Commands 文本。
 * <p>
 * 编译原理里的中间代码可以有多种形式：三地址码、四元式、三元式、P-code、字节码等。
 * 本项目不是 JVM 字节码，也没有把命令拆成标准四元组对象；它直接调用每个
 * {@link SemanticCommand#toString()}，输出一行一条文本 IR。
 * <p>
 * 因此最准确的说法是：本项目实现的是“栈式求值命令 + 标签跳转命令”的三地址码/四元式风格中间表示。
 * 例如先用 {@code load_st_*} 把操作数压到语义栈，再用 {@code st_plus_int32} 这类命令完成运算，
 * 控制流则用 {@code if_goto}/{@code ifn_goto}/{@code goto} 和标签下标表达。
 * <p>
 * 主线下一站：{@link org.harvey.vie.theory.demo.program.ProgramSyntaxTestRunner}。
 * 这是讲解收尾站，用它查看 text1/text2/text3 等用例生成的 Commands 报告；如果要看自动断言，
 * 再跳到 {@link org.harvey.vie.theory.AppTest}。
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

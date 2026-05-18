package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * 把 demo 语义命令整理成更容易阅读的展示文本。
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

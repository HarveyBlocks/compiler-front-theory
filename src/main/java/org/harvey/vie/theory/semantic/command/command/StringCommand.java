package org.harvey.vie.theory.semantic.command.command;

import lombok.AllArgsConstructor;

/**
 * TODO 仅用作测试和demo
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:26
 */
@AllArgsConstructor
public class StringCommand implements SemanticCommand {
    private final String value;

    @Override
    public String toString() {
        return value;
    }
}

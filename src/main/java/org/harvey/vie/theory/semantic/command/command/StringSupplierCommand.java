package org.harvey.vie.theory.semantic.command.command;

import lombok.AllArgsConstructor;

import java.util.function.Supplier;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:24
 */
@AllArgsConstructor
public class StringSupplierCommand implements SemanticCommand {
    private final Supplier<String> stringSupplier;

    @Override
    public String toString() {
        return stringSupplier.get();
    }
}

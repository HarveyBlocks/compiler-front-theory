package org.harvey.vie.theory.semantic.identifier.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 01:05
 */
@AllArgsConstructor
@Getter
public class IdentifierRecord {
    private final int no;
    private final HeadNode type;
    private final byte[] lexeme;
    private final boolean initialized;

    @Override
    public String toString() {
        return "[" + no + "] " + type.stream().map(Objects::toString).collect(Collectors.joining()) + " " + new String(lexeme) + " " + (initialized ? "" : "non-") + "initialized";
    }
}

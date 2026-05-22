package org.harvey.vie.theory.semantic.identifier.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.value.ConstantValue;

import java.nio.charset.StandardCharsets;
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
    private final int offset;
    private final HeadNode type;
    private final SemanticType declaredType;
    private final byte[] lexeme;
    private final boolean initialized;
    @Setter
    private ConstantValue constantValue;

    @Override
    public String toString() {
        return "record=" + no +
               " offset=" + offset +
               " type=" + type.stream().map(Objects::toString).collect(Collectors.joining()) +
               " name=" + new String(lexeme, StandardCharsets.UTF_8) +
               " initialized=" + initialized +
               " constant=" + (constantValue == null ? "<none>" : constantValue.literalText());
    }
}

package org.harvey.vie.theory.semantic.identifier.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.value.ConstantValue;

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
    private final FunctionRecord ownerFunction;

    @Setter
    private ConstantValue constantValue;
/**
 * 函数功能：返回当前对象的字符串表示。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    @Override
    public String toString() {
        return "record=" + no +
               " offset=" + offset +
               " type=" + type.stream().map(Objects::toString).collect(Collectors.joining()) +
               " name=" + SourceTokenStringMapping.utf8(lexeme) +
               " initialized=" + initialized +
               " constant=" + (constantValue == null ? "<none>" : constantValue.toString());
    }
/**
 * 函数功能：获取记录的显示字符串。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    public String displayString() {
        return toString();
    }

}

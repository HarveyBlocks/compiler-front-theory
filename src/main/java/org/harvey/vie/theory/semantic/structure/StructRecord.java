package org.harvey.vie.theory.semantic.structure;

import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;

import java.util.List;

/**
 * Declared struct type with ordered field layout.
 *
 * @author Temper
 */
@Getter
public class StructRecord {
    private final int tableIndex;
    private final SourceToken nameToken;
    private final IdentifierKey nameKey;
    private final List<StructField> fields;
    private final HeadNode declarationNode;
/**
 * 函数功能：创建 StructRecord 对象。
 * 输入：
 * - tableIndex：int 类型参数。
 * - nameToken：SourceToken 类型参数。
 * - fields：List<StructField> 类型参数。
 * - declarationNode：HeadNode 类型参数。
 * 输出：无。
 */

    public StructRecord(int tableIndex, SourceToken nameToken, List<StructField> fields, HeadNode declarationNode) {
        this.tableIndex = tableIndex;
        this.nameToken = nameToken;
        this.nameKey = IdentifierKey.generate(nameToken);
        this.fields = List.copyOf(fields);
        this.declarationNode = declarationNode;
    }
/**
 * 函数功能：获取指定名称的字段。
 * 输入：
 * - token：SourceToken 类型参数。
 * 输出：StructField 类型返回值。
 */

    public StructField field(SourceToken token) {
        for (StructField field : fields) {
            if (field.isNamed(token)) {
                return field;
            }
        }
        return null;
    }
/**
 * 函数功能：获取用于显示的名称。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    public String displayName() {
        return SourceTokenStringMapping.utf8(nameToken);
    }
}

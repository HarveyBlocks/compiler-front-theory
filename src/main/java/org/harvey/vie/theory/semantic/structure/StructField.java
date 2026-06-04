package org.harvey.vie.theory.semantic.structure;

import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.type.SemanticType;

/**
 * Field metadata for a declared struct type.
 *
 * @author Temper
 */
@Getter
public class StructField {
    private final SourceToken nameToken;
    private final IdentifierKey nameKey;
    private final SemanticType type;
    private final int offset;
/**
 * 函数功能：创建 StructField 对象。
 * 输入：
 * - nameToken：SourceToken 类型参数。
 * - type：SemanticType 类型参数。
 * - offset：int 类型参数。
 * 输出：无。
 */

    public StructField(SourceToken nameToken, SemanticType type, int offset) {
        this.nameToken = nameToken;
        this.nameKey = IdentifierKey.generate(nameToken);
        this.type = type;
        this.offset = offset;
    }
/**
 * 函数功能：判断类型是否为命名类型。
 * 输入：
 * - token：SourceToken 类型参数。
 * 输出：判断结果布尔值。
 */

    public boolean isNamed(SourceToken token) {
        return nameKey.equals(IdentifierKey.generate(token));
    }
}

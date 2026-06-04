package org.harvey.vie.theory.source.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.io.ToBytes;

/**
 * Implementation of {@link SourceCharacter} that encapsulates a Unicode code point.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 10:37
 */
@Getter
@AllArgsConstructor
public class CodePointCharacter implements SourceCharacter {
    private final int codePoint;

    /**
     * 函数功能：将 Unicode 码点源字符转换为字节数组表示。
     * 输入：
     * - 无。
     * 输出：Unicode 码点对应的字节数组。
     */
    @Override
    public byte[] toCharacter() {
        return ToBytes.fromInt(codePoint);
    }

}

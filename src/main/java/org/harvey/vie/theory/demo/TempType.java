package org.harvey.vie.theory.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.harvey.vie.theory.io.Loaders;
import org.harvey.vie.theory.io.Storages;
import org.harvey.vie.theory.lexical.analysis.token.AbstractTokenType;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TokenType不做限制, 就是因为, 一个TokenType可以代表一类TokenSource.
 * 然后如何知道source对应的是哪个Token呢?
 * TokenType怎么和TerminalSymbol匹配呢?
 * 可以用严格匹配, 也可以用正则之类的.
 * 因此可以自己实现, 来实现更复杂的匹配功能
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:19
 */
@AllArgsConstructor
public class TempType extends AbstractTokenType {
    private final int id;
    @Getter
    private final int priority;
    private final String s;

    /**
     * 函数功能：获取临时词法单元类型的显示名称。
     * 输入：
     * - 无。
     * 输出：词法单元类型提示字符串。
     */
    @Override
    public @NonNull String hint() {
        return s;
    }

    /**
     * 函数功能：将临时词法单元类型编号写入输出流。
     * 输入：
     * - os：接收序列化数据的输出流。
     * 输出：写入的字节数。
     */
    @Override
    public int store(OutputStream os) throws IOException {
        return Storages.storeInteger(os, id);
    }

    public static class Loader implements TokenType.Loader<TempType> {
        private final Map<Integer, TempType> map;

        /**
         * 函数功能：创建临时词法单元类型加载器。
         * 输入：
         * - types：用于建立编号映射的临时词法单元类型数组。
         * 输出：无。
         */
        public Loader(TempType... types) {
            this.map = Arrays.stream(types).collect(Collectors.toMap(t -> t.id, t -> t));
        }

        /**
         * 函数功能：从输入流读取编号并还原临时词法单元类型。
         * 输入：
         * - is：包含序列化编号的输入流。
         * 输出：编号对应的 TempType；未知编号返回 null。
         */
        @Override
        public TempType load(InputStream is) throws IOException {
            int id = Loaders.loadInteger(is);
            return map.get(id);
        }
    }
}

package org.harvey.vie.theory.syntax.grammar.symbol;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.harvey.vie.theory.io.Loaders;
import org.harvey.vie.theory.io.Storages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:46
 */
@Getter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class HeadDefineSymbolImpl extends AbstractTagGrammarSymbol implements HeadDefineSymbol {
    private final String name;

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

    @Override
    public String toString() {
        return name;
    }

    /**
     * 函数功能：将对象写入输出流。
     * 输入：
     * - os：OutputStream 类型参数。
     * 输出：整数结果。
     */

    @Override
    public int store(OutputStream os) throws IOException {
        byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
        int len = Storages.storeInteger(os, bytes.length);
        os.write(bytes);
        return len + bytes.length;
    }

    public static class Loader implements HeadDefineSymbol.Loader<HeadDefineSymbolImpl> {
        /**
         * 函数功能：从输入流加载对象。
         * 输入：
         * - is：InputStream 类型参数。
         * 输出：HeadDefineSymbolImpl 类型返回值。
         */
        @Override
        public HeadDefineSymbolImpl load(InputStream is) throws IOException {
            int length = Loaders.loadInteger(is);
            byte[] bytes = Loaders.loadBytes(is, length);
            return new HeadDefineSymbolImpl(new String(bytes, StandardCharsets.UTF_8));
        }
    }

}

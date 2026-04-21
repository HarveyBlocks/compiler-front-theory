package org.harvey.vie.theory.syntax.grammar.symbol;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.harvey.vie.theory.io.ILoader;
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
@EqualsAndHashCode
@AllArgsConstructor
public class HeadDefineSymbolImpl implements HeadDefineSymbol {
    private final String name;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int store(OutputStream os) throws IOException {
        byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
        int len = Storages.storeInteger(os, bytes.length);
        os.write(bytes);
        return len + bytes.length;
    }

    public static class Loader implements HeadDefineSymbol.Loader<HeadDefineSymbolImpl> {
        @Override
        public HeadDefineSymbolImpl load(InputStream is) throws IOException {
            int length = Loaders.loadInteger(is);
            byte[] bytes = Loaders.loadBytes(is, length);
            return new HeadDefineSymbolImpl(new String(bytes, StandardCharsets.UTF_8));
        }
    }
}

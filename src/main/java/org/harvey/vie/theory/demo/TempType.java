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

    @Override
    public @NonNull String hint() {
        return s;
    }

    @Override
    public int store(OutputStream os) throws IOException {
        return Storages.storeInteger(os, id);
    }

    public static class Loader implements TokenType.Loader<TempType> {
        private final Map<Integer, TempType> map;

        public Loader(TempType... types) {
            this.map = Arrays.stream(types).collect(Collectors.toMap(t -> t.id, t -> t));
        }

        /**
         * @throws IOException null for unknown token
         */
        @Override
        public TempType load(InputStream is) throws IOException {
            int id = Loaders.loadInteger(is);
            return map.get(id);
        }
    }
}

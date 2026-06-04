package org.harvey.vie.theory.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for components that can deserialize objects of type {@code T}
 * from an {@link InputStream}.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 14:02
 */
public interface ILoader<T> {
    /**
     * 函数功能：从输入流加载对象。
     * 输入：
     * - is：提供序列化数据的输入流。
     * 输出：加载得到的对象。
     */
    T load(InputStream is) throws IOException;
}

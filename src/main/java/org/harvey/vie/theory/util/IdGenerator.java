package org.harvey.vie.theory.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A utility class for generating unique integer identifiers in a thread-safe manner.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 21:58
 */
public class IdGenerator {
    private final AtomicInteger generator;

    /**
     * 函数功能：创建从零开始的编号生成器。
     * 输入：
     * - 无。
     * 输出：无。
     */
    public IdGenerator() {this.generator = new AtomicInteger();}

    /**
     * 函数功能：创建从指定初始值开始的编号生成器。
     * 输入：
     * - initialValue：编号生成器的初始值。
     * 输出：无。
     */
    public IdGenerator(int initialValue) {this.generator = new AtomicInteger(initialValue);}

    /**
     * 函数功能：获取下一个编号并推进生成器。
     * 输入：
     * - 无。
     * 输出：当前生成的编号整数。
     */
    public int next() {
        return generator.getAndIncrement();
    }

    /**
     * 函数功能：获取当前编号值。
     * 输入：
     * - 无。
     * 输出：当前编号整数。
     */
    public int current() {
        return generator.get();
    }
}

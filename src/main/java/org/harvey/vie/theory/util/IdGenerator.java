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

    public IdGenerator() {this.generator = new AtomicInteger();}
    public IdGenerator(int initialValue) {this.generator = new AtomicInteger(initialValue);}

    public int next() {
        return generator.getAndIncrement();
    }
}

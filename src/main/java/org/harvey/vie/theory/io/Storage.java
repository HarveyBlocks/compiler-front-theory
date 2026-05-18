package org.harvey.vie.theory.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface representing a data structure that can be serialized (stored)
 * to an {@link OutputStream}.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 13:48
 */
public interface Storage {
    int store(OutputStream os) throws IOException;
}

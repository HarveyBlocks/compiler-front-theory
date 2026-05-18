package org.harvey.vie.theory.semantic.callback;

import org.harvey.vie.theory.util.SimpleCollection;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-08 13:00
 */
public  interface CallbackRegister<T extends SemanticCallback> extends SimpleCollection<T> {
    void add(T callable);
}
